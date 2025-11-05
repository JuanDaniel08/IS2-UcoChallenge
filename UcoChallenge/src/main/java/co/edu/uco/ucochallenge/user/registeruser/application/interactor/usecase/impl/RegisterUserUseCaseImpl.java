package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.ValidationResultVO;
import org.springframework.stereotype.Service;
import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.CityRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.CatalogUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;
import co.edu.uco.ucochallenge.user.registeruser.application.service.NotificationService;
import co.edu.uco.ucochallenge.user.registeruser.application.service.VerificationTokenService;
import java.time.Duration;
import java.util.UUID;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
	
	private UserRepository repository;
	private CityRepository cityRepository;
	private CatalogUseCase catalogUseCase;
	private NotificationService notificationService;
	private VerificationTokenService tokenService;
	
	public RegisterUserUseCaseImpl(UserRepository repository, 
			CityRepository cityRepository,
			CatalogUseCase catalogUseCase,
			NotificationService notificationService,
			VerificationTokenService tokenService) {
		this.repository = repository;
		this.cityRepository = cityRepository;
		this.catalogUseCase = catalogUseCase;
		this.notificationService = notificationService;
		this.tokenService = tokenService;
	}


	@Override
	public Void execute(final RegisterUserDomain domain) {
		var resultadoFinal = new ValidationResultVO();

		// 2. Si no existe un id, generar un nuevo id (tomamos siempre uno nuevo para registro)
		final UUID nuevoUsuarioId = UUID.randomUUID();
		
		// Preparar idTypeEntity para uso en validaciones y creación
		var idTypeEntity = new IdTypeEntity.Builder().id(domain.getIdType()).build();

		try {
			// NOTA: La validación de ID duplicado se omite para registros nuevos porque
			// siempre se genera un UUID único. Esta validación es útil para actualizaciones.
			// Si en el futuro se necesita validar IDs específicos, usar: usuarioConMismoId.validate(nuevoUsuarioId)

			// 3. No puede existir un usuario con el mismo idType y IdNumber
			repository.findByIdTypeAndIdNumber(idTypeEntity, domain.getIdNumber())
					.ifPresent(existing -> {
						resultadoFinal.agregarMensaje(catalogUseCase.getMessage("validation.user.idtype.idnumber.duplicate"));
						// Regla 5: Notificar al admin sobre la novedad
						notificationService.notifyAdmin(catalogUseCase.getMessage("notification.identification.duplicate.admin", 
								domain.getIdType().toString(), domain.getIdNumber()));
					});

			// 4. No puede existir un usuario con el mismo email
			repository.findByEmail(domain.getEmail())
					.ifPresent(existing -> {
						resultadoFinal.agregarMensaje(catalogUseCase.getMessage("validation.user.email.duplicate"));
						// Regla 4: Notificar al dueño del email existente
						notificationService.notifyOwnerEmail(existing.getEmail(), 
								catalogUseCase.getMessage("notification.email.duplicate.owner"));
					});

			// 6. No puede existir 2 usuarios con el mismo mobile number
			repository.findByMobileNumber(domain.getMobileNumber())
					.ifPresent(existing -> {
						resultadoFinal.agregarMensaje(catalogUseCase.getMessage("validation.user.mobile.duplicate"));
						// Regla 7: Informar al dueño del número ya existente por SMS
						if (existing.getMobileNumber() != null) {
							notificationService.notifyOwnerSms(existing.getMobileNumber(), 
									catalogUseCase.getMessage("notification.mobile.duplicate.owner.sms"));
						}
					});
		} catch (Exception e) {
			resultadoFinal.agregarMensaje(catalogUseCase.getMessage("validation.error.unexpected", e.getMessage()));
		}

		// Si hay errores de validación no persistimos
		if (!resultadoFinal.isValidacionCorrecta()) {
			return Void.returnVoid();
		}

		// Mapear Domain -> Entity y persistir
		// Cargar CityEntity completo desde la BD para tener las relaciones con State y Country
		CityEntity homeCity = cityRepository.findById(domain.getHomeCity())
				.orElseThrow(() -> new IllegalArgumentException(catalogUseCase.getMessage("validation.city.not.exists")));
		
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
		
		// Reglas 9-12: Confirmar correo por minutos y enviar estrategias (email/sms)
		// Regla 9-10: Generar token de email y enviarlo (válido por 10 minutos)
		var emailToken = tokenService.generateEmailToken(savedUser.getId(), Duration.ofMinutes(10));
		notificationService.sendEmailVerification(savedUser.getEmail(), emailToken);
		
		// Regla 11-12: Si tiene número móvil, generar token SMS y enviarlo (válido por 10 minutos)
		if (savedUser.getMobileNumber() != null && !savedUser.getMobileNumber().isBlank()) {
			var smsToken = tokenService.generateSmsToken(savedUser.getId(), Duration.ofMinutes(10));
			notificationService.sendSmsVerification(savedUser.getMobileNumber(), smsToken);
		}
		
		return Void.returnVoid();
	}

}
