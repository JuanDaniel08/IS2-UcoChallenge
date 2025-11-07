package co.edu.uco.ucochallenge.user.listuser.application.interactor;

import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserFilterInputDTO;
import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserOutputDTO;
import org.springframework.data.domain.Page;

public interface ListUsersInteractor {
    Page<UserOutputDTO> execute(UserFilterInputDTO dto);
}