package rs.chat.strategies.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.Client;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.USER_JOINED;
import static rs.chat.utils.Utils.createServerMessage;

/**
 * Strategy for handling {@link Message#USER_JOINED} messages.
 */
@Slf4j
public class UserJoinedStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");
		ClientID clientID = (ClientID) otherData.get("clientID");
		String chatId = clientID.chatId();
		String username = clientID.username();

		webSocketChatMap.addClientToChat(new Client(session, clientID));
		webSocketChatMap.broadcastToSingleChatAndExcludeClient(
				clientID,
				createServerMessage(username + " has joined the chat", USER_JOINED.type(), chatId)
		);

		log.debug(username + " has joined the chat");
	}
}
