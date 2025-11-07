package co.edu.uco.ucochallenge.user.listuser.application.usecase;

import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserOutputDTO;
import co.edu.uco.ucochallenge.user.listuser.application.usecase.domain.UserFilterDomain;
import org.springframework.data.domain.Page;

public interface ListUsersUseCase {
    Page<UserOutputDTO> execute(UserFilterDomain domain);
}