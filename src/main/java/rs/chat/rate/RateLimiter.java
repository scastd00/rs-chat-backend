package rs.chat.rate;

import lombok.extern.slf4j.Slf4j;
import rs.chat.tasks.TaskScheduler;
import rs.chat.mem.ref.Ref;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Limits the number of actions to perform in a specified time interval.
 */
@Slf4j
public final class RateLimiter {
	private final Map<String, Ref<Long>> rateLimitMap = new ConcurrentHashMap<>();
	private final long limit;

	/**
	 * Creates a new rate limiter with a specified limit every 3 seconds.
	 *
	 * @param limit the limit to use for the rate limiter.
	 */
	public RateLimiter(long limit) {
		this(limit, 3, TimeUnit.SECONDS);
	}

	/**
	 * Creates a new rate limiter with a specified limit, reset interval and time unit.
	 *
	 * @param limit         the limit to use for the rate limiter.
	 * @param resetInterval the interval to reset the rate limiter.
	 * @param timeUnit      the time unit to use for the reset interval.
	 */
	public RateLimiter(long limit, int resetInterval, TimeUnit timeUnit) {
		this.limit = limit;
		TaskScheduler.periodicSchedule(this::reset, resetInterval, resetInterval, timeUnit);
	}

	/**
	 * Removes an entry from the rate limiter.
	 *
	 * @param key the key to remove.
	 */
	public void removeEntry(String key) {
		this.rateLimitMap.remove(key);
	}

	/**
	 * Resets the rate limiter.
	 */
	private void reset() {
		this.rateLimitMap.values().forEach(Ref::reset);
	}

	/**
	 * Checks if the rate limiter is allowed to execute and decreases the limit.
	 *
	 * @param key the key to check.
	 *
	 * @return {@code true} if the rate limiter is allowed to execute, {@code false} otherwise.
	 */
	public boolean isAllowedAndDecrease(String key) {
		Ref<Long> ref = this.rateLimitMap.computeIfAbsent(key, k -> new Ref<>(this.limit));

		Long value = ref.get();
		if (value > 0) {
			ref.set(value - 1);
			return true;
		}

		return false;
	}
}
