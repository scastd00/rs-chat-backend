package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.AUDIO_MESSAGE;

public class AudioMessageEvent extends MessageEvent {
	public AudioMessageEvent(Object source, String username) {
		super(source, AUDIO_MESSAGE.type(), username);
	}
}
