package rs.chat.domain.entity.mappers;

import com.google.gson.JsonObject;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.chat.domain.entity.File;
import rs.chat.domain.entity.dtos.FileDto;
import rs.chat.utils.Utils;

@Mapper(
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		componentModel = MappingConstants.ComponentModel.SPRING
)
public interface FileMapper {
	File toEntity(FileDto fileDto);

	FileDto toDto(File file);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	File partialUpdate(FileDto fileDto, @MappingTarget File file);

	default JsonObject map(String value) {
		return value == null ? null : Utils.parseJson(value);
	}

	default String map(JsonObject value) {
		return value == null ? null : value.toString();
	}
}
