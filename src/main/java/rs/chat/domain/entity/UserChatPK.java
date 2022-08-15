package rs.chat.domain.entity;

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
public class UserChatPK implements Serializable {
	@Id
	@Column(name = "chat_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatId;

	@Id
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
