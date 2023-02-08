package rs.chat.domain.entity.dtos;

import com.google.gson.JsonObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link rs.chat.domain.entity.User} entity
 */
public record UserDto(
		Long id,
		@Size(max = 15) @NotNull String username,
		@Size(max = 70) @NotNull String email,
		@Size(max = 100) @NotNull String fullName,
		Byte age,
		LocalDate birthdate,
		@Size(max = 13) @NotNull String role,
		@NotNull JsonObject messageCountByType) implements Serializable {
}
