package co.edu.uco.ucochallenge.user.deleteuser.application.interactor.impl;

import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.DeleteUserInteractor;
import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;
import co.edu.uco.ucochallenge.user.deleteuser.application.usecase.DeleteUserUseCase;

/**
 * Implementación del interactor que orquesta la eliminación de usuario.
 */
public class DeleteUserInteractorImpl implements DeleteUserInteractor {

    private final DeleteUserUseCase useCase;

    public DeleteUserInteractorImpl(final DeleteUserUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public void execute(final DeleteUserInputDTO input) {
        useCase.execute(input);
    }
}
