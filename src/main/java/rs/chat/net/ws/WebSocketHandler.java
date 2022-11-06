package rs.chat.net.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.strategies.messages.MessageStrategy;
import rs.chat.net.ws.strategies.messages.StrategyMappings;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.utils.Utils.createErrorMessage;

/**
 * WebSocket handler for the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private final WebSocketChatMap chatMap;

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

		// Strategy pattern for handling messages.
		MessageStrategy strategy = StrategyMappings.decideStrategy(receivedMessageType);

		try {
			log.debug("Handling message: {} by class {}.", receivedMessageType.type(), strategy.getClass().getSimpleName());
			Utils.checkTokenValidity(wrappedMessage.token());
			strategy.handle(wrappedMessage, this.chatMap, otherData);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.sendMessage(
				new TextMessage(createErrorMessage(exception.getMessage()))
		);
	}
}
