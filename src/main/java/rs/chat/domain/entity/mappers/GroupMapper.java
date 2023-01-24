package rs.chat.domain.entity.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.Group;
import rs.chat.domain.entity.dtos.GroupDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GroupMapper {
	Group toEntity(GroupDto groupDto);

	GroupDto toDto(Group group);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Group partialUpdate(GroupDto groupDto, @MappingTarget Group group);
}
