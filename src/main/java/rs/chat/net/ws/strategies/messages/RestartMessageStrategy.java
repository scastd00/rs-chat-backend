package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.tasks.DefaultTasks;
import rs.chat.tasks.ShutdownServerTask;
import rs.chat.tasks.Task.TaskStatus;
import rs.chat.tasks.TaskExecutionException;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rs.chat.net.ws.Message.INFO_MESSAGE;

/**
 * Strategy for handling {@link Message#RESTART_MESSAGE} messages.
 */
@Slf4j
public class RestartMessageStrategy extends GenericScheduledMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		ShutdownServerTask shutdownTask = DefaultTasks.SHUTDOWN;

		if (otherData.containsKey("schedule")) {
			LocalDateTime schedule = (LocalDateTime) otherData.get("schedule");
			long delaySeconds = LocalDateTime.now().until(schedule, ChronoUnit.SECONDS);

			if (delaySeconds >= 0) {
				// If the delay is valid, set the new delay and time unit.
				shutdownTask = new ShutdownServerTask(delaySeconds, TimeUnit.SECONDS);
			}
		}

		shutdownTask.setChatManagement(chatManagement);

		Utils.executeTask(shutdownTask, exception -> {
			try {
				getSession(otherData).sendMessage(new TextMessage(
						Utils.createMessage(
								"An error occurred while shutting down the server (%s)".formatted(exception.getStatus().message()),
								INFO_MESSAGE.type(),
								""
						)
				));
			} catch (IOException e) {
				throw new TaskExecutionException(new TaskStatus(TaskStatus.FAILURE, e.getMessage()));
			}

			return null;
		});
	}
}
