package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupId;

import java.util.List;

@SuppressWarnings("java:S100")
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
	List<UserGroup> findAllByUserGroupPK_GroupId(Long groupId);

	List<UserGroup> findAllByUserGroupPK_UserId(Long userId);

	void deleteAllByUserGroupPK_GroupId(Long groupId);
}
