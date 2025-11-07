package co.edu.uco.ucochallenge.user.listuser.application.interactor.dto;

import java.util.UUID;

public record UserOutputDTO(
        UUID id,
        UUID idType,
        String idNumber,
        String firstName,
        String secondName,
        String firstSurname,
        String secondSurname,
        UUID homeCity,
        String email,
        String mobileNumber
) {}
