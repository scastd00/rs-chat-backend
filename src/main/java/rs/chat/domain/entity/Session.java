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
	@Basic
	@Column(name = "start_date", nullable = false)
	private Instant startDate;
	@Basic
	@Column(name = "end_date", nullable = false)
	private Instant endDate;
	@Basic
	@Column(name = "token", nullable = false, length = 300)
	private String token;
	@Basic
	@Column(name = "user_id", nullable = false)
	private Long userId;
}
