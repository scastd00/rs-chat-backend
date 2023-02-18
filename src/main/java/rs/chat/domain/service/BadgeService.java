package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.UserBadge;
import rs.chat.domain.entity.dtos.BadgeDto;
import rs.chat.domain.entity.mappers.BadgeMapper;
import rs.chat.domain.repository.UserBadgeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BadgeService {
	private final UserBadgeRepository userBadgeRepository;
	private final BadgeMapper badgeMapper;

	public List<BadgeDto> getBadgesOfUser(Long userId) {
		return this.userBadgeRepository.findAllById_UserId(userId)
		                               .stream()
		                               .map(UserBadge::getBadge)
		                               .map(this.badgeMapper::toDto)
		                               .toList();
	}
}
