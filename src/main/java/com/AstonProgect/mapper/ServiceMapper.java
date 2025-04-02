package com.AstonProgect.mapper;

import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.model.Service;
import org.mapstruct.*;

/**
 * Маппер для преобразования между сущностью Service и DTO ServiceDto.
 * <p>
 * Предоставляет методы для конвертации объектов из одного представления в другое,
 * с явным указанием соответствия полей сущности и DTO.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMapper {

    @Mapping(target = "attractions", ignore = true)
    Service toEntity(ServiceDto dto);

    ServiceDto toDto(Service entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateServiceFromDto(ServiceDto dto, @MappingTarget Service entity);
}
