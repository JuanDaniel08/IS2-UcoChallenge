package co.edu.uco.ucochallenge.user.updateuser.application.usecase;

import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.domain.UpdateUserDomain;

public interface UpdateUserUseCase {
    Void execute(UpdateUserDomain domain);
}
