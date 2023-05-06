package rs.chat.net.ws.strategies.messages.impl;

import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

import static rs.chat.net.ws.JsonMessageWrapper.createMessage;
import static rs.chat.net.ws.Message.PONG_MESSAGE;

/**
 * Strategy for handling {@link Message#PING_MESSAGE} messages.
 */
public class PingStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		handlingDTO.getSession().sendMessage(new TextMessage(
				createMessage("I send a pong message", PONG_MESSAGE.type(), handlingDTO.wrappedMessage().chatId())
		));
	}
}
