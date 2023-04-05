package rs.chat.net.ws.strategies.messages.notifications;

import rs.chat.net.ws.Message;

public class UserBlockedEvent extends NotificationEvent {
	public UserBlockedEvent(Object source, String username, String message) {
		super(source, username, message, Message.KICK_MESSAGE.type());
	}
}
