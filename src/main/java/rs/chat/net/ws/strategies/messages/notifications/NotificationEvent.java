package rs.chat.net.ws.strategies.messages.notifications;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
	private final String username;
	private final String message;
	private final String notificationType;

	public NotificationEvent(Object source, String username, String message, String notificationType) {
		super(source);
		this.username = username;
		this.message = message;
		this.notificationType = notificationType;
	}
}
