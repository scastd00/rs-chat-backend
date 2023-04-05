package rs.chat.ai.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NSFWUploadEvent extends ApplicationEvent {
	private final Long userId;

	public NSFWUploadEvent(Object source, Long userId) {
		super(source);
		this.userId = userId;
	}
}
