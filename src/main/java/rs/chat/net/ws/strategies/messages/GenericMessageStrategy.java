package rs.chat.net.ws.strategies.messages;

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
		this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage);
		webSocketChatMap.broadcastToSingleChatAndExcludeClient(clientID, wrappedMessage.toString());
	}

	/**
	 * Removes some fields of the received message to be able to send it to
	 * other clients without sensitive information. In addition, it updates
	 * the {@code date} field. NOTE: Only headers are modified.
	 *
	 * @param message received message to remove sensitive fields.
	 */
	protected void clearSensitiveDataChangeDateAndBuildResponse(JsonMessageWrapper message) {
		JsonObject headers = message.headers();
		headers.remove("sessionId");
		headers.remove("token");
		message.updateDateTime();
	}
}
