package rs.chat.domain.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Group} entity
 */
public record GroupDto(
		Long id,
		@Size(max = 70) @NotNull String name
) implements Serializable {
}
