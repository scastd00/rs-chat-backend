package rs.chat.strategies.message;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;

import java.util.Map;

/**
 * Strategy for handling {@link WSMessage#VIDEO_MESSAGE} messages.
 */
@Slf4j
public class VideoMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException {

	}
}
