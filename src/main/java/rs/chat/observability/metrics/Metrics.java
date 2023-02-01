package rs.chat.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static rs.chat.net.ws.Message.PING_MESSAGE;

@Component
@Slf4j
@RequiredArgsConstructor
public class Metrics {
	private final MeterRegistry registry;

	public void incrementMessageCount(String type) {
		if (type.equals(PING_MESSAGE.type())) {
			return;
		}

		this.registry.counter("chat.messages", "type", type).increment();
	}

	public void incrementMentionedUsers() {
		this.registry.counter("chat.mentioned.users").increment();
	}

	public void incrementMessageTime(String type, long time) {
		if (type.equals(PING_MESSAGE.type())) {
			return;
		}

		this.registry.timer("chat.message.time", "type", type).record(time, TimeUnit.MILLISECONDS);
	}
}
