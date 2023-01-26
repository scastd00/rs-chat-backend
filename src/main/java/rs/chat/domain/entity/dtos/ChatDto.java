package rs.chat.domain.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Chat} entity
 */
public record ChatDto(
		Long id,
		@Size(max = 100) @NotNull String name,
		@Size(max = 10) @NotNull String type,
		@Size(max = 1073741824) @NotNull String metadata,
		@Size(max = 15) @NotNull String invitationCode,
		@Size(max = 30) @NotNull String key
) implements Serializable {
}
