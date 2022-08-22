package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClient;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.USER_JOINED;
import static rs.chat.utils.Utils.createServerMessage;

@Slf4j
public class UserJoinedStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");
		WSClientID wsClientID = (WSClientID) otherData.get("wsClientID");
		String chatId = wsClientID.chatId();
		String username = wsClientID.username();

		webSocketChatMap.addClientToChat(new WSClient(session, wsClientID));
		webSocketChatMap.broadcastToSingleChatAndExcludeClient(
				wsClientID,
				createServerMessage(username + " has joined the chat", USER_JOINED.type(), chatId)
		);

		log.debug(username + " has joined the chat");
	}
}
