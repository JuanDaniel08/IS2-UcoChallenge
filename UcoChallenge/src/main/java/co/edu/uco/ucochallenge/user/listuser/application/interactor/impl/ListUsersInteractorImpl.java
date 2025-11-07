package co.edu.uco.ucochallenge.user.listuser.application.interactor.impl;


import co.edu.uco.ucochallenge.user.listuser.application.interactor.ListUsersInteractor;
import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserFilterInputDTO;
import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserOutputDTO;
import co.edu.uco.ucochallenge.user.listuser.application.usecase.ListUsersUseCase;
import co.edu.uco.ucochallenge.user.listuser.application.usecase.domain.UserFilterDomain;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@Transactional(readOnly = true)
public class ListUsersInteractorImpl implements ListUsersInteractor {

    private final ListUsersUseCase useCase;

    public ListUsersInteractorImpl(ListUsersUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public Page<UserOutputDTO> execute(UserFilterInputDTO dto) {
        UserFilterDomain domain = new UserFilterDomain(
                dto.page(),
                dto.size(),
                dto.name()
        );

        return useCase.execute(domain);
    }
}