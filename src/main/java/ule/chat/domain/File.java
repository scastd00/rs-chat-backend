package ule.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "files", schema = "ule_chat")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class File {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Basic
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "date_uploaded", nullable = false)
	private LocalDateTime dateUploaded;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
		File file = (File) o;
		return id != null && Objects.equals(id, file.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
