package rs.chat.domain.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.User;
import rs.chat.domain.repository.BadgeRepository;
import rs.chat.domain.repository.UserBadgeRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RankService {
	private final UserRepository userRepository;
	private final UserBadgeRepository userBadgeRepository;
	private final BadgeRepository badgeRepository;

	public void incrementMessageCount(String username, String type) {
		User user = this.userRepository.findByUsername(username)
		                               .orElseThrow(() -> new NotFoundException("User not found"));

		JsonObject messageCountByType = user.getMessageCountByType();
		JsonElement typeCount = messageCountByType.get(type);

		if (typeCount == null) {
			messageCountByType.addProperty(type, 1);
		} else {
			messageCountByType.addProperty(type, typeCount.getAsInt() + 1);
		}

		this.userRepository.save(user);
	}
}
