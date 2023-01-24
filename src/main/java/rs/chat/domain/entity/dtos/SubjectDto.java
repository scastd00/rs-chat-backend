package rs.chat.domain.entity.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Subject} entity
 */
public record SubjectDto(
		Long id,
		@Size(max = 70) @NotNull String name,
		@Size(max = 2) @NotNull String period,
		@Size(max = 2) @NotNull String type,
		@NotNull Byte credits,
		@NotNull Byte grade
) implements Serializable {
}
