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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
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

	@Size(max = 1073741824)
	@NotNull
	@Column(name = "metadata", nullable = false, length = 1073741824)
	private String metadata;

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
