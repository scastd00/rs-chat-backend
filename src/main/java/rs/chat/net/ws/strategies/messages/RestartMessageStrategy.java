package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.net.ws.WebSocketChatMap;
import rs.chat.tasks.DefaultTasks;
import rs.chat.tasks.ShutdownServerTask;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.util.Map;

/**
 * Strategy for handling {@link Message#RESTART_MESSAGE} messages.
 */
@Slf4j
public class RestartMessageStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, WebSocketChatMap webSocketChatMap,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		ShutdownServerTask shutdownTask = DefaultTasks.SHUTDOWN;
		shutdownTask.setWebSocketChatMap(webSocketChatMap);

		Utils.executeTask(shutdownTask, exception -> {
			try {
				WebSocketSession session = (WebSocketSession) otherData.get("session");
				session.sendMessage(new TextMessage(
						Utils.createMessage(
								"An error occurred while shutting down the server.%n%s".formatted(exception.getStatus().getMessage()),
								Message.INFO_MESSAGE.type(),
								""
						)
				));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return null;
		});
	}
}
