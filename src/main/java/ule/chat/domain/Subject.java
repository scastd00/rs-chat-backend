package ule.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "subjects", schema = "ule_chat")
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false, length = 30)
	private String name;

	@Column(name = "subj_period", nullable = false, length = 2)
	private String subjPeriod;

	@Column(name = "type", nullable = false, length = 2)
	private String type;

	@Column(name = "credits", nullable = false)
	private Integer credits;

	@Column(name = "grade", nullable = false)
	private Integer grade;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "degree_id", nullable = false)
	@ToString.Exclude
	private Degree degree;
}
