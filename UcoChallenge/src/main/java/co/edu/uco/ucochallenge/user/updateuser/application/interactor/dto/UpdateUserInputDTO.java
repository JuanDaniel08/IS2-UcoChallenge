package co.edu.uco.ucochallenge.user.updateuser.application.interactor.dto;

import java.util.UUID;

public record UpdateUserInputDTO(
        UUID userId,
        UUID idType,
        String idNumber,
        String firstName,
        String secondName,
        String firstSurname,
        String secondSurname,
        UUID homeCity,
        String email,
        String mobileNumber
) {
    public static UpdateUserInputDTO normalize(
            UUID userId,
            UUID idType,
            String idNumber,
            String firstName,
            String secondName,
            String firstSurname,
            String secondSurname,
            UUID homeCity,
            String email,
            String mobileNumber) {

        return new UpdateUserInputDTO(
                userId,
                idType,
                idNumber != null ? idNumber.trim() : null,
                firstName != null ? firstName.trim() : null,
                secondName != null ? secondName.trim() : null,
                firstSurname != null ? firstSurname.trim() : null,
                secondSurname != null ? secondSurname.trim() : null,
                homeCity,
                email != null ? email.trim().toLowerCase() : null,
                mobileNumber != null ? mobileNumber.trim() : null
        );
    }
}
