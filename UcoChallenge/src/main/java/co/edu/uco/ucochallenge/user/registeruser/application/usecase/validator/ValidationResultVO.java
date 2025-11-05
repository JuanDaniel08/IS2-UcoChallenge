package co.edu.uco.ucochallenge.user.registeruser.application.usecase.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResultVO {

	private final List<String> mensajes;

	public ValidationResultVO() {
		this.mensajes = new ArrayList<>();
	}

	public void agregarMensaje(final String mensaje) {
		if (mensaje == null || mensaje.isBlank()) {
			return;
		}
		this.mensajes.add(mensaje);
	}

	public void agregarMensajes(final List<String> nuevosMensajes) {
		if (nuevosMensajes == null || nuevosMensajes.isEmpty()) {
			return;
		}
		for (final String mensaje : nuevosMensajes) {
			agregarMensaje(mensaje);
		}
	}

	public List<String> getMensajes() {
		return Collections.unmodifiableList(this.mensajes);
	}

	public boolean isValidacionCorrecta() {
		return this.mensajes.isEmpty();
	}
}


