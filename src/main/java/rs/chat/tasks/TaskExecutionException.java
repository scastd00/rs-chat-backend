package rs.chat.tasks;

import lombok.Getter;
import rs.chat.tasks.Task.TaskStatus;

@Getter
public class TaskExecutionException extends RuntimeException {
	private final TaskStatus status;

	public TaskExecutionException(TaskStatus taskStatus) {
		super(taskStatus.getMessage());
		this.status = taskStatus;
	}
}
