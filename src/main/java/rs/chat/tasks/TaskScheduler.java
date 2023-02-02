package rs.chat.tasks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskScheduler {
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE;

	static {
		SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(4, runnable -> {
			Thread t = new Thread(runnable);
			t.setDaemon(true);
			return t;
		});
	}

	public static void executeTaskInsecure(Task task) {
		SCHEDULED_EXECUTOR_SERVICE.execute(task);
	}

	public static void executeTaskSecure(Task task, Function<TaskExecutionException, Void> exceptionHandler) {
		SCHEDULED_EXECUTOR_SERVICE.execute(() -> {
			try {
				task.run();
			} catch (TaskExecutionException e) {
				exceptionHandler.apply(e);
			}
		});
	}

	public static void schedule(Task task, int delay, int interval, TimeUnit timeUnit) {
		SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
			try {
				task.run();
			} catch (TaskExecutionException e) {
				log.error("Error executing periodic task", e);
			}
		}, delay, interval, timeUnit);
	}
}
