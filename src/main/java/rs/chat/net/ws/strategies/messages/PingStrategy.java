package rs.chat.net.ws.strategies.messages;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

import static rs.chat.net.ws.Message.PONG_MESSAGE;
import static rs.chat.utils.Utils.createMessage;

/**
 * Strategy for handling {@link Message#PING_MESSAGE} messages.
 */
public class PingStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		WebSocketSession session = (WebSocketSession) otherData.get("session");

		session.sendMessage(new TextMessage(
				createMessage("I send a pong message", PONG_MESSAGE.type(), wrappedMessage.chatId())
		));
	}
}
