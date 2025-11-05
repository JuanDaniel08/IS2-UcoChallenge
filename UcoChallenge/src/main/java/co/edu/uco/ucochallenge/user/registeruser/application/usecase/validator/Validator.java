package co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator;

public interface Validator<T, R> {
	R validate(T data);
}


