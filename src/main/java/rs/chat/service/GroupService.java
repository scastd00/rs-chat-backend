package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.chat.domain.DomainUtils;
import rs.chat.domain.entity.Group;
import rs.chat.domain.repository.ChatRepository;
import rs.chat.domain.repository.GroupRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GroupService {
	private final GroupRepository groupRepository;
	private final ChatRepository chatRepository;

	public List<Group> getAll() {
		return this.groupRepository.findAll();
	}

	public Group getGroupByName(String name) {
		return this.groupRepository.findByName(name);
	}

	public Group saveGroup(Group group) {
		Group savedGroup = this.groupRepository.save(group);

		// When I know that the group is saved, chat is created.
		this.chatRepository.save(DomainUtils.groupChat(group.getName()));
		return savedGroup;
	}
}
