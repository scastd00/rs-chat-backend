package rs.chat.net.ws.strategies.messages.events;

import static rs.chat.net.ws.Message.MENTION_MESSAGE;

public class MentionMessageEvent extends MessageEvent {
	public MentionMessageEvent(Object source, String username) {
		super(source, MENTION_MESSAGE.type(), username);
	}
}
