package co.edu.uco.ucochallenge.user.registeruser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uco.ucochallenge.user.registeruser.service.dto.NotificationMessage;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	
	private static final String CHANNEL_EMAIL_VERIFICATION = "notification:email:verification";
	private static final String CHANNEL_SMS_VERIFICATION = "notification:sms:verification";
	private static final String CHANNEL_ACTOR = "notification:actor";
	private static final String CHANNEL_OWNER_EMAIL = "notification:owner:email";
	private static final String CHANNEL_OWNER_SMS = "notification:owner:sms";
	private static final String CHANNEL_ADMIN = "notification:admin";

	public NotificationService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = new ObjectMapper();
	}

	public void sendEmailVerification(String email, String token, String message) {
		// Enviar email con el token y el mensaje
		// Código de envío de email...
		System.out.println("Email sent to: " + email);
		System.out.println("Token: " + token);
		System.out.println("Message: " + message);
	}

	public void sendSmsVerification(String phone, String token, String message) {
		// Enviar SMS con el token y el mensaje
		System.out.println("SMS sent to: " + phone);
		System.out.println("Token: " + token);
		System.out.println("Message: " + message);
	}


	public void notifyActor(String actor, String message) {
		publishMessage(CHANNEL_ACTOR, actor, message);
	}

	public void notifyOwnerEmail(String email, String message) {
		publishMessage(CHANNEL_OWNER_EMAIL, email, message);
	}

	public void notifyOwnerSms(String phone, String message) {
		publishMessage(CHANNEL_OWNER_SMS, phone, message);
	}

	public void notifyAdmin(String message) {
		publishMessage(CHANNEL_ADMIN, "admin", message);
	}
	
	private void publishMessage(String channel, String recipient, String message) {
		try {
			NotificationMessage notificationMessage = new NotificationMessage(channel, recipient, message);
			String jsonMessage = objectMapper.writeValueAsString(notificationMessage);
			redisTemplate.convertAndSend(channel, jsonMessage);
			// Log opcional para debugging
			System.out.println(String.format("[REDIS PUBLISH] Canal: %s, Destinatario: %s", channel, recipient));
		} catch (JsonProcessingException e) {
			// Fallback a System.out en caso de error de serialización
			System.err.println("Error al serializar mensaje para Redis: " + e.getMessage());
			System.out.println(String.format("[%s] %s: %s", channel, recipient, message));
		}
	}
}
