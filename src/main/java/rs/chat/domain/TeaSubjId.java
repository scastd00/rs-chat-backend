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
public class TeaSubjId implements Serializable {
	@Serial
	private static final long serialVersionUID = 4052510291554406562L;

	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@Column(name = "teacher_id", nullable = false)
	private Long teacherId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		TeaSubjId entity = (TeaSubjId) o;
		return Objects.equals(this.teacherId, entity.teacherId) &&
				Objects.equals(this.subjectId, entity.subjectId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(teacherId, subjectId);
	}
}
