package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

import static rs.chat.net.ws.JsonMessageWrapper.createMessage;
import static rs.chat.net.ws.Message.ERROR_MESSAGE;

/**
 * Strategy for handling {@link Message#ERROR_MESSAGE} messages.
 */
@Slf4j
public class ErrorMessageStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		handlingDTO.getSession().sendMessage(new TextMessage(
				createMessage(
						"ERROR: type property is not present in the content of the JSON",
						ERROR_MESSAGE.type(),
						handlingDTO.getClientID().chatId()
				))
		);
	}
}
