package co.edu.uco.ucochallenge.user.deleteuser.application.validator;

import co.edu.uco.ucochallenge.crosscuting.exception.UcoChallengeException;
import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;
import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;

/**
 * Validador para la entrada de eliminación de usuario.
 */
public final class DeleteUserInputValidator {

    private DeleteUserInputValidator() {
        super();
    }

    public static void validate(final DeleteUserInputDTO dto) {
        if (dto == null) {
            throw new UcoChallengeException("La información de entrada para eliminar un usuario no puede ser nula.");
        }

        if (UUIDHelper.isDefault(dto.userId())) {
            throw new UcoChallengeException("El identificador del usuario es obligatorio para eliminarlo.");
        }
    }
}
