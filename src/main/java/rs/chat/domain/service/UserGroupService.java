package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.User;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupId;
import rs.chat.domain.repository.GroupRepository;
import rs.chat.domain.repository.UserGroupRepository;
import rs.chat.domain.repository.UserRepository;
import rs.chat.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserGroupService {
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final UserGroupRepository userGroupRepository;

	/**
	 * Adds a user to a group. This method is used when a user redeems an invitation link,
	 * or it is added to global group on register. Invitation link URI format:
	 * https:.../groupId (and user will send the userId stored in Redux)
	 *
	 * @param userId  user id to add to group.
	 * @param groupId group id to add user to.
	 */
	public void addUserToGroup(Long userId, Long groupId) {
		User user = this.userRepository.findById(userId).orElseThrow(() -> {
			throw new NotFoundException("User not found");
		});

		Group group = this.groupRepository.findById(groupId).orElseThrow(() -> {
			throw new NotFoundException("Group not found");
		});

		this.userGroupRepository.save(
				new UserGroup(new UserGroupId(user.getId(), groupId), user, group)
		);
	}
}
