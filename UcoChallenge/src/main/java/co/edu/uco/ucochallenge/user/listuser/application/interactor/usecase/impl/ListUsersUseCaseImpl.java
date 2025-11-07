package co.edu.uco.ucochallenge.user.listuser.application.interactor.usecase.impl;

import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserOutputDTO;
import co.edu.uco.ucochallenge.user.listuser.application.usecase.ListUsersUseCase;
import co.edu.uco.ucochallenge.user.listuser.application.usecase.domain.UserFilterDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserOutputDTO> execute(UserFilterDomain domain) {
        Pageable pageable = PageRequest.of(domain.getPage(), domain.getSize());

        Page<UserEntity> userEntities;

        // Si no hay filtro de nombre, traer todos
        if (domain.getNameFilter() == null || domain.getNameFilter().isBlank()) {
            userEntities = userRepository.findAll(pageable);
        } else {
            // Si hay filtro, buscar por nombre
            userEntities = userRepository.findByNameContaining(domain.getNameFilter(), pageable);
        }

        // Mapear entidades a DTOs
        return userEntities.map(user -> new UserOutputDTO(
                user.getIdType().getId(),
                user.getIdNumber(),
                user.getFirstName(),
                user.getSecondName(),
                user.getFirstSurname(),
                user.getSecondSurname(),
                user.getHomeCity().getId(),
                user.getEmail(),
                user.getMobileNumber()
        ));
    }
}