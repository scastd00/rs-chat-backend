package rs.chat.domain.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Degree} entity
 */
public record DegreeDto(
		Long id,
		@Size(max = 255) @NotNull String name
) implements Serializable {
}
