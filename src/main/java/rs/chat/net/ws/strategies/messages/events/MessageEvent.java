package rs.chat.net.ws.strategies.messages.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageEvent extends ApplicationEvent {
	private final String type;
	private final String username;

	public MessageEvent(Object source, String type, String username) {
		super(source);
		this.type = type;
		this.username = username;
	}
}
