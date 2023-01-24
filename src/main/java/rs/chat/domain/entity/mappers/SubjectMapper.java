package rs.chat.domain.entity.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.Subject;
import rs.chat.domain.entity.dtos.SubjectDto;

@Mapper(
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING
)
public interface SubjectMapper {
	Subject toEntity(SubjectDto subjectDto);

	SubjectDto toDto(Subject subject);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Subject partialUpdate(SubjectDto subjectDto, @MappingTarget Subject subject);
}
