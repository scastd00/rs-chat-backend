package rs.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class UserGroupId implements Serializable {
	@Serial
	private static final long serialVersionUID = 2792758443585331286L;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotNull
	@Column(name = "group_id", nullable = false)
	private Long groupId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		UserGroupId entity = (UserGroupId) o;
		return Objects.equals(this.groupId, entity.groupId) &&
		       Objects.equals(this.userId, entity.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId, userId);
	}
}
