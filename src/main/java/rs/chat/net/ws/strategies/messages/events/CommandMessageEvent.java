package rs.chat.net.ws.strategies.messages.events;

public class CommandMessageEvent extends MessageEvent {
	public CommandMessageEvent(Object source, String command, String username) {
		super(source, command, username);
	}
}
