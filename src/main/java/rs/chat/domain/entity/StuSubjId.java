package rs.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class StuSubjId implements Serializable {
	@Serial
	private static final long serialVersionUID = 1171143602156298652L;
	@NotNull
	@Column(name = "student_id", nullable = false)
	private Long studentId;

	@NotNull
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		StuSubjId entity = (StuSubjId) o;
		return Objects.equals(this.studentId, entity.studentId) &&
		       Objects.equals(this.subjectId, entity.subjectId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(studentId, subjectId);
	}
}
