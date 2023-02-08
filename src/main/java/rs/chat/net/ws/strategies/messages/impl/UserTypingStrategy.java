package rs.chat.net.ws.strategies.messages.impl;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

/**
 * Strategy for handling {@link Message#USER_TYPING} messages.
 */
public class UserTypingStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {

	}
}
