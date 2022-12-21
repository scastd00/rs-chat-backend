package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static rs.chat.utils.Utils.createActiveUsersMessage;

/**
 * Strategy for handling {@link Message#ACTIVE_USERS_MESSAGE} messages.
 */
@Slf4j
public class ActiveUsersStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = getSession(otherData);
		ClientID clientID = (ClientID) otherData.get("clientID");

		List<String> usernamesOfChat = chatManagement.getUsernamesOfChat(clientID.chatId());

		// The sender username is removed from the list (because it is already connected).
		List<String> sortedUsers =
				usernamesOfChat.stream()
				               .sorted(String::compareToIgnoreCase)
				               .filter(username -> !username.equals(clientID.username()))
				               .toList();

		session.sendMessage(
				new TextMessage(createActiveUsersMessage(sortedUsers))
		);
	}
}
