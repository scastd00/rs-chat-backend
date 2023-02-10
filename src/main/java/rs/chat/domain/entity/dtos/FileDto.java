package rs.chat.domain.entity.dtos;

import com.google.gson.JsonObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link rs.chat.domain.entity.File} entity
 */
public record FileDto(
		Long id,
		@Size(max = 255) @NotNull String name,
		@NotNull Instant dateUploaded,
		@NotNull Integer size,
		@Size(max = 400) @NotNull String path,
		@NotNull JsonObject metadata,
		@Size(max = 20) @NotNull String type
) implements Serializable {
}
