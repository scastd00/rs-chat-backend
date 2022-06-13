package ule.chat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "stu_subj", schema = "ule_chat")
@IdClass(StuSubjPK.class)
public class StuSubj {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "student_id", nullable = false)
	private Long studentId;

	public Long getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Long getStudentId() {
		return this.studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
}
