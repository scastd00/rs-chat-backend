package rs.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "subjects")
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 70)
	@NotNull
	@Column(name = "name", nullable = false, length = 70)
	private String name;

	@Size(max = 2)
	@NotNull
	@Column(name = "period", nullable = false, length = 2)
	private String period;

	@Size(max = 2)
	@NotNull
	@Column(name = "type", nullable = false, length = 2)
	private String type;

	@NotNull
	@Column(name = "credits", nullable = false)
	private Byte credits;

	@NotNull
	@Column(name = "grade", nullable = false)
	private Byte grade;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "degree_id", nullable = false)
	@ToString.Exclude
	private Degree degree;

	@ManyToMany
	@JoinTable(name = "tea_subj",
			joinColumns = @JoinColumn(name = "subject_id"),
			inverseJoinColumns = @JoinColumn(name = "teacher_id"))
	@ToString.Exclude
	private Set<User> teachers = new LinkedHashSet<>();

	@ManyToMany
	@JoinTable(name = "stu_subj",
			joinColumns = @JoinColumn(name = "subject_id"),
			inverseJoinColumns = @JoinColumn(name = "student_id"))
	@ToString.Exclude
	private Set<User> students = new LinkedHashSet<>();
}
