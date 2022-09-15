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
@Table(name = "emojis", schema = "rs_chat")
public class Emoji {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	@Basic
	@Column(name = "name")
	private String name;
	@Basic
	@Column(name = "icon")
	private String icon;
	@Basic
	@Column(name = "unicode")
	private String unicode;
	@Basic
	@Column(name = "category")
	private String category;
	@Basic
	@Column(name = "subcategory")
	private String subcategory;
}
