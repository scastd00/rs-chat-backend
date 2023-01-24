package rs.chat.domain.entity.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.Chat;
import rs.chat.domain.entity.dtos.ChatDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {
	Chat toEntity(ChatDto chatDto);

	ChatDto toDto(Chat chat);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Chat partialUpdate(ChatDto chatDto, @MappingTarget Chat chat);
}
