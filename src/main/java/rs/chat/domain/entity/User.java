package rs.chat.domain.entity;

import com.google.gson.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import rs.chat.domain.entity.converters.JsonStringConverter;

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
@Builder
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

	@Convert(converter = JsonStringConverter.class)
	@Column(name = "message_count_by_type", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private @NotNull JsonObject messageCountByType;

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

	@ManyToMany(mappedBy = "users")
	@ToString.Exclude
	private Set<Badge> badges = new LinkedHashSet<>();
}
