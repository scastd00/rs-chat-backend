package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WebSocketChatMap;

import java.util.Map;

import static rs.chat.net.ws.WSMessage.USER_LEFT;
import static rs.chat.utils.Utils.createServerMessage;

@Slf4j
public class UserLeftStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException {
		WSClientID wsClientID = (WSClientID) otherData.get("wsClientID");
		String chatId = wsClientID.chatId();
		String username = wsClientID.username();

		webSocketChatMap.broadcastToSingleChat(
				chatId,
				createServerMessage(username + " has disconnected from the chat", USER_LEFT.type(), chatId)
		);
		webSocketChatMap.removeClientFromChat(wsClientID);
		// Closed from the frontend

		log.debug(username + " has disconnected from the chat");
	}
}
