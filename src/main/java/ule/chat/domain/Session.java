package ule.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sessions", schema = "ule_chat")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Session {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "src_ip", nullable = false, length = 16)
	private String srcIp;

	@Column(name = "date_started", nullable = false)
	private LocalDateTime dateStarted;

	@Basic
	@Column(name = "access_token", nullable = false, length = 300)
	private String accessToken;

	@Basic
	@Column(name = "refresh_token", nullable = false, length = 300)
	private String refreshToken;

	@Basic
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		Session session = (Session) o;
		return id != null && Objects.equals(id, session.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
