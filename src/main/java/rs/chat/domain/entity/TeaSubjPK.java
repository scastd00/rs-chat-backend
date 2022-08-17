package rs.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class TeaSubjPK implements Serializable {
	@Column(name = "subject_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subjectId;

	@Column(name = "teacher_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long teacherId;

	@Override
	public int hashCode() {
		return Objects.hash(subjectId, teacherId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TeaSubjPK teaSubjPK = (TeaSubjPK) o;
		return Objects.equals(subjectId, teaSubjPK.subjectId) && Objects.equals(teacherId, teaSubjPK.teacherId);
	}
}
