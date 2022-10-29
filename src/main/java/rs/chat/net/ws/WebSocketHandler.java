package rs.chat.net.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.strategies.message.ActiveUsersStrategy;
import rs.chat.strategies.message.AudioMessageStrategy;
import rs.chat.strategies.message.ErrorMessageStrategy;
import rs.chat.strategies.message.GetHistoryStrategy;
import rs.chat.strategies.message.ImageMessageStrategy;
import rs.chat.strategies.message.MessageStrategy;
import rs.chat.strategies.message.PdfMessageStrategy;
import rs.chat.strategies.message.PingStrategy;
import rs.chat.strategies.message.ServerRestartMessage;
import rs.chat.strategies.message.TextDocMessageStrategy;
import rs.chat.strategies.message.TextMessageStrategy;
import rs.chat.strategies.message.UserConnectedStrategy;
import rs.chat.strategies.message.UserDisconnectedStrategy;
import rs.chat.strategies.message.UserJoinedStrategy;
import rs.chat.strategies.message.UserLeftStrategy;
import rs.chat.strategies.message.VideoMessageStrategy;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.Message.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.Message.AUDIO_MESSAGE;
import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.Message.IMAGE_MESSAGE;
import static rs.chat.net.ws.Message.PDF_MESSAGE;
import static rs.chat.net.ws.Message.PING_MESSAGE;
import static rs.chat.net.ws.Message.RESTART_MESSAGE;
import static rs.chat.net.ws.Message.TEXT_DOC_MESSAGE;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.net.ws.Message.USER_CONNECTED;
import static rs.chat.net.ws.Message.USER_DISCONNECTED;
import static rs.chat.net.ws.Message.USER_JOINED;
import static rs.chat.net.ws.Message.USER_LEFT;
import static rs.chat.net.ws.Message.VIDEO_MESSAGE;
import static rs.chat.utils.Utils.createServerErrorMessage;

/**
 * WebSocket handler for the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private static final Map<String, MessageStrategy> strategies = new HashMap<>();
	private final WebSocketChatMap chatMap;

	static {
		// Todo: this increases speed because instances are created once and then reused.
		//  If we want to save memory, we can create a new instance every time.
		strategies.put(USER_CONNECTED.type(), new UserConnectedStrategy());
		strategies.put(USER_DISCONNECTED.type(), new UserDisconnectedStrategy());

		strategies.put(USER_JOINED.type(), new UserJoinedStrategy());
		strategies.put(USER_LEFT.type(), new UserLeftStrategy());

		strategies.put(TEXT_MESSAGE.type(), new TextMessageStrategy());
		strategies.put(IMAGE_MESSAGE.type(), new ImageMessageStrategy());
		strategies.put(AUDIO_MESSAGE.type(), new AudioMessageStrategy());
		strategies.put(VIDEO_MESSAGE.type(), new VideoMessageStrategy());
		strategies.put(PDF_MESSAGE.type(), new PdfMessageStrategy());
		strategies.put(TEXT_DOC_MESSAGE.type(), new TextDocMessageStrategy());

		strategies.put(ACTIVE_USERS_MESSAGE.type(), new ActiveUsersStrategy());
		strategies.put(GET_HISTORY_MESSAGE.type(), new GetHistoryStrategy());
		strategies.put(PING_MESSAGE.type(), new PingStrategy());
		strategies.put(ERROR_MESSAGE.type(), new ErrorMessageStrategy());
		strategies.put(RESTART_MESSAGE.type(), new ServerRestartMessage());
	}

	/**
	 * Handles text messages (JSON string).
	 *
	 * @param session remote WebSocket session of the client in the server.
	 * @param message message received from the client.
	 */
	@Override
	protected void handleTextMessage(@NotNull WebSocketSession session,
	                                 @NotNull TextMessage message) {
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
		MessageStrategy strategy = this.decideStrategy(receivedMessageType);

		try {
			log.info("Handling message: {} by class {}.", receivedMessageType.type(), strategy.getClass().getSimpleName());
			Utils.checkTokenValidity(wrappedMessage.token());
			strategy.handle(wrappedMessage, this.chatMap, otherData);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Makes a decision on which strategy to use for handling the message.
	 *
	 * @param receivedMessageType message type received from the client.
	 *
	 * @return strategy to use for handling the message.
	 */
	@NotNull
	private MessageStrategy decideStrategy(Message receivedMessageType) {
		return strategies.getOrDefault(receivedMessageType.type(), strategies.get(ERROR_MESSAGE.type()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.sendMessage(
				new TextMessage(createServerErrorMessage(exception.getMessage()))
		);
	}
}
