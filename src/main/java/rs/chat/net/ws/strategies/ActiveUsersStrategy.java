package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static rs.chat.utils.Utils.createActiveUsersMessage;

@Slf4j
public class ActiveUsersStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");
		WSClientID wsClientID = (WSClientID) otherData.get("wsClientID");

		String[] usernamesOfChat = webSocketChatMap.getUsernamesOfChat(wsClientID.chatId());

		List<String> sortedUsers =
				Arrays.stream(usernamesOfChat)
				      .sorted(String::compareToIgnoreCase)
				      .filter(username -> !username.equals(wsClientID.username()))
				      .toList();

		session.sendMessage(
				new TextMessage(createActiveUsersMessage(sortedUsers))
		);
	}
}
