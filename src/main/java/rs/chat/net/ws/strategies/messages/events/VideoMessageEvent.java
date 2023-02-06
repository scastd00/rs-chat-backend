package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.VIDEO_MESSAGE;

public class VideoMessageEvent extends MessageEvent {
	public VideoMessageEvent(Object source, String username) {
		super(source, VIDEO_MESSAGE.type(), username);
	}
}
