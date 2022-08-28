package rs.chat.strategies.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static rs.chat.utils.Utils.createActiveUsersMessage;

/**
 * Strategy for handling {@link WSMessage#ACTIVE_USERS_MESSAGE} messages.
 */
@Slf4j
public class ActiveUsersStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");
		WSClientID wsClientID = (WSClientID) otherData.get("wsClientID");

		List<String> usernamesOfChat = webSocketChatMap.getUsernamesOfChat(wsClientID.chatId());

		// The sender username is removed from the list (because it is already connected).
		List<String> sortedUsers =
				usernamesOfChat.stream()
				               .sorted(String::compareToIgnoreCase)
				               .filter(username -> !username.equals(wsClientID.username()))
				               .toList();

		session.sendMessage(
				new TextMessage(createActiveUsersMessage(sortedUsers))
		);
	}
}
