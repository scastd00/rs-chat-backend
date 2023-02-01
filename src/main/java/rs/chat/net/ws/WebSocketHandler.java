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
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.net.ws.strategies.messages.MessageStrategyMappings;
import rs.chat.observability.metrics.Metrics;
import rs.chat.rate.RateLimit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.PING_MESSAGE;
import static rs.chat.net.ws.Message.TOO_FAST_MESSAGE;
import static rs.chat.net.ws.Message.USER_CONNECTED;
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
	private final ChatManagement chatManagement;
	private final Metrics metrics;
	private final JWTService jwtService;
	private final RateLimit rateLimit = new RateLimit(10);

	private static final String EMPTY_TOKEN = JWT_TOKEN_PREFIX + "empty";
	private static final String CONNECTION_MESSAGE_CONTENT = "Connection";

	/**
	 * Handles text messages (JSON string).
	 *
	 * @param session remote WebSocket session of the client in the server.
	 * @param message message received from the client.
	 */
	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session,
	                                 @NotNull TextMessage message) throws IOException {
		// FIXME: A user that did not send the USER_JOINED message could send messages
		//  but cannot receive them.

		long start = System.currentTimeMillis();
		JsonMessageWrapper wrappedMessage = new JsonMessageWrapper(message.getPayload());

		// The second condition is only executed once (when the user connects to the server,
		// in a controlled situation). Then, the user will have a valid token.
		boolean isEmptyToken = wrappedMessage.token().equals(EMPTY_TOKEN);
		if (isEmptyToken && !isConnectionOrPingMessage(wrappedMessage)) {
			sendQuickResponse(session, "You cannot send messages without a valid token.", ERROR_MESSAGE, wrappedMessage);
			throw new TokenValidationException("You cannot send messages without a valid token.");
		}

		////// TOKEN STATE //////
		// If I am here, the state is one of the following:
		// 1. Empty token and the user is connecting to the server.
		// 2. We have a token (valid or not) and the user is sending a message (not a connection one).

		// Decrease the rate limit counter for the user.
		if (Message.typeBelongsToGroup(wrappedMessage.type(), Message.NORMAL_MESSAGES) &&
				!this.rateLimit.isAllowedAndDecrease(wrappedMessage.username())) {
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

			strategy.handle(wrappedMessage, this.chatManagement, otherData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		this.metrics.incrementMessageCount(receivedMessageType.type());
		this.metrics.incrementMessageTime(receivedMessageType.type(), System.currentTimeMillis() - start);
	}

	private static void sendQuickResponse(@NotNull WebSocketSession session, String content,
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
	 * Checks if the message is a connection one, this is, a message that has the type
	 * {@link Message#USER_CONNECTED}{@link Message#type() .type()} and the content
	 * {@link WebSocketHandler#CONNECTION_MESSAGE_CONTENT} or
	 * {@link Message#PING_MESSAGE}{@link Message#type() .type()}.
	 *
	 * @param wrappedMessage message to check.
	 *
	 * @return {@code true} if the message is a connection or a ping one, {@code false} otherwise.
	 */
	private boolean isConnectionOrPingMessage(JsonMessageWrapper wrappedMessage) {
		boolean connectionMessage = wrappedMessage.type().equals(USER_CONNECTED.type()) &&
				wrappedMessage.content().equals(CONNECTION_MESSAGE_CONTENT);

		return connectionMessage || wrappedMessage.type().equals(PING_MESSAGE.type());
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
