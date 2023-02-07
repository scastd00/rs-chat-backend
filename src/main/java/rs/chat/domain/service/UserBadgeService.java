package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Badge;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserBadge;
import rs.chat.domain.entity.UserBadgeId;
import rs.chat.domain.repository.BadgeRepository;
import rs.chat.domain.repository.UserBadgeRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserBadgeService {
	private final UserBadgeRepository userBadgeRepository;
	private final BadgeRepository badgeRepository;

	public String updateBadgesOfUser(User user, String type) {
		Badge badge = this.badgeRepository.findAllByTypeOrderByPointsOfTypeDesc(type)
		                                  .stream()
		                                  .filter(b -> user.getMessageCountByType().get(type).getAsInt() == b.getPointsOfType())
		                                  .findFirst()
		                                  .orElse(null);

		if (badge == null || this.userBadgeRepository.existsById_UserIdAndId_BadgeId(user.getId(), badge.getId())) {
			return null;
		}

		UserBadgeId id = new UserBadgeId(user.getId(), badge.getId());
		this.userBadgeRepository.save(new UserBadge(id, user, badge));
		return badge.getTitle();
	}
}
