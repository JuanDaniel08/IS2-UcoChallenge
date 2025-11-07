package co.edu.uco.ucochallenge.user.updateuser.application.interactor;

import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.user.updateuser.application.interactor.dto.UpdateUserInputDTO;

public interface UpdateUserInteractor {
    Void execute(UpdateUserInputDTO dto);
}
