package co.edu.uco.ucochallenge.user.updateuser.application.usecase.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.uco.ucochallenge.application.Void;
import co.edu.uco.ucochallenge.application.service.RedisParameterService;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.CityEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.IdTypeEntity;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.CityRepository;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.UpdateUserUseCase;
import co.edu.uco.ucochallenge.user.updateuser.application.usecase.domain.UpdateUserDomain;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final RedisParameterService redisParameterService;

    public UpdateUserUseCaseImpl(UserRepository userRepository,
                                 CityRepository cityRepository,
                                 RedisParameterService redisParameterService) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.redisParameterService = redisParameterService;
    }

    @Override
    public Void execute(UpdateUserDomain domain) {

        // 1. Verificar que el usuario existe
        UserEntity existingUser = userRepository.findById(domain.getUserId())
                .orElseThrow(() -> {
                    String errorMsg = redisParameterService.getErrorMessage("validation.user.not.exists");
                    return new IllegalArgumentException(errorMsg);
                });

        // 2. Validar que no exista otro usuario con el mismo idType y idNumber
        Optional<UserEntity> userWithSameId = userRepository.findByIdTypeAndIdNumber(
                new IdTypeEntity.Builder().id(domain.getIdType()).build(),
                domain.getIdNumber()
        );

        if (userWithSameId.isPresent() && !userWithSameId.get().getId().equals(domain.getUserId())) {
            String errorMsg = redisParameterService.getErrorMessage("validation.user.idtype.idnumber.duplicate");
            throw new IllegalArgumentException(errorMsg);
        }

        // 3. Validar que no exista otro usuario con el mismo email
        Optional<UserEntity> userWithSameEmail = userRepository.findByEmail(domain.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(domain.getUserId())) {
            String errorMsg = redisParameterService.getErrorMessage("validation.user.email.duplicate");
            throw new IllegalArgumentException(errorMsg);
        }

        // 4. Validar que no exista otro usuario con el mismo móvil
        if (domain.getMobileNumber() != null && !domain.getMobileNumber().isBlank()) {
            Optional<UserEntity> userWithSameMobile = userRepository.findByMobileNumber(domain.getMobileNumber());
            if (userWithSameMobile.isPresent() && !userWithSameMobile.get().getId().equals(domain.getUserId())) {
                String errorMsg = redisParameterService.getErrorMessage("validation.user.mobile.duplicate");
                throw new IllegalArgumentException(errorMsg);
            }
        }

        // 5. Cargar la ciudad
        CityEntity homeCity = cityRepository.findById(domain.getHomeCity())
                .orElseThrow(() -> {
                    String errorMsg = redisParameterService.getErrorMessage("validation.city.not.exists");
                    return new IllegalArgumentException(errorMsg);
                });

        // 6. Preparar el tipo de identificación
        var idTypeEntity = new IdTypeEntity.Builder().id(domain.getIdType()).build();

        // 7. Crear la entidad actualizada
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
