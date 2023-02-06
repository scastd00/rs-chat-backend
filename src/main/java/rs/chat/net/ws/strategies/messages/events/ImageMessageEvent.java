package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.IMAGE_MESSAGE;

public class ImageMessageEvent extends MessageEvent {
	public ImageMessageEvent(Object source, String username) {
		super(source, IMAGE_MESSAGE.type(), username);
	}
}
