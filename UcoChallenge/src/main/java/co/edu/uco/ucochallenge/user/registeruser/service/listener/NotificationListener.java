package co.edu.uco.ucochallenge.user.registeruser.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uco.ucochallenge.user.registeruser.service.dto.NotificationMessage;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Listener opcional para consumir mensajes de notificación desde Redis
 * Puede ser usado para procesar las notificaciones en tiempo real
 */
@Component
public class NotificationListener implements MessageListener {

	private final ObjectMapper objectMapper;

	public NotificationListener() {
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String channel = new String(message.getChannel());
			String body = new String(message.getBody());
			
			NotificationMessage notificationMessage = objectMapper.readValue(body, NotificationMessage.class);
			
			// Procesar el mensaje según el canal
			processNotification(channel, notificationMessage);
			
		} catch (Exception e) {
			System.err.println("Error al procesar mensaje de Redis: " + e.getMessage());
		}
	}
	
	private void processNotification(String channel, NotificationMessage notificationMessage) {
		// Aquí puedes agregar lógica para procesar diferentes tipos de notificaciones
		// Por ejemplo, enviar emails reales, SMS, etc.
		System.out.println(String.format("[REDIS] Canal: %s, Mensaje: %s", channel, notificationMessage));
		
		// Ejemplo de procesamiento según el tipo de canal
		switch (channel) {
			case "notification:email:verification":
				// Lógica para enviar email de verificación
				break;
			case "notification:sms:verification":
				// Lógica para enviar SMS de verificación
				break;
			case "notification:admin":
				// Lógica para notificar al admin
				break;
			default:
				// Procesamiento genérico
				break;
		}
	}
}

