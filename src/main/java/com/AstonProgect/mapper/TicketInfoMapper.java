package com.AstonProgect.mapper;

import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.model.TicketInfo;
import com.AstonProgect.model.Attraction;
import com.AstonProgect.repository.AttractionRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;

/**
 * Маппер для преобразования между сущностью TicketInfo и DTO TicketInfoDto.
 * <p>
 * Предоставляет методы для конвертации объектов из одного представления в другое,
 * с обработкой взаимосвязи с сущностью Attraction.
 * <p>
 * Этот класс обращается к репозиторию AttractionRepository для загрузки
 * связанной сущности достопримечательности при преобразовании DTO в сущность.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TicketInfoMapper {

    @Autowired
    protected AttractionRepository attractionRepository;

    @Mapping(target = "attraction",
            source = "attractionId",
            qualifiedByName = "mapAttraction")
    public abstract TicketInfo toEntity(TicketInfoDto dto);

    @Mapping(target = "attractionId",
            source = "attraction.id")
    public abstract TicketInfoDto toDto(TicketInfo entity);

    @Named("mapAttraction")
    protected Attraction mapAttraction(UUID attractionId) {
        if (attractionId == null) return null;
        return attractionRepository.findById(attractionId).orElseThrow();
    }
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateTicketInfoFromDto(TicketInfoDto dto, @MappingTarget TicketInfo entity);
}