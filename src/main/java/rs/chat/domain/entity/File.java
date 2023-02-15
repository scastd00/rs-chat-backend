package rs.chat.domain.entity;

import com.google.gson.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import rs.chat.domain.entity.converters.JsonStringConverter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "files")
public class File {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 255)
	@NotNull
	@Column(name = "name", nullable = false)
	private String name;

	@NotNull
	@Column(name = "date_uploaded", nullable = false)
	private Instant dateUploaded;

	@NotNull
	@Column(name = "size", nullable = false)
	private Integer size;

	@Size(max = 400)
	@NotNull
	@Column(name = "path", nullable = false, length = 400)
	private String path;

	@Convert(converter = JsonStringConverter.class)
	@Column(name = "metadata", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private @NotNull JsonObject metadata;

	@Size(max = 20)
	@NotNull
	@Column(name = "type", nullable = false, length = 20)
	private String type;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	private User user;
}
