package co.edu.uco.ucochallenge.user.deleteuser.application.mapper;

import co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto.DeleteUserInputDTO;
import co.edu.uco.ucochallenge.user.deleteuser.usecase.domain.DeleteUserDomain;


public final class DeleteUserMapper {

    private DeleteUserMapper() {
        super();
    }

    public static DeleteUserDomain toDomain(final DeleteUserInputDTO dto) {
        return new DeleteUserDomain(dto.userId());
    }

    public static DeleteUserInputDTO toDTO(final DeleteUserDomain domain) {
        return new DeleteUserInputDTO(domain.getUserId());
    }
}
