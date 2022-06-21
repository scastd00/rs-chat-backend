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
@Table(name = "stu_subj", schema = "ule_chat")
@IdClass(StuSubjPK.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StuSubj {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "student_id", nullable = false)
	private Long studentId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		StuSubj stuSubj = (StuSubj) o;
		return subjectId != null && Objects.equals(subjectId, stuSubj.subjectId)
				&& studentId != null && Objects.equals(studentId, stuSubj.studentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subjectId, studentId);
	}
}
