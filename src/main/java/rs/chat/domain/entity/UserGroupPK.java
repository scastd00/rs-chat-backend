package rs.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class UserGroupPK implements Serializable {
	@Column(name = "user_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "group_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long groupId;

	@Override
	public int hashCode() {
		return Objects.hash(userId, groupId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserGroupPK that = (UserGroupPK) o;
		return Objects.equals(userId, that.userId) && Objects.equals(groupId, that.groupId);
	}
}
