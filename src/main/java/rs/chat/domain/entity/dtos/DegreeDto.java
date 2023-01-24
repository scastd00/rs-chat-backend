package rs.chat.domain.entity.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Degree} entity
 */
public record DegreeDto(
		Long id,
		@Size(max = 255) @NotNull String name
) implements Serializable {
}
