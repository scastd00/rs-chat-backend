package rs.chat.net.ws.strategies.messages.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.chat.net.ws.ChatManagement;
import rs.chat.utils.Utils;

import static rs.chat.utils.Constants.SERVER_CHAT_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener implements ApplicationListener<NotificationEvent> {
	private final ChatManagement chatManagement;

	@Override
	public void onApplicationEvent(@NotNull NotificationEvent event) {
		String username = event.getUsername();
		String message = event.getMessage();

		log.info("Sending notification to user with username={}, message={}", username, message);

		this.chatManagement.sendNotificationTo(username, Utils.createMessage(
				message, event.getNotificationType(), SERVER_CHAT_ID
		));
	}
}
