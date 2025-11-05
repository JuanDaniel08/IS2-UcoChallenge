package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.impl;

import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.ValidationResultVO;
import co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator.Validator;

import java.util.UUID;

public class UsuarioConMismoId implements Validator<UUID, ValidationResultVO> {
    private UserRepository userRepository;

    public UsuarioConMismoId(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public ValidationResultVO validate(UUID data) {
        var resultadoValidacion = new ValidationResultVO();

        if (!userRepository.existsById(data)){
            //TODO: No se pueden quemar mensajes. Debe estar en el catalogo de mensajes
            resultadoValidacion.agregarMensaje("No existe un usuario en el id: " + data);
        }
        return resultadoValidacion;
    }
}
