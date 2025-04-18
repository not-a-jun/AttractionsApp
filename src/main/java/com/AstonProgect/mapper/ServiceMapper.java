package com.AstonProgect.mapper;

import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.model.Service;
import org.mapstruct.*;

/**
 * Маппер для преобразования между сущностью {@link Service} и DTO {@link ServiceDto}.
 * <p>
 * Настройки:
 * <ul>
 *   <li>Игнорирует несопоставленные поля</li>
 *   <li>Игнорирует поле attractions при преобразовании DTO → Entity</li>
 *   <li>Не обновляет null-значения при частичном обновлении</li>
 * </ul>
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
