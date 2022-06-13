package ule.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "subjects", schema = "ule_chat")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		Subject subject = (Subject) o;
		return id != null && Objects.equals(id, subject.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
