package rs.chat.domain.entity.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

public record OpenedSessionDTO(
		Long id,
		@Size(max = 32) @NotNull String srcIp,
		@NotNull Instant startDate,
		@NotNull Instant endDate
) implements Serializable {
}
