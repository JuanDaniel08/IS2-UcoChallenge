package co.edu.uco.ucochallenge.user.registeruser.application.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class NotificationMessage {
	
	private String type;
	private String recipient;
	private String message;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;
	
	public NotificationMessage() {
		this.timestamp = LocalDateTime.now();
	}
	
	public NotificationMessage(String type, String recipient, String message) {
		this.type = type;
		this.recipient = recipient;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return String.format("NotificationMessage{type='%s', recipient='%s', message='%s', timestamp=%s}", 
				type, recipient, message, timestamp);
	}
}

