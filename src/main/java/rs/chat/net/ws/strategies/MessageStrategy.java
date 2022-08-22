package rs.chat.net.ws.strategies;

import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

public interface MessageStrategy {
	void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap) throws WebSocketException;
}
