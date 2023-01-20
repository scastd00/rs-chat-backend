package rs.chat.observability.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Metrics {
	private final MeterRegistry registry;

	public void incrementMessageCount(String type) {
		this.registry.counter("chat.messages", "type", type).increment();
	}

	public void incrementMentionedUsers() {
		this.registry.counter("chat.mentioned.users").increment();
	}
}
