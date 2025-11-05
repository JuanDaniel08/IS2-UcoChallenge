package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.ValidationResultVO;
import org.springframework.stereotype.Service;
import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.RegisterUserUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.domain.RegisterUserDomain;
import java.util.UUID;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
	
	private UserRepository repository;
	private UsuarioConMismoId usuarioConMismoId;
	
	public RegisterUserUseCaseImpl(UserRepository repository) {
		this.repository = repository;
		this.usuarioConMismoId = new UsuarioConMismoId(repository);
	}


	@Override
	public Void execute(final RegisterUserDomain domain) {
		var resultadoFinal = new ValidationResultVO();

		// 2. Si no existe un id, generar un nuevo id (tomamos siempre uno nuevo para registro)
		final UUID nuevoUsuarioId = UUID.randomUUID();

		try {
			// 1. Un user con el mismo ID (seguir la lógica de UsuarioConMismoId)
			resultadoFinal.agregarMensajes(usuarioConMismoId.validate(nuevoUsuarioId).getMensajes());

			// 3. No puede existir un usuario con el mismo idType y IdNumber
			var idTypeEntity = new IdTypeEntity.Builder().id(domain.getIdType()).build();
			repository.findByIdTypeAndIdNumber(idTypeEntity, domain.getIdNumber())
					.ifPresent(u -> resultadoFinal.agregarMensaje("Ya existe un usuario con el mismo tipo y número de identificación"));

			// 6. No puede existir 2 usuarios con el mismo mobile number
			repository.findByMobileNumber(domain.getMobileNumber())
					.ifPresent(u -> resultadoFinal.agregarMensaje("Ya existe un usuario con el mismo número móvil"));

			// 4,5,7,9,10,11,12 -> Acciones informativas/estrategias (se registran como pendientes)
			// TODO: Informar al admin sobre la novedad
			// TODO: Informar a quien ejecuta la transaccion
			// TODO: Informar al dueño del numero ya existente por sms
			// TODO: Confirmar correo por minutos y enviar estrategias (email/sms)
		} catch (Exception e) {
			resultadoFinal.agregarMensaje("Error inesperado durante la validación: " + e.getMessage());
		}

		// Si hay errores de validación no persistimos
		if (!resultadoFinal.isValidacionCorrecta()) {
			return Void.returnVoid();
		}

		// Mapear Domain -> Entity y persistir
		var homeCity = new CityEntity.Builder().id(domain.getHomeCity()).build();
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

		repository.save(userEntity);
		return Void.returnVoid();
	}

}
