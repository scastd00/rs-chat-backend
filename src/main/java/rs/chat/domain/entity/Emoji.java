package rs.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "emojis")
public class Emoji {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 100)
	@NotNull
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Size(max = 100)
	@NotNull
	@Column(name = "icon", nullable = false, length = 100)
	private String icon;

	@Size(max = 80)
	@NotNull
	@Column(name = "unicode", nullable = false, length = 80)
	private String unicode;

	@Size(max = 30)
	@NotNull
	@Column(name = "category", nullable = false, length = 30)
	private String category;

	@Size(max = 40)
	@NotNull
	@Column(name = "subcategory", nullable = false, length = 40)
	private String subcategory;
}
