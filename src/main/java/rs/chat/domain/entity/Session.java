package rs.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "sessions", schema = "rs_chat")
public class Session {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "src_ip", nullable = false, length = 32)
	private String srcIp;

	@Column(name = "date_started", nullable = false)
	private Instant dateStarted;

	@Basic
	@Column(name = "access_token", nullable = false, length = 300)
	private String accessToken;

	@Basic
	@Column(name = "refresh_token", nullable = false, length = 300)
	private String refreshToken;

	@Basic
	@Column(name = "user_id", nullable = false)
	private Long userId;
}
