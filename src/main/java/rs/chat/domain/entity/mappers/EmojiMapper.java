package rs.chat.domain.entity.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.Emoji;
import rs.chat.domain.entity.dtos.EmojiDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EmojiMapper {
	Emoji toEntity(EmojiDto emojiDto);

	EmojiDto toDto(Emoji emoji);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Emoji partialUpdate(EmojiDto emojiDto, @MappingTarget Emoji emoji);
}
