package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.TEXT_DOC_MESSAGE;

public class TextDocMessageEvent extends MessageEvent {
	public TextDocMessageEvent(Object source, String username) {
		super(source, TEXT_DOC_MESSAGE.type(), username);
	}
}
