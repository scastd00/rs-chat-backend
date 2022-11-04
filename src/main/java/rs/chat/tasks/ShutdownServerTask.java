package rs.chat.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.WebSocketChatMap;

import java.util.concurrent.TimeUnit;

import static rs.chat.net.ws.Message.INFO_MESSAGE;
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
	private WebSocketChatMap webSocketChatMap;

	@Override
	public void run() throws TaskExecutionException {
		if (this.webSocketChatMap == null) {
			log.error("WebSocketChatMap is null, cannot send shutdown message.");
			throw new TaskExecutionException(
					TaskStatus.builder()
					          .status(TaskStatus.FATAL)
					          .message("WebSocketChatMap is null, cannot send shutdown message.")
					          .build()
			);
		}

		if (this.running) {
			log.warn("Server is already shutting down.");
			throw new TaskExecutionException(
					TaskStatus.builder()
					          .status(TaskStatus.WARNING)
					          .message("Server is already shutting down.")
					          .build()
			);
		}

		this.running = true;
		log.info("Shutting down server...");

		try {
			this.webSocketChatMap.totalBroadcast(
					createMessage(
							"Server is shutting down in %d %s.".formatted(this.delay, this.timeUnit.name().toLowerCase()),
							INFO_MESSAGE.type(),
							""
					)
			);

			Thread.sleep(this.timeUnit.toMillis(this.delay));

			this.webSocketChatMap.close();
			System.exit(0);
		} catch (Exception e) {
			log.error("Error while shutting down server.", e);

			throw new TaskExecutionException(
					TaskStatus.builder()
					          .status(TaskStatus.FATAL)
					          .message("Error while shutting down server.")
					          .build()
			);
		}
	}
}
