package rs.chat.net.ws.strategies.messages;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.strategies.messages.impl.ActiveUsersStrategy;
import rs.chat.net.ws.strategies.messages.impl.AudioMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.ErrorMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.GetHistoryStrategy;
import rs.chat.net.ws.strategies.messages.impl.ImageMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.InfoMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.MaintenanceMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.ParseableMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.PdfMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.PingStrategy;
import rs.chat.net.ws.strategies.messages.impl.RestartMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.TextDocMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.TextMessageStrategy;
import rs.chat.net.ws.strategies.messages.impl.UserConnectedStrategy;
import rs.chat.net.ws.strategies.messages.impl.UserDisconnectedStrategy;
import rs.chat.net.ws.strategies.messages.impl.UserJoinedStrategy;
import rs.chat.net.ws.strategies.messages.impl.UserLeftStrategy;
import rs.chat.net.ws.strategies.messages.impl.UserStoppedTypingStrategy;
import rs.chat.net.ws.strategies.messages.impl.UserTypingStrategy;
import rs.chat.net.ws.strategies.messages.impl.VideoMessageStrategy;
import rs.chat.rate.RateLimiter;

import java.util.HashMap;
import java.util.Map;

import static rs.chat.net.ws.Message.ACTIVE_USERS_MESSAGE;
import static rs.chat.net.ws.Message.AUDIO_MESSAGE;
import static rs.chat.net.ws.Message.ERROR_MESSAGE;
import static rs.chat.net.ws.Message.GET_HISTORY_MESSAGE;
import static rs.chat.net.ws.Message.IMAGE_MESSAGE;
import static rs.chat.net.ws.Message.INFO_MESSAGE;
import static rs.chat.net.ws.Message.MAINTENANCE_MESSAGE;
import static rs.chat.net.ws.Message.PARSEABLE_MESSAGE;
import static rs.chat.net.ws.Message.PDF_MESSAGE;
import static rs.chat.net.ws.Message.PING_MESSAGE;
import static rs.chat.net.ws.Message.RESTART_MESSAGE;
import static rs.chat.net.ws.Message.TEXT_DOC_MESSAGE;
import static rs.chat.net.ws.Message.TEXT_MESSAGE;
import static rs.chat.net.ws.Message.USER_CONNECTED;
import static rs.chat.net.ws.Message.USER_DISCONNECTED;
import static rs.chat.net.ws.Message.USER_JOINED;
import static rs.chat.net.ws.Message.USER_LEFT;
import static rs.chat.net.ws.Message.USER_STOPPED_TYPING;
import static rs.chat.net.ws.Message.USER_TYPING;
import static rs.chat.net.ws.Message.VIDEO_MESSAGE;

/**
 * Utility class for mapping {@link Message} to {@link MessageStrategy}.
 * <p>
 * This class is used to get the correct strategy for a given message.
 * The mapping is done by the {@link Message#type()} method.
 * <br>
 * The mapping is done statically, so the mappings are created when the class is loaded.
 * <br>
 * This is done to avoid the overhead of creating the mappings every time a new strategy is needed. This is
 * especially important when the application is under heavy load. The mappings are created only once, when the
 * application is started.
 */
@Component
public final class MessageStrategyMappings {
	private static final Map<String, MessageStrategy> strategies = new HashMap<>();
	private final ChatManagement chatManagement;
	private final RateLimiter rateLimiter;
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public MessageStrategyMappings(ChatManagement chatManagement, RateLimiter rateLimiter, ApplicationEventPublisher eventPublisher) {
		this.chatManagement = chatManagement;
		this.rateLimiter = rateLimiter;
		this.eventPublisher = eventPublisher;
		this.initStrategies();
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

	private void initStrategies() {
		strategies.put(USER_CONNECTED.type(), new UserConnectedStrategy());
		strategies.put(USER_DISCONNECTED.type(), new UserDisconnectedStrategy());
		strategies.put(USER_TYPING.type(), new UserTypingStrategy());
		strategies.put(USER_STOPPED_TYPING.type(), new UserStoppedTypingStrategy());

		strategies.put(USER_JOINED.type(), new UserJoinedStrategy(this.chatManagement));
		strategies.put(USER_LEFT.type(), new UserLeftStrategy(this.chatManagement, this.rateLimiter));

		strategies.put(TEXT_MESSAGE.type(), new TextMessageStrategy(this.chatManagement, this.eventPublisher));
		strategies.put(IMAGE_MESSAGE.type(), new ImageMessageStrategy(this.chatManagement, this.eventPublisher));
		strategies.put(AUDIO_MESSAGE.type(), new AudioMessageStrategy(this.chatManagement, this.eventPublisher));
		strategies.put(VIDEO_MESSAGE.type(), new VideoMessageStrategy(this.chatManagement, this.eventPublisher));
		strategies.put(PDF_MESSAGE.type(), new PdfMessageStrategy(this.chatManagement, this.eventPublisher));
		strategies.put(TEXT_DOC_MESSAGE.type(), new TextDocMessageStrategy(this.chatManagement, this.eventPublisher));
		strategies.put(PARSEABLE_MESSAGE.type(), new ParseableMessageStrategy(this.chatManagement, this.eventPublisher)); // Mentions and commands

		strategies.put(ACTIVE_USERS_MESSAGE.type(), new ActiveUsersStrategy(this.chatManagement));
		strategies.put(GET_HISTORY_MESSAGE.type(), new GetHistoryStrategy());
		strategies.put(INFO_MESSAGE.type(), new InfoMessageStrategy(this.chatManagement));
		strategies.put(PING_MESSAGE.type(), new PingStrategy());
		strategies.put(ERROR_MESSAGE.type(), new ErrorMessageStrategy());
		strategies.put(RESTART_MESSAGE.type(), new RestartMessageStrategy(this.chatManagement));
		strategies.put(MAINTENANCE_MESSAGE.type(), new MaintenanceMessageStrategy(this.chatManagement));
	}
}
