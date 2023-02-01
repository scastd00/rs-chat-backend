package rs.chat.net.ws.strategies.messages.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;

import java.io.IOException;

/**
 * Strategy for handling messages that can be broadcast to the entire chat without
 * any processing.
 */
@Slf4j
@AllArgsConstructor
public class GenericMessageStrategy implements MessageStrategy {
	private final ChatManagement chatManagement;

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		ClientID clientID = handlingDTO.getClientID();

		// Clear the sensitive data to send the message to other clients
		this.clearSensitiveDataChangeDateAndBuildResponse(handlingDTO.wrappedMessage());
		chatManagement.broadcastToSingleChatAndExcludeClient(
				handlingDTO.wrappedMessage().toString(),
				clientID
		);
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
