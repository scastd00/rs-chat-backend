package rs.chat.ai.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.chat.domain.entity.User;
import rs.chat.domain.service.UserService;
import rs.chat.net.ws.strategies.messages.notifications.UserBlockedEvent;
import rs.chat.observability.metrics.Metrics;

import java.time.Clock;

import static rs.chat.utils.Constants.DEFAULT_BLOCK_DURATION_MILLIS;
import static rs.chat.utils.Constants.MINIMUM_NSFW_TO_BLOCK;

@Component
@RequiredArgsConstructor
@Slf4j
public class NSFWMessageEventListener implements ApplicationListener<NSFWUploadEvent> {
	private final UserService userService;
	private final Clock clock;
	private final Metrics metrics;
	private final ApplicationEventPublisher publisher;

	@Override
	public void onApplicationEvent(@NotNull NSFWUploadEvent event) {
		User user = this.userService.getUserById(event.getUserId());
		user.setNsfwCount((byte) (user.getNsfwCount() + 1));
		this.metrics.incrementMessageCount("nsfw");

		if (user.getNsfwCount() >= MINIMUM_NSFW_TO_BLOCK) {
			user.setBlockUntil(this.clock.instant().plusMillis(DEFAULT_BLOCK_DURATION_MILLIS.toMillis()));
			this.metrics.incrementBlockedUsers();
			log.info("User with id={} has been blocked for uploading NSFW content", user.getId());
			user.setNsfwCount((byte) 0); // Reset the counter

			// Send a notification to the user to disconnect from the chat.
			this.publisher.publishEvent(
					new UserBlockedEvent(this, user.getUsername(), "You have been blocked for uploading NSFW content.")
			);
		}

		this.userService.saveUser(user);
	}
}
