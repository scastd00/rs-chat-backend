package ule.chat.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "users", schema = "ule_chat")
public class User {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "username", nullable = false, length = 20)
	private String username;

	@Basic
	@Column(name = "password", nullable = false)
	private byte[] password;

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

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPassword() {
		return this.password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Byte getAge() {
		return this.age;
	}

	public void setAge(Byte age) {
		this.age = age;
	}

	public Date getBirthdate() {
		return this.birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Timestamp getBlockUntil() {
		return this.blockUntil;
	}

	public void setBlockUntil(Timestamp blockUntil) {
		this.blockUntil = blockUntil;
	}
}
