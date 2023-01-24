package rs.chat.domain.entity.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.Session;
import rs.chat.domain.entity.dtos.SessionDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SessionMapper {
	Session toEntity(SessionDto sessionDto);

	SessionDto toDto(Session session);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Session partialUpdate(SessionDto sessionDto, @MappingTarget Session session);
}
