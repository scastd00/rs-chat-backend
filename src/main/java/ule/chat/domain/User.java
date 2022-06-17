package ule.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "ule_chat")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "username", nullable = false, length = 20)
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
	@Column(name = "role", nullable = false, length = 25)
	private String role;

	@Basic
	@Column(name = "block_until")
	private Timestamp blockUntil;

	public SimpleGrantedAuthority getSimpleGrantedAuthority() {
		return new SimpleGrantedAuthority(this.role);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		User user = (User) o;
		return id != null && Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
