package rs.chat.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class UserGroupPK implements Serializable {
	@Id
	@Column(name = "group_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long groupId;

	@Id
	@Column(name = "user_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Override
	public int hashCode() {
		return Objects.hash(groupId, userId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserGroupPK that = (UserGroupPK) o;
		return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
	}
}
