package rs.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

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

	@Column(name = "username", nullable = false, length = 15)
	private String username;

	@Column(name = "password", nullable = false, length = 126)
	private String password;

	@Column(name = "email", nullable = false, length = 70)
	private String email;

	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;

	@Column(name = "age")
	private Integer age;

	@Convert(disableConversion = true)
	@Column(name = "birthdate")
	private LocalDate birthdate;

	@Column(name = "role", nullable = false, length = 13)
	private String role;

	@Convert(disableConversion = true)
	@Column(name = "block_until")
	private Instant blockUntil;
}
