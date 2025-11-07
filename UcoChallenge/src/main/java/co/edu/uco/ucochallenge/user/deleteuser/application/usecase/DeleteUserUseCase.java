package co.edu.uco.ucochallenge.user.deleteuser.application.usecase;

import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;

/**
 * Contrato del caso de uso para eliminar un usuario.
 */
public interface DeleteUserUseCase {
    void execute(DeleteUserInputDTO input);
}
