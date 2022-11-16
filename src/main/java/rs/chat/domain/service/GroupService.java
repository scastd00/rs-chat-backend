package rs.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.Group;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.GroupRepository;
import rs.chat.domain.repository.UserChatRepository;
import rs.chat.domain.repository.UserGroupRepository;
import rs.chat.exceptions.NotFoundException;
import rs.chat.storage.S3;

import java.util.List;

import static rs.chat.utils.Constants.CHAT_KEY_FORMAT;
import static rs.chat.utils.Constants.GROUP_CHAT;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GroupService {
	private final GroupRepository groupRepository;
	private final ChatRepository chatRepository;
	private final UserChatRepository userChatRepository;
	private final UserGroupRepository userGroupRepository;

	/**
	 * Retrieves all the groups.
	 *
	 * @return the list of groups.
	 */
	public List<Group> getAll() {
		return this.groupRepository.findAll();
	}

	/**
	 * Retrieves a group by its name.
	 *
	 * @param name the name of the group.
	 *
	 * @return the group with the given name.
	 */
	public Group getGroupByName(String name) {
		return this.groupRepository.findByName(name)
		                           .orElseThrow(() -> new NotFoundException("Group with name " + name + " not found."));
	}

	public Group getById(Long id) {
		return this.groupRepository.findById(id)
		                           .orElseThrow(() -> new NotFoundException("Group with id '%s' does not exist.".formatted(id)));
	}

	/**
	 * Saves a new group to database.
	 *
	 * @param group group to be saved.
	 *
	 * @return the saved group.
	 */
	public Group saveGroup(Group group) {
		// Todo: check name?
		Group savedGroup = this.groupRepository.save(group);

		// When I know that the group is saved, chat is created.
		this.chatRepository.save(DomainUtils.groupChat(group.getName(), savedGroup.getId()));
		return savedGroup;
	}

	public void deleteById(Long id) {
		if (!this.groupRepository.existsById(id)) {
			throw new NotFoundException("Group with id '%s' does not exist.".formatted(id));
		}

		String chatKey = CHAT_KEY_FORMAT.formatted(GROUP_CHAT, id);
		Chat chat = this.chatRepository.findByKey(chatKey)
		                               .orElseThrow(
				                               () -> new NotFoundException("Chat for group %s not found.".formatted(id))
		                               );

		this.userChatRepository.deleteAllByUserChatPK_ChatId(chat.getId());
		this.chatRepository.deleteById(chat.getId());
		this.userGroupRepository.deleteAllByUserGroupPK_GroupId(id);
		this.groupRepository.deleteById(id);
		S3.getInstance().deleteHistoryFile(chatKey);
	}
}
