package rs.chat.domain;

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
@Table(name = "chats", schema = "rs_chat")
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Basic
	@Column(name = "type", nullable = false, length = 10)
	private String type;

	@Basic
	@Column(name = "s3_folder", length = 300)
	private String s3Folder;

	/**
	 * JSON content.
	 */
	@Basic
	@Column(name = "metadata", nullable = false, length = -1)
	private String metadata;
}