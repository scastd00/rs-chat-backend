package rs.chat.net.ws.strategies.messages.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Client;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

import static rs.chat.Constants.MAX_FILE_BYTES;
import static rs.chat.net.ws.JsonMessageWrapper.createMessage;
import static rs.chat.net.ws.Message.USER_JOINED;

/**
 * Strategy for handling {@link Message#USER_JOINED} messages.
 */
@Slf4j
@AllArgsConstructor
public class UserJoinedStrategy implements MessageStrategy {
	private final ChatManagement chatManagement;

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		ClientID clientID = handlingDTO.getClientID();
		String chatId = clientID.chatId();
		String username = clientID.username();

		ConcurrentWebSocketSessionDecorator decorator = new ConcurrentWebSocketSessionDecorator(
				handlingDTO.getSession(),
				Integer.MAX_VALUE,
				Math.toIntExact(MAX_FILE_BYTES)
		);

		this.chatManagement.addClientToChat(new Client(decorator, clientID));
		this.chatManagement.broadcastToSingleChatExcludeClientWithoutSaving(
				createMessage(username + " has joined the chat", USER_JOINED.type(), chatId),
				clientID
		);

		log.debug(username + " has joined the chat");
	}
}
