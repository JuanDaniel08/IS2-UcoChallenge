package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.catalog;

import java.util.HashMap;
import java.util.Map;

/**
 * Catálogo de mensajes del sistema.
 * Centraliza todos los mensajes para evitar mensajes hardcodeados.
 */
public final class MessageCatalog {

	private static final Map<String, String> messages = new HashMap<>();
	
	private MessageCatalog() {
		// Constructor privado para evitar instanciación
	}
	
	static {
		// Mensajes de validación
		messages.put("validation.user.id.null", "El ID del usuario no puede ser nulo");
		messages.put("validation.user.id.exists", "Ya existe un usuario con el ID: {0}");
		messages.put("validation.user.email.duplicate", "Ya existe un usuario con el mismo email");
		messages.put("validation.user.mobile.duplicate", "Ya existe un usuario con el mismo número móvil");
		messages.put("validation.user.idtype.idnumber.duplicate", "Ya existe un usuario con el mismo tipo y número de identificación");
		messages.put("validation.error.unexpected", "Error inesperado durante la validación: {0}");
		messages.put("validation.city.not.exists", "La ciudad especificada no existe");
		
		// Mensajes de excepción de reglas de negocio
		messages.put("business.email.registered", "Email ya registrado");
		messages.put("business.mobile.registered", "Número móvil ya registrado");
		messages.put("business.identification.registered", "Identificación ya registrada");
		
		// Mensajes de notificación
		messages.put("notification.email.duplicate.actor", "Ya existe un usuario con el email: {0}");
		messages.put("notification.email.duplicate.owner", "Se intentó registrar otro usuario con tu email.");
		messages.put("notification.mobile.duplicate.actor", "Ya existe un usuario con el número: {0}");
		messages.put("notification.mobile.duplicate.owner", "Se intentó registrar otro usuario con tu número.");
		messages.put("notification.identification.duplicate.actor", "Ya existe un usuario con el mismo tipo y número de identificación.");
		messages.put("notification.identification.duplicate.admin", "Doble identificación detectada: tipo={0}, número={1}");
		messages.put("notification.mobile.duplicate.owner.sms", "Se intentó registrar otro usuario con tu número móvil.");
		messages.put("notification.user.registered.success", "Usuario registrado. Se enviaron confirmaciones.");
		
		// Mensajes de tokens
		messages.put("token.invalid", "Token inválido");
		messages.put("token.type.incorrect", "Tipo de token incorrecto");
		messages.put("token.expired", "Token expirado");
		messages.put("token.already.used", "Token ya utilizado");
	}
	
	/**
	 * Obtiene un mensaje del catálogo por su clave.
	 * 
	 * @param key Clave del mensaje
	 * @return Mensaje asociado a la clave, o la clave misma si no se encuentra
	 */
	public static String getMessage(String key) {
		return messages.getOrDefault(key, key);
	}
	
	/**
	 * Obtiene un mensaje del catálogo y reemplaza los parámetros.
	 * 
	 * @param key Clave del mensaje
	 * @param params Parámetros para reemplazar en el mensaje (usando {0}, {1}, etc.)
	 * @return Mensaje con parámetros reemplazados
	 */
	public static String getMessage(String key, String... params) {
		String message = messages.getOrDefault(key, key);
		
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				message = message.replace("{" + i + "}", params[i] != null ? params[i] : "");
			}
		}
		
		return message;
	}
	
	/**
	 * Agrega o actualiza un mensaje en el catálogo.
	 * 
	 * @param key Clave del mensaje
	 * @param value Valor del mensaje
	 */
	public static void setMessage(String key, String value) {
		messages.put(key, value);
	}
	
	/**
	 * Verifica si existe un mensaje en el catálogo.
	 * 
	 * @param key Clave del mensaje
	 * @return true si existe, false en caso contrario
	 */
	public static boolean containsMessage(String key) {
		return messages.containsKey(key);
	}
}

