package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupId;

import java.util.List;

@SuppressWarnings("java:S100")
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
	List<UserGroup> findAllById_GroupId(Long groupId);

	List<UserGroup> findAllById_UserId(Long userId);

	void deleteAllById_GroupId(Long groupId);
}
