package rs.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
	private static final long serialVersionUID = 6336301901240336355L;

	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@Column(name = "student_id", nullable = false)
	private Long studentId;

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
