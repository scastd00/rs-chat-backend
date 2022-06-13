package ule.chat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "tea_subj", schema = "ule_chat")
@IdClass(TeaSubjPK.class)
public class TeaSubj {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "subject_id", nullable = false)
	private Long subjectId;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "teacher_id", nullable = false)
	private Long teacherId;

	public Long getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Long getTeacherId() {
		return this.teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}
}
