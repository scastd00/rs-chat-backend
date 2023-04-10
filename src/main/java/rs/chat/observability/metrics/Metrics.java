package rs.chat.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static rs.chat.net.ws.Message.PING_MESSAGE;

/**
 * Metrics for the application.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Metrics {
	private final MeterRegistry registry;

	/**
	 * Increment the message count for the given type.
	 * Ping messages are ignored.
	 *
	 * @param type The type of message.
	 */
	public void incrementMessageCount(String type) {
		if (type.equals(PING_MESSAGE.type())) {
			return;
		}

		this.registry.counter("chat.messages", "type", type).increment();
	}

	/**
	 * Increment the mentioned user count.
	 */
	public void incrementMentionedUsers() {
		this.registry.counter("chat.mentioned.users").increment();
	}

	/**
	 * Increment the time it took to process a message of the given type.
	 *
	 * @param type The type of message.
	 * @param time The time it took to process the message.
	 */
	public void incrementMessageTime(String type, long time) {
		if (type.equals(PING_MESSAGE.type())) {
			return;
		}

		this.registry.timer("chat.message.time", "type", type).record(time, TimeUnit.MILLISECONDS);
	}

	/**
	 * Increment the blocked users' counter.
	 */
	public void incrementBlockedUsers() {
		this.registry.counter("chat.blocked.users").increment();
	}

	/**
	 * Increments the commands executed counter for the given command name and the
	 * total commands executed counter in the application.
	 *
	 * @param command The command name.
	 */
	public void incrementCommandsExecuted(String command) {
		this.registry.counter("chat.commands.executed", "command", command).increment();
		this.registry.counter("chat.commands.executed.total").increment();
	}
}
