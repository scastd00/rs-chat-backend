package rs.chat.domain.entity;

import com.google.gson.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import rs.chat.domain.entity.converters.JsonStringConverter;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "chats")
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 100)
	@NotNull
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Size(max = 10)
	@NotNull
	@Column(name = "type", nullable = false, length = 10)
	private String type;

	@Size(max = 300)
	@NotNull
	@Column(name = "s3_folder", nullable = false, length = 300)
	private String s3Folder;

	@Convert(converter = JsonStringConverter.class)
	@Column(name = "metadata", nullable = false)
	@JdbcTypeCode(SqlTypes.JSON)
	private @NotNull JsonObject metadata;

	@Size(max = 15)
	@NotNull
	@Column(name = "invitation_code", nullable = false, length = 15)
	private String invitationCode;

	@Size(max = 30)
	@NotNull
	@Column(name = "`key`", nullable = false, length = 30)
	private String key;

	@ManyToMany
	@JoinTable(name = "user_chat",
			joinColumns = @JoinColumn(name = "chat_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id"))
	@ToString.Exclude
	private Set<User> users = new LinkedHashSet<>();
}
