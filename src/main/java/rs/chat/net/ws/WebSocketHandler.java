package rs.chat.net.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.net.ws.strategies.messages.MessageStrategyMappings;
import rs.chat.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket handler for the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private final ChatManagement chatManagement;

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

		if (wrappedMessage.content().contains("#SCHEDULE#")) {
			String[] strings = wrappedMessage.content().split("#SCHEDULE#");
			otherData.put("schedule", LocalDateTime.parse(strings[1], DateTimeFormatter.ISO_DATE_TIME));
			wrappedMessage.setContent(strings[0]);
		}

		// Strategy pattern for handling messages.
		MessageStrategy strategy;

		if (this.isParseableMessage(wrappedMessage.content())) {
			strategy = MessageStrategyMappings.decideStrategy(Message.PARSEABLE_MESSAGE);
		} else {
			strategy = MessageStrategyMappings.decideStrategy(receivedMessageType);
		}

		try {
			Utils.checkTokenValidity(wrappedMessage.token());
			strategy.handle(wrappedMessage, this.chatManagement, otherData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) {
		log.error(exception.getMessage(), exception);
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
}
