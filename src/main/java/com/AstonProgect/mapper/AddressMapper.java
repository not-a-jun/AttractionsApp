package com.AstonProgect.mapper;

import com.AstonProgect.dto.AddressDto;
import com.AstonProgect.model.Address;
import org.mapstruct.*;

/**
 * Маппер для преобразования между сущностью Address и DTO AddressDto.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AddressMapper {
    @Mapping(target = "attractions", ignore = true)
    Address toEntity(AddressDto addressDto);

    AddressDto toDto(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddressFromDto(AddressDto dto, @MappingTarget Address entity);
}
