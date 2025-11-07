package co.edu.uco.ucochallenge.user.deleteuser.application.interactor.dto;

import java.util.UUID;

import co.edu.uco.ucochallenge.crosscuting.helper.UUIDHelper;

/**
 * DTO de entrada para eliminar un usuario.
 */
public record DeleteUserInputDTO(UUID userId) {
	
	public static DeleteUserInputDTO normalize(final UUID userId) {
		var userIdNormalized = UUIDHelper.getDefault(userId);
		
		return new DeleteUserInputDTO(userIdNormalized);
	}
}
