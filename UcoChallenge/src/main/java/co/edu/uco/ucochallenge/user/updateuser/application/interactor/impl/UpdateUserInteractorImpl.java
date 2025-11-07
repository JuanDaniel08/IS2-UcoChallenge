package co.edu.uco.ucochallenge.user.updateuser.application.interactor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.user.updateuser.application.interactor.UpdateUserInteractor;
import co.edu.uco.ucochallenge.user.updateuser.application.interactor.dto.UpdateUserInputDTO;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.UpdateUserUseCase;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.domain.UpdateUserDomain;

@Service
@Transactional
public class UpdateUserInteractorImpl implements UpdateUserInteractor {

    private final UpdateUserUseCase useCase;

    public UpdateUserInteractorImpl(UpdateUserUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public Void execute(UpdateUserInputDTO dto) {
        UpdateUserDomain domain = new UpdateUserDomain(
                dto.userId(),
                dto.idType(),
                dto.idNumber(),
                dto.firstName(),
                dto.secondName(),
                dto.firstSurname(),
                dto.secondSurname(),
                dto.homeCity(),
                dto.email(),
                dto.mobileNumber()
        );

        return useCase.execute(domain);
    }
}
