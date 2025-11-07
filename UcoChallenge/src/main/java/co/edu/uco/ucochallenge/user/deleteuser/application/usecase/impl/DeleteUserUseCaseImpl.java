package co.edu.uco.ucochallenge.user.deleteuser.application.usecase.impl;

import org.springframework.stereotype.Service;
import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;
import co.edu.uco.ucochallenge.user.deleteuser.application.mapper.DeleteUserMapper;
import co.edu.uco.ucochallenge.user.deleteuser.application.usecase.DeleteUserUseCase;
import co.edu.uco.ucochallenge.user.deleteuser.application.validator.DeleteUserInputValidator;
import co.edu.uco.ucochallenge.user.deleteuser.usecase.domain.DeleteUserDomain;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.crosscuting.exception.UcoChallengeExceptionFactory;


@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DeleteUserInputDTO input) {
        // 1️⃣ Validar entrada
        DeleteUserInputValidator.validate(input);

        // 2️⃣ Mapear a dominio
        DeleteUserDomain domain = DeleteUserMapper.toDomain(input);

        // 3️⃣ Verificar si existe el usuario
        var existingUser = userRepository.findById(domain.getUserId());
        if (existingUser.isEmpty()) {
            throw UcoChallengeExceptionFactory.create("USER_NOT_FOUND");
        }

        // 4️⃣ Eliminar usuario
        userRepository.deleteById(domain.getUserId());
    }
}
