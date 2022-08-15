package rs.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "subjects", schema = "rs_chat")
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "name", nullable = false, length = 30)
	private String name;

	@Basic
	@Column(name = "period", nullable = false, length = 2)
	private String period;

	@Basic
	@Column(name = "type", nullable = false, length = 2)
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
}
