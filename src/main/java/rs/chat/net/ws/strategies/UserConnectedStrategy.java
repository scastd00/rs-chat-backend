package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.USER_CONNECTED;
import static rs.chat.utils.Utils.createServerMessage;

/**
 * Strategy for handling {@link Message#USER_CONNECTED} messages.
 */
@Slf4j
public class UserConnectedStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");
		session.sendMessage(new TextMessage(
				createServerMessage(
						"Connected to server",
						USER_CONNECTED.type(),
						""
				)
		));
	}
}
