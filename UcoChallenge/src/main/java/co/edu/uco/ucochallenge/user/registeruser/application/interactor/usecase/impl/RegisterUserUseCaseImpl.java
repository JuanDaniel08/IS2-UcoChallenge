package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;


import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.application.service.RedisParameterService;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.CityRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.service.NotificationService;
import co.edu.uco.ucochallenge.user.registeruser.application.service.VerificationTokenService;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

	private final UserRepository repository;
	private final CityRepository cityRepository;
	private final RedisParameterService redisParameterService;
	private final NotificationService notificationService;
	private final VerificationTokenService tokenService;

	public RegisterUserUseCaseImpl(UserRepository repository,
								   CityRepository cityRepository,
								   RedisParameterService redisParameterService,
								   NotificationService notificationService,
								   VerificationTokenService tokenService) {
		this.repository = repository;
		this.cityRepository = cityRepository;
		this.redisParameterService = redisParameterService;
		this.notificationService = notificationService;
		this.tokenService = tokenService;
	}

	@Override
	public Void execute(final RegisterUserDomain domain) {

		// 2. Generar un nuevo ID
		final UUID nuevoUsuarioId = UUID.randomUUID();

		// Preparar idTypeEntity para uso en validaciones y creación
		var idTypeEntity = new IdTypeEntity.Builder().id(domain.getIdType()).build();

		// 3. No puede existir un usuario con el mismo idType y IdNumber
		if (repository.findByIdTypeAndIdNumber(idTypeEntity, domain.getIdNumber()).isPresent()) {
			String errorMsg = redisParameterService.getErrorMessage("validation.user.idtype.idnumber.duplicate");
			String adminMsg = redisParameterService.getErrorMessage("notification.identification.duplicate.admin");
			notificationService.notifyAdmin(adminMsg + " - ID Type: " + domain.getIdType() + ", ID Number: " + domain.getIdNumber());
			throw new IllegalArgumentException(errorMsg);
		}

		// 4. No puede existir un usuario con el mismo email
		if (repository.findByEmail(domain.getEmail()).isPresent()) {
			var existing = repository.findByEmail(domain.getEmail()).get();
			String errorMsg = redisParameterService.getErrorMessage("validation.user.email.duplicate");
			String ownerMsg = redisParameterService.getErrorMessage("notification.email.duplicate.owner");
			notificationService.notifyOwnerEmail(existing.getEmail(), ownerMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		// 6. No puede existir 2 usuarios con el mismo mobile number
		if (domain.getMobileNumber() != null && !domain.getMobileNumber().isBlank()) {
			if (repository.findByMobileNumber(domain.getMobileNumber()).isPresent()) {
				var existing = repository.findByMobileNumber(domain.getMobileNumber()).get();
				String errorMsg = redisParameterService.getErrorMessage("validation.user.mobile.duplicate");
				String ownerSmsMsg = redisParameterService.getErrorMessage("notification.mobile.duplicate.owner.sms");
				if (existing.getMobileNumber() != null) {
					notificationService.notifyOwnerSms(existing.getMobileNumber(), ownerSmsMsg);
				}
				throw new IllegalArgumentException(errorMsg);
			}
		}

		// Mapear Domain -> Entity y persistir
		// Cargar CityEntity completo desde la BD para tener las relaciones con State y Country
		CityEntity homeCity = cityRepository.findById(domain.getHomeCity())
				.orElseThrow(() -> {
					String errorMsg = redisParameterService.getErrorMessage("validation.city.not.exists");
					return new IllegalArgumentException(errorMsg);
				});

		var userEntity = new UserEntity.Builder()
				.id(nuevoUsuarioId)
				.idType(idTypeEntity)
				.idNumber(domain.getIdNumber())
				.firstName(domain.getFirstName())
				.secondName(domain.getSecondNamer())
				.firstSurname(domain.getFirstSurname())
				.secondSurname(domain.getSecondSurname())
				.homeCity(homeCity)
				.email(domain.getEmail())
				.mobileNumber(domain.getMobileNumber())
				.build();

		UserEntity savedUser = repository.save(userEntity);

		// Obtener timeout de Redis
		String timeoutStr = redisParameterService.getParameter("app.email_verification_timeout");
		int timeout = timeoutStr != null ? Integer.parseInt(timeoutStr) : 10;

		// Reglas 9-12: Confirmar correo por minutos y enviar estrategias (email/sms)
		// Regla 9-10: Generar token de email y enviarlo (válido por X minutos)
		var emailToken = tokenService.generateEmailToken(savedUser.getId(), Duration.ofMinutes(timeout));
		String emailVerificationMsg = redisParameterService.getErrorMessage("notification.email.verification.sent");
		notificationService.sendEmailVerification(savedUser.getEmail(), emailToken, emailVerificationMsg);

		// Regla 11-12: Si tiene número móvil, generar token SMS y enviarlo (válido por X minutos)
		if (savedUser.getMobileNumber() != null && !savedUser.getMobileNumber().isBlank()) {
			var smsToken = tokenService.generateSmsToken(savedUser.getId(), Duration.ofMinutes(timeout));
			String smsVerificationMsg = redisParameterService.getErrorMessage("notification.sms.verification.sent");
			notificationService.sendSmsVerification(savedUser.getMobileNumber(), smsToken, smsVerificationMsg);
		}

		return Void.returnVoid();
	}
}
