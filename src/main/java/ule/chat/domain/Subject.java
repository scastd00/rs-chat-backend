package ule.chat.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subjects", schema = "ule_chat")
public class Subject {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "name", nullable = false, length = 30)
	private String name;

	@Basic
	@Column(name = "subj_period", nullable = false, length = 15)
	private String subjPeriod;

	@Basic
	@Column(name = "type", nullable = false, length = 50)
	private String type;

	@Basic
	@Column(name = "credits", nullable = false)
	private Byte credits;

	@Basic
	@Column(name = "grade", nullable = false)
	private Byte grade;

	@Basic
	@Column(name = "degree_id", nullable = false)
	private Long degreeId;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubjPeriod() {
		return this.subjPeriod;
	}

	public void setSubjPeriod(String subjPeriod) {
		this.subjPeriod = subjPeriod;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Byte getCredits() {
		return this.credits;
	}

	public void setCredits(Byte credits) {
		this.credits = credits;
	}

	public Byte getGrade() {
		return this.grade;
	}

	public void setGrade(Byte grade) {
		this.grade = grade;
	}

	public Long getDegreeId() {
		return this.degreeId;
	}

	public void setDegreeId(Long degreeId) {
		this.degreeId = degreeId;
	}
}
