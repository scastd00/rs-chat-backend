package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;

import java.util.Map;

import static rs.chat.net.ws.Message.USER_LEFT;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#USER_LEFT} messages.
 */
@Slf4j
public class UserLeftStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException {
		ClientID clientID = (ClientID) otherData.get("clientID");
		String chatId = clientID.chatId();
		String username = clientID.username();

		chatManagement.broadcastToSingleChatAndExcludeClient(
				clientID,
				createMessage(username + " has left the chat", USER_LEFT.type(), chatId)
		);
		chatManagement.removeClientFromChat(clientID);
		// Closed from the frontend

		log.debug("{} has left the chat {}", username, chatId);
	}
}