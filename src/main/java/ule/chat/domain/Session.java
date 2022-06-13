package ule.chat.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "sessions", schema = "ule_chat")
public class Session {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "src_ip", nullable = false, length = 16)
	private String srcIp;

	@Basic
	@Column(name = "date_started", nullable = false)
	private Timestamp dateStarted;

	@Basic
	@Column(name = "token", nullable = false, length = 128)
	private String token;

	@Basic
	@Column(name = "user_id", nullable = false)
	private Long userId;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSrcIp() {
		return this.srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	public Timestamp getDateStarted() {
		return this.dateStarted;
	}

	public void setDateStarted(Timestamp dateStarted) {
		this.dateStarted = dateStarted;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
