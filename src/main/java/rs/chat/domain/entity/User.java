package rs.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 15)
	@NotNull
	@Column(name = "username", nullable = false, length = 15)
	private String username;

	@Size(max = 126)
	@NotNull
	@Column(name = "password", nullable = false, length = 126)
	private String password;

	@Size(max = 70)
	@NotNull
	@Column(name = "email", nullable = false, length = 70)
	private String email;

	@Size(max = 100)
	@NotNull
	@Column(name = "full_name", nullable = false, length = 100)
	private String fullName;

	@Column(name = "age")
	private Byte age;

	@Column(name = "birthdate")
	private LocalDate birthdate;

	@Size(max = 13)
	@NotNull
	@Column(name = "role", nullable = false, length = 13)
	private String role;

	@Column(name = "block_until")
	private Instant blockUntil;

	@Size(max = 6)
	@Column(name = "password_code", length = 6)
	private String passwordCode;

	@ManyToMany
	@JoinTable(name = "tea_subj",
			joinColumns = @JoinColumn(name = "teacher_id"),
			inverseJoinColumns = @JoinColumn(name = "subject_id"))
	@ToString.Exclude
	private Set<Subject> teacherSubjects = new LinkedHashSet<>();

	@ManyToMany
	@JoinTable(name = "user_group",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id"))
	@ToString.Exclude
	private Set<Group> groups = new LinkedHashSet<>();

	@OneToMany(mappedBy = "user")
	@ToString.Exclude
	private Set<Session> sessions = new LinkedHashSet<>();

	@OneToMany(mappedBy = "user")
	@ToString.Exclude
	private Set<File> files = new LinkedHashSet<>();

	@ManyToMany
	@JoinTable(name = "user_chat",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "chat_id"))
	@ToString.Exclude
	private Set<Chat> chats = new LinkedHashSet<>();

	@ManyToMany
	@JoinTable(name = "stu_subj",
			joinColumns = @JoinColumn(name = "student_id"),
			inverseJoinColumns = @JoinColumn(name = "subject_id"))
	@ToString.Exclude
	private Set<Subject> studentSubjects = new LinkedHashSet<>();
}
