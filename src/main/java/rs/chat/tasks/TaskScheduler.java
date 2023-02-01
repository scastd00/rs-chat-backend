package rs.chat.tasks;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class TaskScheduler {
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE;

	static {
		ThreadFactory threadFactory = r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		};

		SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(4, threadFactory);
	}

	public static void schedule(Task task) {
		SCHEDULED_EXECUTOR_SERVICE.execute(() -> {
			try {
				task.run();
			} catch (TaskExecutionException e) {
				log.error("Error executing task", e);
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
