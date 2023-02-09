package rs.chat.net.ws.strategies.messages.events;

import lombok.Getter;
import lombok.Setter;

import static rs.chat.net.ws.Message.COMMAND_MESSAGE;

@Getter
@Setter
public class CommandMessageEvent extends MessageEvent {
	private String command;

	public CommandMessageEvent(Object source, String username) {
		super(source, COMMAND_MESSAGE.type(), username);
	}
}
