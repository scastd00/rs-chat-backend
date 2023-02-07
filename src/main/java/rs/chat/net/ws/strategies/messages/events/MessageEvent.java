package rs.chat.net.ws.strategies.messages.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.function.Function;

@Getter
@Setter
public class MessageEvent extends ApplicationEvent {
	private final String type;
	private final String username;
	protected transient Function<String, Void> callback;

	public MessageEvent(Object source, String type, String username) {
		super(source);
		this.type = type;
		this.username = username;
		this.callback = s -> null;
	}
}
