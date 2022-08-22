package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

@Slf4j
public class ImageMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap) throws WebSocketException {

	}
}
