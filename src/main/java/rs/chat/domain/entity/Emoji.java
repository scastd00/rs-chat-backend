package rs.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
