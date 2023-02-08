package rs.chat.net.ws.strategies.messages.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.rate.RateLimiter;

import static rs.chat.net.ws.Message.USER_LEFT;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#USER_LEFT} messages.
 */
@Slf4j
@AllArgsConstructor
public class UserLeftStrategy implements MessageStrategy {
	private final ChatManagement chatManagement;
	private final RateLimiter rateLimiter;

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException {
		ClientID clientID = handlingDTO.getClientID();
		String chatId = clientID.chatId();
		String username = clientID.username();

		chatManagement.broadcastToSingleChatAndExcludeClient(
				createMessage(username + " has left the chat", USER_LEFT.type(), chatId),
				clientID
		);
		chatManagement.removeClientFromChat(clientID);
		// Closed from the frontend
		this.rateLimiter.removeEntry(clientID.username());

		log.debug("{} has left the chat {}", username, chatId);
	}
}
