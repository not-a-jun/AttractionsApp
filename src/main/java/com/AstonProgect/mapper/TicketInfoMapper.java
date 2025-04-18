package com.AstonProgect.mapper;

import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.model.TicketInfo;
import com.AstonProgect.model.Attraction;
import com.AstonProgect.repository.AttractionRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;

/**
 * Маппер для преобразования между сущностью {@link TicketInfo} и DTO {@link TicketInfoDto}.
 * <p>
 * Особенности:
 * <ul>
 *   <li>Автоматически загружает связанную Attraction по ID</li>
 *   <li>Игнорирует несопоставленные поля</li>
 *   <li>Поддерживает частичное обновление (игнорирование null)</li>
 * </ul>
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TicketInfoMapper {

    @Autowired
    protected AttractionRepository attractionRepository;

    /**
     * Преобразует TicketInfoDto в TicketInfo с загрузкой Attraction.
     *
     * @param dto DTO для преобразования
     * @return сущность TicketInfo
     * @throws RuntimeException если Attraction не найдена
     */
    @Mapping(target = "attraction",
            source = "attractionId",
            qualifiedByName = "mapAttraction")
    public abstract TicketInfo toEntity(TicketInfoDto dto);

    /**
     * Преобразует TicketInfo в TicketInfoDto с извлечением ID Attraction.
     *
     * @param entity сущность для преобразования
     * @return DTO TicketInfoDto
     */
    @Mapping(target = "attractionId",
            source = "attraction.id")
    public abstract TicketInfoDto toDto(TicketInfo entity);

    /**
     * Загружает Attraction по ID.
     *
     * @param attractionId ID достопримечательности
     * @return сущность Attraction
     * @throws RuntimeException если не найдена
     */
    @Named("mapAttraction")
    protected Attraction mapAttraction(UUID attractionId) {
        if (attractionId == null) return null;
        return attractionRepository.findById(attractionId).orElseThrow();
    }

    /**
     * Обновляет сущность TicketInfo данными из DTO, игнорируя null-значения.
     *
     * @param dto DTO с новыми данными
     * @param entity сущность для обновления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateTicketInfoFromDto(TicketInfoDto dto, @MappingTarget TicketInfo entity);
}