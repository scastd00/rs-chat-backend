package rs.chat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "badges")
public class Badge {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 100)
	@NotNull
	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Size(max = 300)
	@NotNull
	@Column(name = "description", nullable = false, length = 300)
	private String description;

	@Size(max = 200)
	@NotNull
	@Column(name = "icon", nullable = false, length = 200)
	private String icon;

	@Size(max = 20)
	@NotNull
	@Column(name = "type", nullable = false, length = 20)
	private String type;

	@NotNull
	@Column(name = "points_of_type", nullable = false)
	private Integer pointsOfType;

	@ManyToMany
	@JoinTable(name = "user_badge",
			joinColumns = @JoinColumn(name = "badge_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id"))
	@ToString.Exclude
	private Set<User> users = new LinkedHashSet<>();
}
