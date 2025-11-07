package co.edu.uco.ucochallenge.primary.controller;

import co.edu.uco.ucochallenge.user.listuser.application.interactor.ListUsersInteractor;
import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserFilterInputDTO;
import co.edu.uco.ucochallenge.user.listuser.application.interactor.dto.UserOutputDTO;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.RegisterUserInteractor;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.dto.RegisterUserInputDTO;
import co.edu.uco.ucochallenge.user.registeruser.application.service.UserRegistrationService;
import co.edu.uco.ucochallenge.user.updateuser.application.interactor.UpdateUserInteractor;
import co.edu.uco.ucochallenge.user.updateuser.application.interactor.dto.UpdateUserInputDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/uco-challenge/api/v1/users")
public class UserController {

    private final RegisterUserInteractor registerUserInteractor;
    private final UserRegistrationService registrationService;
    private final ListUsersInteractor listUsersInteractor;
    private final UpdateUserInteractor updateUserInteractor;

    public UserController(RegisterUserInteractor registerUserInteractor,
                          UserRegistrationService registrationService,
                          UpdateUserInteractor updateUserInteractor,
                          ListUsersInteractor listUsersInteractor) {
        this.registerUserInteractor = registerUserInteractor;
        this.listUsersInteractor = listUsersInteractor;
        this.updateUserInteractor = updateUserInteractor;

        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserInputDTO dto,
                                          @RequestHeader(value = "X-Actor", required = false) String actor) {
        
        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El cuerpo de la petición no puede estar vacío"));
        }

        var normalizedDto = RegisterUserInputDTO.normalize(
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

        registerUserInteractor.execute(normalizedDto);
        return new ResponseEntity<>(Map.of("message", "Usuario registrado exitosamente"), HttpStatus.CREATED);
    }
    @GetMapping("/filter/{page}/{size}")
    public ResponseEntity<?> getUsers(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam(required = false) String name) {

        UserFilterInputDTO dto = UserFilterInputDTO.of(page, size, name);
        Page<UserOutputDTO> users = listUsersInteractor.execute(dto);


        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserInputDTO dto) {

        if (dto == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El cuerpo de la petición no puede estar vacío"));
        }

        var normalizedDto = UpdateUserInputDTO.normalize(
                userId,
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

        updateUserInteractor.execute(normalizedDto);

        return new ResponseEntity<>(
                Map.of("message", "Usuario actualizado exitosamente"),
                HttpStatus.OK
        );
    }

    @PostMapping("/confirm/email/{token}")
    public ResponseEntity<?> confirmEmail(@PathVariable String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El token no puede estar vacío"));
        }
        
        try {
            registrationService.confirmEmail(token);
            return ResponseEntity.ok(Map.of("status", "email_confirmed", 
                    "message", "Email confirmado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/confirm/sms/{token}")
    public ResponseEntity<?> confirmSms(@PathVariable String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El token no puede estar vacío"));
        }
        
        try {
            registrationService.confirmSms(token);
            return ResponseEntity.ok(Map.of("status", "mobile_confirmed",
                    "message", "Número móvil confirmado exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

