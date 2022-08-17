package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.chat.domain.entity.UserGroup;
import rs.chat.domain.entity.UserGroupPK;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupPK> {
	List<UserGroup> findAllByUserGroupPK_GroupId(Long groupId);

	List<UserGroup> findAllByUserGroupPK_UserId(Long userId);
}
