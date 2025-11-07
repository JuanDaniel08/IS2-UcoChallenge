package co.edu.uco.ucochallenge.user.updateuser.application.usecase.impl;

import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.CityRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.CatalogUseCase;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.UpdateUserUseCase;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.domain.UpdateUserDomain;
import co.edu.uco.ucochallenge.application.Void;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final CatalogUseCase catalogUseCase;

    public UpdateUserUseCaseImpl(UserRepository userRepository,
                                 CityRepository cityRepository,
                                 CatalogUseCase catalogUseCase) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.catalogUseCase = catalogUseCase;
    }

    @Override
    public Void execute(UpdateUserDomain domain) {

        // 1. Verificar que el usuario existe
        UserEntity existingUser = userRepository.findById(domain.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        catalogUseCase.getMessage("validation.user.not.exists")
                ));

        Optional<UserEntity> userWithSameId = userRepository.findByIdTypeAndIdNumber(
                new IdTypeEntity.Builder().id(domain.getIdType()).build(),
                domain.getIdNumber()
        );

        if (userWithSameId.isPresent() && !userWithSameId.get().getId().equals(domain.getUserId())) {
            throw new IllegalArgumentException(
                    catalogUseCase.getMessage("validation.user.idtype.idnumber.duplicate")
            );
        }

        Optional<UserEntity> userWithSameEmail = userRepository.findByEmail(domain.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(domain.getUserId())) {
            throw new IllegalArgumentException(
                    catalogUseCase.getMessage("validation.user.email.duplicate")
            );
        }

        if (domain.getMobileNumber() != null && !domain.getMobileNumber().isBlank()) {
            Optional<UserEntity> userWithSameMobile = userRepository.findByMobileNumber(domain.getMobileNumber());
            if (userWithSameMobile.isPresent() && !userWithSameMobile.get().getId().equals(domain.getUserId())) {
                throw new IllegalArgumentException(
                        catalogUseCase.getMessage("validation.user.mobile.duplicate")
                );
            }
        }

        CityEntity homeCity = cityRepository.findById(domain.getHomeCity())
                .orElseThrow(() -> new IllegalArgumentException(
                        catalogUseCase.getMessage("validation.city.not.exists")
                ));

        var idTypeEntity = new IdTypeEntity.Builder().id(domain.getIdType()).build();


        var updatedUser = new UserEntity.Builder()
                .id(domain.getUserId())
                .idType(idTypeEntity)
                .idNumber(domain.getIdNumber())
                .firstName(domain.getFirstName())
                .secondName(domain.getSecondName())
                .firstSurname(domain.getFirstSurname())
                .secondSurname(domain.getSecondSurname())
                .homeCity(homeCity)
                .email(domain.getEmail())
                .mobileNumber(domain.getMobileNumber())
                .build();


        // 8. Guardar (JPA reemplazará la entidad existente por tener el mismo ID)
        userRepository.save(updatedUser);

        return Void.returnVoid();
    }
}
