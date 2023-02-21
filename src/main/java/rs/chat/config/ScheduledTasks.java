package rs.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import rs.chat.domain.entity.Session;
import rs.chat.domain.service.SessionService;

import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasks {
	private final SessionService sessionService;

	/**
	 * Deletes all expired sessions from the database every 15 minutes.
	 * <p>
	 * Note: Spring Boot fails if the method is private
	 */
	@Scheduled(fixedRate = 15, initialDelay = 15, timeUnit = MINUTES)
	private void deleteExpiredSessions() {
		this.sessionService.deleteAllById(this.getExpiredSessions());
	}

	/**
	 * Finds all expired sessions in the database.
	 *
	 * @return the found expired sessions.
	 */
	private List<Long> getExpiredSessions() {
		return this.sessionService.getAll()
		                          .stream()
		                          .filter(this.sessionService::isExpiredSession)
		                          .map(Session::getId)
		                          .toList();
	}
}
