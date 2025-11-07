package co.edu.uco.ucochallenge.user.deleteuser.usecase.domain;

import java.util.UUID;

/**
 * Entidad de dominio para representar la eliminación de un usuario.
 */
public class DeleteUserDomain {

    private UUID userId;

    public DeleteUserDomain(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
