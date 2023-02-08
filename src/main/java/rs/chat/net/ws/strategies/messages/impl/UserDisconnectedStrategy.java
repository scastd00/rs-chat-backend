package rs.chat.net.ws.strategies.messages.impl;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

/**
 * Strategy for handling {@link Message#USER_DISCONNECTED} messages.
 */
public class UserDisconnectedStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		// Do nothing. User is already removed from the chats.
	}
}
