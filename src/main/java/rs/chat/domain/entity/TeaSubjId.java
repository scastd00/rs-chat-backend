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
public class TeaSubjId implements Serializable {
	@Serial
	private static final long serialVersionUID = 3664791734179899894L;

	@NotNull
	@Column(name = "teacher_id", nullable = false)
	private Long teacherId;

	@NotNull
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

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
