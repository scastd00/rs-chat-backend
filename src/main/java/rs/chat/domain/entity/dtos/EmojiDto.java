package rs.chat.domain.entity.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A DTO for the {@link rs.chat.domain.entity.Emoji} entity
 */
public record EmojiDto(
		Long id,
		@Size(max = 100) @NotNull String name,
		@Size(max = 100) @NotNull String icon,
		@Size(max = 80) @NotNull String unicode,
		@Size(max = 30) @NotNull String category,
		@Size(max = 40) @NotNull String subcategory
) implements Serializable {
}
