package rs.chat.net.ws.strategies;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

public interface MessageStrategy {
	void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	            Map<String, Object> otherData) throws WebSocketException, IOException;
}
