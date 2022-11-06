package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#MENTION_MESSAGE} messages.
 */
@Slf4j
public class MentionMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap, Map<String, Object> otherData) throws WebSocketException, IOException {

	}
}
