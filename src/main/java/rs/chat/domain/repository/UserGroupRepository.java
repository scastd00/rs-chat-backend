package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupId;

@SuppressWarnings("java:S100")
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
	void deleteAllById_GroupId(Long groupId);
}
