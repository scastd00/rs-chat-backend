package rs.chat.net.ws.strategies.messages.impl;

import lombok.extern.slf4j.Slf4j;
import rs.chat.exceptions.WebSocketException;
import rs.chat.net.ws.ChatManagement;
import rs.chat.net.ws.strategies.messages.MessageHandlingDTO;
import rs.chat.tasks.ScheduleMessageTask;
import rs.chat.tasks.TaskScheduler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Strategy for handling messages that can be scheduled to be sent at a later time without
 * any processing.
 */
@Slf4j
public class GenericScheduledMessageStrategy extends GenericMessageStrategy {
	public GenericScheduledMessageStrategy(ChatManagement chatManagement) {
		super(chatManagement);
	}

	@Override
	public void handle(MessageHandlingDTO handlingDTO) throws WebSocketException, IOException {
		this.clearSensitiveDataChangeDateAndBuildResponse(handlingDTO.wrappedMessage());

		if (!handlingDTO.otherData().containsKey("schedule")) {
			// Send message immediately
			chatManagement.totalBroadcast(handlingDTO.wrappedMessage().toString());
			return;
		}

		// Schedule message to be sent at a later time (specified in the "schedule" key)
		LocalDateTime schedule = (LocalDateTime) handlingDTO.otherData().get("schedule");
		ScheduleMessageTask task = new ScheduleMessageTask(handlingDTO.wrappedMessage(), schedule);
		task.setChatManagement(chatManagement);

		TaskScheduler.executeTaskSecure(task, exception -> {
			log.error("Error while scheduling message.", exception);
			return null;
		});
	}
}
