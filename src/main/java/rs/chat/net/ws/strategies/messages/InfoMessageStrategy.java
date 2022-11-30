package rs.chat.net.ws.strategies.messages;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.JsonMessageWrapper;
import rs.chat.net.ws.Message;
import rs.chat.tasks.ScheduleMessageTask;
import rs.chat.utils.Utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Strategy for handling {@link Message#INFO_MESSAGE} messages.
 */
@Slf4j
public class InfoMessageStrategy extends GenericMessageStrategy {
	@Override
	public void handle(JsonMessageWrapper wrappedMessage, ChatManagement chatManagement,
	                   Map<String, Object> otherData) throws WebSocketException, IOException {
		this.clearSensitiveDataChangeDateAndBuildResponse(wrappedMessage);

		if (!otherData.containsKey("schedule")) {
			// Send message immediately
			chatManagement.totalBroadcast(wrappedMessage.toString());
			return;
		}

		// Schedule message to be sent at a later time (specified in the "schedule" key)
		LocalDateTime schedule = (LocalDateTime) otherData.get("schedule");
		ScheduleMessageTask task = new ScheduleMessageTask(wrappedMessage, schedule);
		task.setChatManagement(chatManagement);
		Utils.executeTask(task, exception -> {
			log.error("Error while scheduling message.", exception);
			return null;
		});
	}
}
