package rs.chat.tasks;

import lombok.Getter;
import rs.chat.tasks.Task.TaskStatus;

@Getter
public class TaskExecutionException extends RuntimeException {
	private final transient TaskStatus status;

	public TaskExecutionException(TaskStatus taskStatus) {
		super(taskStatus.message());
		this.status = taskStatus;
	}
}
