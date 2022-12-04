package rs.chat.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.ChatManagement;

import java.util.concurrent.TimeUnit;

import static rs.chat.net.ws.Message.RESTART_MESSAGE;
import static rs.chat.utils.Utils.createMessage;

/**
 * Task for shutting down the server.
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class ShutdownServerTask implements Task {
	private boolean running = false;
	private final long delay;
	private final TimeUnit timeUnit;
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

		if (this.running) {
			log.warn("Server is already shutting down.");
			throw new TaskExecutionException(
					new TaskStatus(TaskStatus.WARNING, "Server is already shutting down.")
			);
		}

		this.running = true;
		log.info("Shutting down server...");

		try {
			this.chatManagement.totalBroadcast(
					createMessage(
							"Server is shutting down in %d %s.".formatted(this.delay, this.timeUnit.name().toLowerCase()),
							RESTART_MESSAGE.type(),
							""
					)
			);

			Thread.sleep(this.timeUnit.toMillis(this.delay));

			this.chatManagement.close();
			System.exit(0);
		} catch (Exception e) {
			log.error("Error while shutting down server.", e);

			throw new TaskExecutionException(
					new TaskStatus(TaskStatus.FATAL, "Error while shutting down server.")
			);
		}
	}
}
