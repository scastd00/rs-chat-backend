package rs.chat.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public enum DefaultTasks {
	SHUTDOWN(new ShutdownServerTask(5, TimeUnit.SECONDS)),
	;

	private final Task task;
}
