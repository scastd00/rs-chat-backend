package rs.chat.domain.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link rs.chat.domain.entity.Session} entity
 */
public record SessionDto(
		Long id,
		@Size(max = 32) @NotNull String srcIp,
		@NotNull Instant startDate,
		@NotNull Instant endDate,
		@Size(max = 300) @NotNull String token
) implements Serializable {
}
