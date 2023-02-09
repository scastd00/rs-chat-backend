package rs.chat.net.ws.strategies.messages.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.chat.domain.entity.User;
import rs.chat.domain.service.UserBadgeService;
import rs.chat.domain.service.UserService;

@Component
@RequiredArgsConstructor
public final class MessageEventListener implements ApplicationListener<MessageEvent> {
	private final UserBadgeService userBadgeService;
	private final UserService userService;

	@Override
	public void onApplicationEvent(@NotNull MessageEvent event) {
		User user = this.userService.getUserByUsername(event.getUsername());
		JsonObject messageCountByType = user.getMessageCountByType();
		JsonElement jsonElement = messageCountByType.get(event.getType());

		if (jsonElement == null) {
			messageCountByType.addProperty(event.getType(), 1);
		} else {
			messageCountByType.addProperty(event.getType(), jsonElement.getAsInt() + 1);
		}

		if (event instanceof CommandMessageEvent cme) {
			final String key = "USED_COMMANDS";

			if (!messageCountByType.has(key)) {
				messageCountByType.add(key, new JsonObject());
			}

			JsonObject commandsCount = messageCountByType.getAsJsonObject(key); // We have it, so it's safe to cast
			JsonElement commandCount = commandsCount.get(cme.getCommand());

			if (commandCount == null) {
				commandsCount.addProperty(cme.getCommand(), 1);
			} else {
				commandsCount.addProperty(cme.getCommand(), commandCount.getAsInt() + 1);
			}
		}

		User savedUser = this.userService.updateUser(user);

		String badgeTitle = this.userBadgeService.updateBadgesOfUser(savedUser, event.getType());
		if (badgeTitle != null) {
			event.getCallback().apply(badgeTitle);
		}
	}
}
