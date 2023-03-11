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
import rs.chat.config.security.JWTService;
import rs.chat.exceptions.TokenValidationException;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.net.ws.strategies.messages.MessageStrategyMappings;
import rs.chat.observability.metrics.Metrics;
import rs.chat.rate.RateLimiter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.PING_MESSAGE;
import static rs.chat.net.ws.Message.TOO_FAST_MESSAGE;
import static rs.chat.net.ws.Message.USER_CONNECTED;
import static rs.chat.net.ws.Message.USER_DISCONNECTED;
import static rs.chat.utils.Constants.JWT_TOKEN_PREFIX;
import static rs.chat.utils.Constants.SCHEDULE_STRING;
import static rs.chat.utils.Utils.createMessage;

/**
 * WebSocket handler for the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private static final String EMPTY_TOKEN = JWT_TOKEN_PREFIX + "empty";
	private static final String CONNECTION_MESSAGE_CONTENT = "Connection";
	private static final String PING_MESSAGE_CONTENT = "I am a ping message";
	private static final String DISCONNECT_MESSAGE_CONTENT = "Disconnect";

	private final Metrics metrics;
	private final JWTService jwtService;
	private final RateLimiter rateLimiter;

	/**
	 * Handles text messages (JSON string).
	 *
	 * @param session remote WebSocket session of the client in the server.
	 * @param message message received from the client.
	 */
	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session,
	                                 @NotNull TextMessage message) throws IOException {
		long start = System.currentTimeMillis();
		JsonMessageWrapper wrappedMessage = new JsonMessageWrapper(message.getPayload());

		if (!wrappedMessage.correctStructure()) {
			log.debug("The message is not in the correct format");
			sendQuickResponse(session, "The message is not in the correct format.", ERROR_MESSAGE, wrappedMessage);
			return;
		}

		// The method call will return true in the following situations:
		//   - when the user connects to the server (without token).
		//   - when the user sends a PING message, being in a chat (with token) or not (without token).
		//   - when the user disconnects from the server (without token).
		boolean isEmptyToken = wrappedMessage.token().equals(EMPTY_TOKEN);
		if (isEmptyToken && !isMessageToIgnoreTokenCheck(wrappedMessage)) {
			sendQuickResponse(session, "You cannot send messages without a valid token.", ERROR_MESSAGE, wrappedMessage);
			throw new TokenValidationException("You cannot send messages without a valid token.");
		}

		////// TOKEN STATE //////
		// If I am here, the state is one of the following:
		// 1. One of the conditions (of the comment) above is true.
		// 2. We have a token (valid or not) and the user is sending a message (not a connection one).

		// Decrease the rate limit counter for the user.
		if (Message.typeBelongsToGroup(wrappedMessage.type(), Message.NORMAL_RECEPTION_MESSAGES) &&
				!this.rateLimiter.isAllowedAndDecrease(wrappedMessage.username())) {
			sendQuickResponse(session, "You are sending messages too fast.", TOO_FAST_MESSAGE, wrappedMessage);
			return;
		}

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

		if (hasTextBody && this.isParseable(wrappedMessage.content())) {
			receivedMessageType = Message.PARSEABLE_MESSAGE;
		}

		MessageStrategy strategy = MessageStrategyMappings.decideStrategy(receivedMessageType);

		try {
			// If the token is empty, we do not need to validate it, because the user is
			// connecting to the server (Check the TOKEN STATE comment above).
			if (!isEmptyToken && this.jwtService.isInvalidToken(wrappedMessage.token())) {
				throw new TokenValidationException("Invalid token.");
			}

			strategy.handle(new MessageHandlingDTO(wrappedMessage, otherData));

			this.metrics.incrementMessageCount(receivedMessageType.type());
			this.metrics.incrementMessageTime(receivedMessageType.type(), System.currentTimeMillis() - start);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Sends a message directly to the client.
	 *
	 * @param session        remote WebSocket session of the client in the server.
	 * @param content        content of the message.
	 * @param errorMessage   type of the message.
	 * @param wrappedMessage message to get the chat ID from.
	 *
	 * @throws IOException if an I/O error occurs while sending the message.
	 */
	private void sendQuickResponse(@NotNull WebSocketSession session, String content,
	                               Message errorMessage, JsonMessageWrapper wrappedMessage) throws IOException {
		session.sendMessage(new TextMessage(
				createMessage(
						content,
						errorMessage.type(),
						wrappedMessage.chatId()
				)
		));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleTransportError(@NotNull WebSocketSession session,
	                                 @NotNull Throwable exception) throws IOException {
		log.error(exception.getMessage(), exception);
		session.close(CloseStatus.SERVER_ERROR);
	}

	/**
	 * Checks if the message must contain a valid token, or it could be ignored.
	 *
	 * @param wrappedMessage message to check.
	 *
	 * @return {@code true} if the message must contain a valid token, {@code false} otherwise.
	 */
	private boolean isMessageToIgnoreTokenCheck(JsonMessageWrapper wrappedMessage) {
		String type = wrappedMessage.type();
		String content = wrappedMessage.content();
		boolean connectionMessage = type.equals(USER_CONNECTED.type()) && content.equals(CONNECTION_MESSAGE_CONTENT);
		boolean pingMessage = type.equals(PING_MESSAGE.type()) && content.equals(PING_MESSAGE_CONTENT);
		boolean disconnectMessage = type.equals(USER_DISCONNECTED.type()) && content.equals(DISCONNECT_MESSAGE_CONTENT);

		return connectionMessage || pingMessage || disconnectMessage;
	}

	/**
	 * Checks if the message is a parseable message.
	 *
	 * @param message message to check.
	 *
	 * @return true if the message is a parseable message, false otherwise.
	 */
	private boolean isParseable(String message) {
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
		return wrappedMessage.body().get("content").isJsonPrimitive();
	}
}
