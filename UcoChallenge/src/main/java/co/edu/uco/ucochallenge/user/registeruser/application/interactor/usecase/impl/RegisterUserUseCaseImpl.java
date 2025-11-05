package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.ValidationResultVO;
import org.springframework.stereotype.Service;
import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.CityRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
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
	private UsuarioConMismoId usuarioConMismoId;
	private NotificationService notificationService;
	private VerificationTokenService tokenService;
	
	public RegisterUserUseCaseImpl(UserRepository repository, 
			CityRepository cityRepository,
			NotificationService notificationService,
			VerificationTokenService tokenService) {
		this.repository = repository;
		this.cityRepository = cityRepository;
		this.usuarioConMismoId = new UsuarioConMismoId(repository);
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
			// 1. Un user con el mismo ID (seguir la lógica de UsuarioConMismoId)
			resultadoFinal.agregarMensajes(usuarioConMismoId.validate(nuevoUsuarioId).getMensajes());

			// 3. No puede existir un usuario con el mismo idType y IdNumber
			repository.findByIdTypeAndIdNumber(idTypeEntity, domain.getIdNumber())
					.ifPresent(existing -> {
						resultadoFinal.agregarMensaje("Ya existe un usuario con el mismo tipo y número de identificación");
						// Regla 5: Notificar al admin sobre la novedad
						notificationService.notifyAdmin("Doble identificación detectada: tipo=" + domain.getIdType() + 
								", número=" + domain.getIdNumber());
					});

			// 4. No puede existir un usuario con el mismo email
			repository.findByEmail(domain.getEmail())
					.ifPresent(existing -> {
						resultadoFinal.agregarMensaje("Ya existe un usuario con el mismo email");
						// Regla 4: Notificar al dueño del email existente
						notificationService.notifyOwnerEmail(existing.getEmail(), 
								"Se intentó registrar otro usuario con tu email.");
					});

			// 6. No puede existir 2 usuarios con el mismo mobile number
			repository.findByMobileNumber(domain.getMobileNumber())
					.ifPresent(existing -> {
						resultadoFinal.agregarMensaje("Ya existe un usuario con el mismo número móvil");
						// Regla 7: Informar al dueño del número ya existente por SMS
						if (existing.getMobileNumber() != null) {
							notificationService.notifyOwnerSms(existing.getMobileNumber(), 
									"Se intentó registrar otro usuario con tu número móvil.");
						}
					});
		} catch (Exception e) {
			resultadoFinal.agregarMensaje("Error inesperado durante la validación: " + e.getMessage());
		}

		// Si hay errores de validación no persistimos
		if (!resultadoFinal.isValidacionCorrecta()) {
			return Void.returnVoid();
		}

		// Mapear Domain -> Entity y persistir
		// Cargar CityEntity completo desde la BD para tener las relaciones con State y Country
		CityEntity homeCity = cityRepository.findById(domain.getHomeCity())
				.orElseThrow(() -> new IllegalArgumentException("La ciudad especificada no existe"));
		
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
