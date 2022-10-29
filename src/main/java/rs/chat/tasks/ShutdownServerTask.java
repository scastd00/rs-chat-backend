package rs.chat.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@RequiredArgsConstructor
public class ShutdownServerTask implements Task {
	private boolean running = false;
	private final long delay;
	private final TimeUnit timeUnit;

	@Override
	public void run() {
		if (this.running) {
			log.info("Server is already shutting down.");
			return;
		}

		this.running = true;
		log.info("Shutting down server...");

		try {
			Thread.sleep(this.timeUnit.toMillis(this.delay));
			System.exit(0);
		} catch (Exception e) {
			log.error("Error while shutting down server.", e);
		}
	}
}
