package rs.chat.net.ws;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.net.ws.strategies.messages.MessageStrategyMappings;
import rs.chat.observability.metrics.Metrics;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Constants.SCHEDULE_STRING;

/**
 * WebSocket handler for the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private final ChatManagement chatManagement;
	private final Metrics metrics;

	/**
	 * Handles text messages (JSON string).
	 *
	 * @param session remote WebSocket session of the client in the server.
	 * @param message message received from the client.
	 */
	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
		// FIXME: A user that did not send the USER_JOINED message could send messages
		//  but cannot receive them.

		JsonMessageWrapper wrappedMessage = new JsonMessageWrapper(message.getPayload());
		Message receivedMessageType = new Message(wrappedMessage.type(), null, null);

		Map<String, Object> otherData = new HashMap<>();
		otherData.put("session", session);
		otherData.put("message", receivedMessageType);
		otherData.put("clientID", new ClientID(
				wrappedMessage.username(),
				wrappedMessage.chatId(),
				wrappedMessage.sessionId()
		));

		// Scheduled messages contain, as content, only a string with the message and the
		// string of the constant SCHEDULE_STRING.
		boolean hasTextBody = this.hasTextBody(wrappedMessage);
		if (hasTextBody && wrappedMessage.content().contains(SCHEDULE_STRING)) {
			// Message format is: <content>SCHEDULE_STRING<date>
			String[] strings = wrappedMessage.content().split(SCHEDULE_STRING);
			otherData.put("schedule", LocalDateTime.parse(strings[1], DateTimeFormatter.ISO_DATE_TIME));
			wrappedMessage.setContent(strings[0]);
		}

		// Strategy pattern for handling messages.
		MessageStrategy strategy;

		if (hasTextBody && this.isParseableMessage(wrappedMessage.content())) {
			strategy = MessageStrategyMappings.decideStrategy(Message.PARSEABLE_MESSAGE);
		} else {
			strategy = MessageStrategyMappings.decideStrategy(receivedMessageType);
		}

		try {
			Utils.checkTokenValidity(wrappedMessage.token());
			strategy.handle(wrappedMessage, this.chatManagement, otherData);
			this.metrics.incrementMessageCount(receivedMessageType.type());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws IOException {
		log.error(exception.getMessage(), exception);
		session.close(CloseStatus.SERVER_ERROR);
	}

	/**
	 * Checks if the message is a parseable message.
	 *
	 * @param message message to check.
	 *
	 * @return true if the message is a parseable message, false otherwise.
	 */
	private boolean isParseableMessage(String message) {
		return message.contains("/") || message.contains("@");
	}

	/**
	 * Checks if the message has text body (check {@link JsonObject#isJsonPrimitive()}).
	 * If it is not, it is a JSON object (unable to parse a {@link String} inside it).
	 *
	 * @param wrappedMessage message to check.
	 *
	 * @return {@code true} if the message has a string as body, {@code false} otherwise.
	 */
	private boolean hasTextBody(JsonMessageWrapper wrappedMessage) {
		return ((JsonObject) wrappedMessage.getParsedPayload().get("body"))
				.get("content").isJsonPrimitive();
	}
}
