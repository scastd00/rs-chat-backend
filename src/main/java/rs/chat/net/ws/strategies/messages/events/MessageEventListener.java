package rs.chat.net.ws.strategies.messages.events;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.chat.domain.service.RankService;

@Component
@RequiredArgsConstructor
public final class MessageEventListener implements ApplicationListener<MessageEvent> {
	private final RankService rankService;

	@Override
	public void onApplicationEvent(@NotNull MessageEvent event) {
		//
	}
}
