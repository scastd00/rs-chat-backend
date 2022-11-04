package rs.chat.net.ws.strategies;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import rs.chat.net.ws.Message;

import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.Message.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.Message.AUDIO_MESSAGE;
import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.Message.IMAGE_MESSAGE;
import static rs.chat.net.ws.Message.INFO_MESSAGE;
import static rs.chat.net.ws.Message.MAINTENANCE_MESSAGE;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrategyMappings {
	private static final Map<String, MessageStrategy> strategies = new HashMap<>();

	static {
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
		strategies.put(INFO_MESSAGE.type(), new InfoMessageStrategy());
		strategies.put(PING_MESSAGE.type(), new PingStrategy());
		strategies.put(ERROR_MESSAGE.type(), new ErrorMessageStrategy());
		strategies.put(RESTART_MESSAGE.type(), new RestartMessageStrategy());
		strategies.put(MAINTENANCE_MESSAGE.type(), new MaintenanceMessageStrategy());
	}

	/**
	 * Makes a decision on which strategy to use for handling the message.
	 *
	 * @param receivedMessageType message type received from the client.
	 *
	 * @return {@link MessageStrategy} to use for handling the message.
	 */
	@NotNull
	public static MessageStrategy decideStrategy(Message receivedMessageType) {
		return strategies.getOrDefault(receivedMessageType.type(), strategies.get(ERROR_MESSAGE.type()));
	}
}
