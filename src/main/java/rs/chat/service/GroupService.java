package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Group;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.GroupRepository;
import rs.chat.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GroupService {
	private final GroupRepository groupRepository;
	private final ChatRepository chatRepository;

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

	/**
	 * Saves a new group to database.
	 *
	 * @param group group to be saved.
	 *
	 * @return the saved group.
	 */
	public Group saveGroup(Group group) {
		Group savedGroup = this.groupRepository.save(group);

		// When I know that the group is saved, chat is created.
		this.chatRepository.save(DomainUtils.groupChat(group.getName()));
		return savedGroup;
	}
}
