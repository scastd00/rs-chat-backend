package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

/**
 * Strategy for handling {@link Message#MENTION_MESSAGE} messages.
 */
@Slf4j
public class MentionMessageStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {

	}
}
