package rs.chat.ai.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.chat.domain.entity.User;
import rs.chat.domain.service.SessionService;
import rs.chat.domain.service.UserService;
import rs.chat.net.ws.strategies.messages.notifications.UserBlockedEvent;
import rs.chat.observability.metrics.Metrics;

import java.time.Clock;
import java.util.concurrent.locks.ReentrantLock;

import static rs.chat.Constants.DEFAULT_BLOCK_DURATION_MILLIS;
import static rs.chat.Constants.MINIMUM_NSFW_TO_BLOCK;

@Component
@RequiredArgsConstructor
@Slf4j
public class NSFWMessageEventListener implements ApplicationListener<NSFWUploadEvent> {
	private final UserService userService;
	private final Clock clock;
	private final Metrics metrics;
	private final ApplicationEventPublisher publisher;
	private final SessionService sessionService;

	/**
	 * Lock used to synchronize the access to the user's NSFW count in the database.
	 */
	private final ReentrantLock lock = new ReentrantLock();

	@Override
	public void onApplicationEvent(@NotNull NSFWUploadEvent event) {
		this.lock.lock();
		User user = this.userService.getUserById(event.getUserId());

		// If the user is already blocked, we don't need to do anything.
		if (user.getBlockUntil() != null && user.getBlockUntil().isAfter(this.clock.instant())) {
			this.lock.unlock();
			return;
		}

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

			// Since the user is blocked, we need to disconnect them from the chat.
			this.sessionService.deleteAllSessionsOfUser(user.getUsername());
		}

		this.userService.saveUser(user);
		this.lock.unlock();
	}
}
