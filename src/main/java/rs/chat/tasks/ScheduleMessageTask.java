package rs.chat.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Task for sending scheduled messages.
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class ScheduleMessageTask implements Task {
	private final JsonMessageWrapper message;
	private final LocalDateTime schedule;

	@Setter
	private ChatManagement chatManagement;

	@Override
	public void run() throws TaskExecutionException {
		if (this.chatManagement == null) {
			log.error("ChatManagement is null, cannot send shutdown message.");
			throw new TaskExecutionException(
					new TaskStatus(TaskStatus.FATAL, "ChatManagement is null, cannot send shutdown message.")
			);
		}

		long millisToWait = LocalDateTime.now().until(this.schedule, ChronoUnit.MILLIS);
		log.info("Sending scheduled message: {} (waiting {} milliseconds)", this.message.type(), millisToWait);

		try {
			Thread.sleep(millisToWait);
		} catch (InterruptedException e) {
			log.error("Error while waiting for scheduled message.", e);
			throw new TaskExecutionException(
					new TaskStatus(TaskStatus.FATAL, "Error while waiting for scheduled message.")
			);
		}

		this.message.updateDateTime(); // To set the date to the current time (after waiting)
		this.chatManagement.totalBroadcast(this.message.toString());
	}
}
