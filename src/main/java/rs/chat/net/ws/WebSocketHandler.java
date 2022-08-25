package rs.chat.net.ws;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rs.chat.net.ws.strategies.ActiveUsersStrategy;
import rs.chat.net.ws.strategies.AudioMessageStrategy;
import rs.chat.net.ws.strategies.ErrorMessageStrategy;
import rs.chat.net.ws.strategies.GetHistoryStrategy;
import rs.chat.net.ws.strategies.ImageMessageStrategy;
import rs.chat.net.ws.strategies.MessageStrategy;
import rs.chat.net.ws.strategies.TextMessageStrategy;
import rs.chat.net.ws.strategies.UserJoinedStrategy;
import rs.chat.net.ws.strategies.UserLeftStrategy;
import rs.chat.net.ws.strategies.VideoMessageStrategy;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.WSMessage.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.WSMessage.AUDIO_MESSAGE;
import static rs.chat.net.ws.WSMessage.ERROR_MESSAGE;
import static rs.chat.net.ws.WSMessage.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.WSMessage.IMAGE_MESSAGE;
import static rs.chat.net.ws.WSMessage.TEXT_MESSAGE;
import static rs.chat.net.ws.WSMessage.USER_JOINED;
import static rs.chat.net.ws.WSMessage.USER_LEFT;
import static rs.chat.net.ws.WSMessage.VIDEO_MESSAGE;
import static rs.chat.utils.Utils.createServerErrorMessage;
import static rs.chat.utils.Utils.createServerMessage;

/**
 * WebSocket handler for the application.
 */
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
	private final WebSocketChatMap chatMap = new WebSocketChatMap();

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
		WSMessage receivedMessageType = new WSMessage(wrappedMessage.type(), null, null);

		Map<String, Object> otherData = new HashMap<>();
		otherData.put("session", session);
		otherData.put("wsMessage", receivedMessageType);
		otherData.put("wsClientID", new WSClientID(
				wrappedMessage.username(),
				wrappedMessage.chatId(),
				wrappedMessage.sessionId()
		));

		// Strategy pattern for handling messages.
		MessageStrategy strategy = this.decideStrategy(receivedMessageType);

		try {
			strategy.checkTokenValidity(wrappedMessage.token());
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
	private MessageStrategy decideStrategy(WSMessage receivedMessageType) {
		if (USER_JOINED.equals(receivedMessageType)) {
			return new UserJoinedStrategy();
		} else if (USER_LEFT.equals(receivedMessageType)) {
			return new UserLeftStrategy();
		} else if (TEXT_MESSAGE.equals(receivedMessageType)) {
			return new TextMessageStrategy();
		} else if (IMAGE_MESSAGE.equals(receivedMessageType)) {
			return new ImageMessageStrategy();
		} else if (AUDIO_MESSAGE.equals(receivedMessageType)) {
			return new AudioMessageStrategy();
		} else if (VIDEO_MESSAGE.equals(receivedMessageType)) {
			return new VideoMessageStrategy();
		} else if (ACTIVE_USERS_MESSAGE.equals(receivedMessageType)) {
			return new ActiveUsersStrategy();
		} else if (GET_HISTORY_MESSAGE.equals(receivedMessageType)) {
			return new GetHistoryStrategy();
		} else {
			return new ErrorMessageStrategy();
		}
	}

	/**
	 * This method receives binary messages and treats them as needed.
	 * (Might be used for the media transferred through the websocket).
	 *
	 * @param session socket session of the connected client.
	 * @param message binary message received from the client.
	 */
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		// Todo: send from client the next structure
		/*
		 * 1st byte: length of header data
		 * Next bytes: header containing metadata of the file received.
		 */
		try {
			session.sendMessage(new BinaryMessage(
					Base64.getEncoder().encode(message.getPayload())
			));
		} catch (IOException e) {
			try {
				session.sendMessage(new TextMessage(
						createServerMessage(
								"ERROR: type property is not present in the content of the JSON",
								ERROR_MESSAGE.type(),
								"TODO" // Todo: get the chat id
						))
				);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
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
