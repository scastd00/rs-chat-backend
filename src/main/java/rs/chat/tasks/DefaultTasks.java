package rs.chat.tasks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultTasks {
	public static final ShutdownServerTask SHUTDOWN = new ShutdownServerTask(5, TimeUnit.MINUTES);
}
