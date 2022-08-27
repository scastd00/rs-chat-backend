package rs.chat.net.ws.strategies;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WSClientID;
import rs.chat.net.ws.WSMessage;
import rs.chat.net.ws.WebSocketChatMap;

import java.util.Map;

/**
 * Strategy for handling {@link WSMessage#TEXT_MESSAGE} messages.
 */
@Slf4j
public class TextMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException {
		WSClientID wsClientID = (WSClientID) otherData.get("wsClientID");

		// Clear the sensitive data to send the message to other clients
		String response = this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage.getParsedPayload());
		webSocketChatMap.broadcastToSingleChatAndExcludeClient(wsClientID, response);
	}
}
