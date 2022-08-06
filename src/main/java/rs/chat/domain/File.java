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
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "files", schema = "rs_chat")
public class File {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "name", nullable = false)
	private String name;

	@Basic
	@Column(name = "date_uploaded", nullable = false)
	private Timestamp dateUploaded;

	@Basic
	@Column(name = "size", nullable = false)
	private Integer size;

	@Basic
	@Column(name = "path", length = 400)
	private String path;

	@Basic
	@Column(name = "metadata", length = 700)
	private String metadata;

	@Basic
	@Column(name = "type", nullable = false, length = 10)
	private String type;

	@Basic
	@Column(name = "user_id", nullable = false)
	private Long userId;
}
