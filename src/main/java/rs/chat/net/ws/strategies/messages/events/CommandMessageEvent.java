package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.COMMAND_MESSAGE;

public class CommandMessageEvent extends MessageEvent {
	public CommandMessageEvent(Object source, String username) {
		super(source, COMMAND_MESSAGE.type(), username);
	}
}
