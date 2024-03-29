package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

import static rs.chat.net.ws.Message.USER_CONNECTED;
import static rs.chat.utils.Constants.SERVER_CHAT_ID;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#USER_CONNECTED} messages.
 */
@Slf4j
public class UserConnectedStrategy implements MessageStrategy {
	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		handlingDTO.getSession().sendMessage(new TextMessage(
				createMessage(
						"Connected to server",
						USER_CONNECTED.type(),
						SERVER_CHAT_ID
				)
		));
	}
}
