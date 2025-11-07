package co.edu.uco.ucochallenge.user.listuser.application.interactor.dto;

public record UserFilterInputDTO(
        int page,
        int size,
        String name
) {
    public static UserFilterInputDTO of(int page, int size, String name) {
        return new UserFilterInputDTO(page, size, name);
    }
}