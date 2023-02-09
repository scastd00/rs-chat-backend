package rs.chat.net.ws.strategies.messages.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.ClientID;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.function.Function;

import static rs.chat.net.ws.Message.BADGE_EARNED_MESSAGE;

/**
 * Strategy for handling messages that can be broadcast to the entire chat without
 * any processing.
 */
@Slf4j
@AllArgsConstructor
public class GenericMessageStrategy implements MessageStrategy {
	protected final ChatManagement chatManagement;

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		ClientID clientID = handlingDTO.getClientID();

		// Clear the sensitive data to send the message to other clients
		this.clearSensitiveDataChangeDateAndBuildResponse(handlingDTO.wrappedMessage());
		this.chatManagement.broadcastToSingleChatExcludeClientAndSave(
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

	protected static Function<String, Void> badgeCallback(MessageHandlingDTO handlingDTO) {
		return badgeTitle -> {
			try {
				handlingDTO.getSession().sendMessage(new TextMessage(
						Utils.createMessage(
								"You have received a badge (%s)".formatted(badgeTitle),
								BADGE_EARNED_MESSAGE.type(),
								handlingDTO.getClientID().chatId()
						)
				));
			} catch (IOException e) {
				log.error("Error while sending badge notification", e);
			}

			return null;
		};
	}
}
