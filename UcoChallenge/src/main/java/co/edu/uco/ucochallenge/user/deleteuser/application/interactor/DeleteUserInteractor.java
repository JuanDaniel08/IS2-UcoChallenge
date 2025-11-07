package co.edu.uco.ucochallenge.user.deleteuser.application.interactor;

import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;

public interface DeleteUserInteractor {
    void execute(DeleteUserInputDTO input);
}
