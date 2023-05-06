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
public class UserChatId implements Serializable {
	@Serial
	private static final long serialVersionUID = 8795209564542282605L;

	@NotNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotNull
	@Column(name = "chat_id", nullable = false)
	private Long chatId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		UserChatId entity = (UserChatId) o;
		return Objects.equals(this.chatId, entity.chatId) &&
				Objects.equals(this.userId, entity.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chatId, userId);
	}
}
