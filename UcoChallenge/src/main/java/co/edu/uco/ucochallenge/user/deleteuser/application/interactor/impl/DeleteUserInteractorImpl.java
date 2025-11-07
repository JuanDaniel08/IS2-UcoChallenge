package co.edu.uco.ucochallenge.user.deleteuser.application.interactor.impl;

import org.springframework.stereotype.Service;
import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.DeleteUserInteractor;
import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;
import co.edu.uco.ucochallenge.user.deleteuser.application.usecase.DeleteUserUseCase;

@Service
public class DeleteUserInteractorImpl implements DeleteUserInteractor {

    private final DeleteUserUseCase useCase;

    public DeleteUserInteractorImpl(DeleteUserUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public void execute(DeleteUserInputDTO input) {
        useCase.execute(input);
    }
}
