package rs.chat.domain.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Badge} entity
 */
public record BadgeDto(
		Long id,
		@Size(max = 100) @NotNull String title,
		@Size(max = 300) @NotNull String description,
		@Size(max = 200) @NotNull String icon) implements Serializable {
}
