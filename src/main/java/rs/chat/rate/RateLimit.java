package rs.chat.rate;

import lombok.extern.slf4j.Slf4j;
import rs.chat.tasks.TaskScheduler;
import rs.chat.utils.ref.Ref;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class RateLimit {
	private final Map<String, Ref<Long>> rateLimitMap = new ConcurrentHashMap<>();
	private final long limit;

	public RateLimit(long limit) {
		this(limit, 3, TimeUnit.SECONDS);
	}

	public RateLimit(long limit, int resetInterval, TimeUnit timeUnit) {
		this.limit = limit;
		TaskScheduler.schedule(this::reset, resetInterval, resetInterval, timeUnit);
	}

	public void removeEntry(String key) {
		this.rateLimitMap.remove(key);
	}

	private void reset() {
		this.rateLimitMap.values().forEach(Ref::reset);
	}

	public boolean isAllowedAndDecrease(String key) {
		Ref<Long> ref = this.rateLimitMap.computeIfAbsent(key, k -> new Ref<>(this.limit));

		if (ref.get() > 0) {
			ref.set(ref.get() - 1);
			return true;
		}

		return false;
	}
}
