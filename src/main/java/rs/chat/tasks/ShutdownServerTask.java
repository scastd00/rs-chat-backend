package rs.chat.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rs.chat.net.ws.WebSocketChatMap;

import java.util.concurrent.TimeUnit;

import static rs.chat.net.ws.WSMessage.SERVER_INFO_MESSAGE;
import static rs.chat.utils.Utils.createServerMessage;

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
	public void run() {
		if (this.running) {
			log.info("Server is already shutting down.");
			return;
		}

		this.running = true;
		log.info("Shutting down server...");

		try {
			this.webSocketChatMap.totalBroadcast(
					createServerMessage("Server is shutting down.", SERVER_INFO_MESSAGE.type(), "")
			);
			this.webSocketChatMap.close();

			Thread.sleep(this.timeUnit.toMillis(this.delay));
			System.exit(0);
		} catch (Exception e) {
			log.error("Error while shutting down server.", e);
		}
	}
}
