package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.TEXT_MESSAGE;

public class TextMessageEvent extends MessageEvent {
	public TextMessageEvent(Object source, String username) {
		super(source, TEXT_MESSAGE.type(), username);
	}
}
