package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupPK;
import rs.chat.domain.repository.UserGroupRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.BadRequestException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserGroupService {
	private final UserRepository userRepository;
	private final UserGroupRepository userGroupRepository;

	public void addUserToGroup(@NotNull User user, @NotNull Long groupId) {
		UserGroup relation = new UserGroup(new UserGroupPK(groupId, user.getId()));
		this.userGroupRepository.save(relation);
	}

	/**
	 * Adds a user to a group. This method is used when a user redeems an invitation link.
	 * Invitation link URI format:
	 * https:.../groupId (and user will send the userId stored in Redux)
	 *
	 * @param userId
	 * @param groupId
	 */
	public void addUserToGroup(Long userId, Long groupId) {
		User userById = this.userRepository.findById(userId).orElseThrow(() -> {
			throw new BadRequestException("User not found");
		});

		this.addUserToGroup(userById, groupId);
	}
}
