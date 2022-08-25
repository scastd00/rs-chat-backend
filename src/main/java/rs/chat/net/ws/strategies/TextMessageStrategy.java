package rs.chat.net.ws.strategies;

import com.google.gson.JsonObject;
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

	/**
	 * Removes the fields of the message received to be able to send it to
	 * other clients without sensitive information. In addition, it updates
	 * the {@code date} field. NOTE: Only headers are modified.
	 *
	 * @param message received message to remove sensitive fields.
	 *
	 * @return the {@link String} message without the sensitive information
	 * and the actual date of the server.
	 */
	private String clearSensitiveDataChangeDateAndBuildResponse(JsonObject message) {
		JsonObject headers = (JsonObject) message.get("headers");
		headers.remove("sessionId");
		headers.remove("token");
		headers.addProperty("date", System.currentTimeMillis()); // Modify property
		return message.toString();
	}
}
