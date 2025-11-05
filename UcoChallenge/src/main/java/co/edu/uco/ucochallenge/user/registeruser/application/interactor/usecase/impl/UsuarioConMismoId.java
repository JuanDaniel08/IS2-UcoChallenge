package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.CatalogUseCase;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.ValidationResultVO;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.Validator;

import java.util.UUID;


public class UsuarioConMismoId implements Validator<UUID, ValidationResultVO> {
    private final UserRepository userRepository;
    private final CatalogUseCase catalogUseCase;

    public UsuarioConMismoId(UserRepository userRepository, CatalogUseCase catalogUseCase) {
        this.userRepository = userRepository;
        this.catalogUseCase = catalogUseCase;
    }

    @Override
    public ValidationResultVO validate(UUID data) {
        var resultadoValidacion = new ValidationResultVO();

        // Validar que el ID no sea nulo
        if (data == null) {
            resultadoValidacion.agregarMensaje(catalogUseCase.getMessage("validation.user.id.null"));
            return resultadoValidacion;
        }

        // Verificar si el ID ya existe en la base de datos
        // Esto es útil principalmente para actualizaciones, no para registros nuevos
        if (userRepository.existsById(data)) {
            resultadoValidacion.agregarMensaje(catalogUseCase.getMessage("validation.user.id.exists", data.toString()));
        }
        
        return resultadoValidacion;
    }
}
