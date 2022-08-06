package rs.chat.domain;

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
import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users", schema = "rs_chat")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "username", nullable = false, length = 15)
	private String username;

	@Basic
	@Column(name = "password", nullable = false, length = 126)
	private String password;

	@Basic
	@Column(name = "email", nullable = false, length = 70)
	private String email;

	@Basic
	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;

	@Basic
	@Column(name = "age")
	private Byte age;

	@Basic
	@Column(name = "birthdate")
	private Date birthdate;

	@Basic
	@Column(name = "role", nullable = false, length = 13)
	private String role;

	@Basic
	@Column(name = "block_until")
	private Timestamp blockUntil;
}
