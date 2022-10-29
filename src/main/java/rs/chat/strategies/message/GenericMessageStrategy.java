package rs.chat.strategies.message;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.WebSocketChatMap;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling messages that have a message body containing the string to
 * a specific resource in S3.
 */
@Slf4j
public class GenericMessageStrategy implements MessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		ClientID clientID = (ClientID) otherData.get("clientID");

		// Clear the sensitive data to send the message to other clients
		String response = this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage.getParsedPayload());
		webSocketChatMap.broadcastToSingleChatAndExcludeClient(clientID, response);
	}

	/**
	 * Removes the fields of the received message to be able to send it to
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
