package co.edu.uco.ucochallenge.user.deleteuser.application.interactor.usecase;

import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;

/**
 * Contrato del caso de uso para eliminar un usuario.
 * Define la operación principal de negocio sin detalles técnicos.
 */
public interface DeleteUserUseCase {

    /**
     * Ejecuta la eliminación de un usuario con los datos proporcionados.
     * 
     * @param input DTO con el identificador del usuario a eliminar.
     */
    void execute(DeleteUserInputDTO input);
}
