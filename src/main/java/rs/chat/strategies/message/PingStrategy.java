package rs.chat.strategies.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.PONG_MESSAGE;
import static rs.chat.utils.Utils.createServerMessage;

/**
 * Strategy for handling {@link WSMessage#PING_MESSAGE} messages.
 */
@Slf4j
public class PingStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");

		session.sendMessage(new TextMessage(
				createServerMessage("I send a pong message", PONG_MESSAGE.type(), wrappedMessage.chatId())
		));
	}
}
