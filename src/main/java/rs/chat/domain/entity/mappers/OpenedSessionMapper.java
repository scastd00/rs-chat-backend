package rs.chat.domain.entity.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.dtos.OpenedSessionDTO;

@Mapper(
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OpenedSessionMapper {
	Session toEntity(OpenedSessionDTO sessionDto);

	OpenedSessionDTO toDto(Session session);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Session partialUpdate(OpenedSessionDTO sessionDto, @MappingTarget Session session);
}
