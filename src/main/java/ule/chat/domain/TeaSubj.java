package ule.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "tea_subj", schema = "ule_chat")
@IdClass(TeaSubjPK.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeaSubj {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "teacher_id", nullable = false)
	private Long teacherId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		TeaSubj teaSubj = (TeaSubj) o;
		return subjectId != null && Objects.equals(subjectId, teaSubj.subjectId)
				&& teacherId != null && Objects.equals(teacherId, teaSubj.teacherId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subjectId, teacherId);
	}
}
