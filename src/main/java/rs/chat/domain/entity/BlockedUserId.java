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
public class BlockedUserId implements Serializable {
	@Serial
	private static final long serialVersionUID = -7094438063374331412L;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotNull
	@Column(name = "blocked_id", nullable = false)
	private Long blockedId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		BlockedUserId entity = (BlockedUserId) o;
		return Objects.equals(this.userId, entity.userId) &&
		       Objects.equals(this.blockedId, entity.blockedId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, blockedId);
	}
}
