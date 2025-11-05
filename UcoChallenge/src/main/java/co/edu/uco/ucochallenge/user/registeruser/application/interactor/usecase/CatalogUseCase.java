package co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase;

/**
 * Caso de uso para obtener mensajes del catálogo.
 */
public interface CatalogUseCase {
	
	/**
	 * Obtiene un mensaje del catálogo por su clave.
	 * 
	 * @param key Clave del mensaje
	 * @return Mensaje asociado a la clave
	 */
	String getMessage(String key);
	
	/**
	 * Obtiene un mensaje del catálogo y reemplaza los parámetros.
	 * 
	 * @param key Clave del mensaje
	 * @param params Parámetros para reemplazar
	 * @return Mensaje con parámetros reemplazados
	 */
	String getMessage(String key, String... params);
}

