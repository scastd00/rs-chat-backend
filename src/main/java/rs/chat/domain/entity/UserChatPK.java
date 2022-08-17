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
public class UserChatPK implements Serializable {
	@Column(name = "chat_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatId;

	@Column(name = "user_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Override
	public int hashCode() {
		return Objects.hash(chatId, userId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserChatPK that = (UserChatPK) o;
		return Objects.equals(chatId, that.chatId) && Objects.equals(userId, that.userId);
	}
}
