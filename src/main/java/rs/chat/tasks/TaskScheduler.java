package rs.chat.tasks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A task scheduler that executes tasks in separate threads.
 */
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

	/**
	 * Executes a task in a separate thread without catching any exceptions.
	 *
	 * @param task the task to execute.
	 */
	public static void executeTaskInsecure(Task task) {
		SCHEDULED_EXECUTOR_SERVICE.execute(task);
	}

	/**
	 * Executes a task in a separate thread and catches any {@link TaskExecutionException} that may occur.
	 *
	 * @param task             the task to execute.
	 * @param exceptionHandler the exception handler to use.
	 */
	public static void executeTaskSecure(Task task, Function<TaskExecutionException, Void> exceptionHandler) {
		SCHEDULED_EXECUTOR_SERVICE.execute(() -> {
			try {
				task.run();
			} catch (TaskExecutionException e) {
				exceptionHandler.apply(e);
			}
		});
	}

	/**
	 * Schedules a task to be executed in a separate thread after a specified delay with
	 * a specified interval.
	 *
	 * @param task     the task to execute.
	 * @param delay    the delay before the task is executed for the first time.
	 * @param interval the interval between each execution.
	 * @param timeUnit the time unit to use for the delay and interval.
	 */
	public static void periodicSchedule(Task task, int delay, int interval, TimeUnit timeUnit) {
		SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> {
			try {
				task.run();
			} catch (TaskExecutionException e) {
				log.error("Error executing periodic task", e);
			}
		}, delay, interval, timeUnit);
	}
}
