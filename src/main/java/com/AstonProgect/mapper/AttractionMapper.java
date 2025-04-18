package com.AstonProgect.mapper;

import com.AstonProgect.dto.AttractionDto;
import com.AstonProgect.model.Attraction;
import com.AstonProgect.model.Address;
import com.AstonProgect.model.Service;
import com.AstonProgect.model.TicketInfo;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.ServiceRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Маппер для преобразования между сущностью {@link Attraction} и DTO {@link AttractionDto}.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>Преобразование с загрузкой связанных сущностей (адрес, услуги, билеты)</li>
 *   <li>Обработку двунаправленных связей (например, TicketInfo ↔ Attraction)</li>
 *   <li>Игнорирование null-значений при обновлении</li>
 * </ul>
 */
@Mapper(componentModel = "spring",
        uses = {ServiceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AttractionMapper {

    @Autowired
    protected AddressRepository addressRepository;
    
    @Autowired
    protected ServiceRepository serviceRepository;
    
    @Autowired
    protected TicketInfoRepository ticketInfoRepository;

    /**
     * Преобразует AttractionDto в Attraction с загрузкой зависимостей.
     *
     * @param attractionDto DTO для преобразования
     * @return сущность Attraction
     * @throws RuntimeException если связанные сущности не найдены
     */
    @Mapping(target = "address", source = "addressId", qualifiedByName = "mapAddress")
    @Mapping(target = "services", source = "serviceIds", qualifiedByName = "mapServices")
    @Mapping(target = "ticketInfo", source = "ticketInfoId", qualifiedByName = "mapTicketInfo")
    public abstract Attraction toEntity(AttractionDto attractionDto);

    /**
     * Преобразует Attraction в AttractionDto с извлечением ID зависимостей.
     *
     * @param attraction сущность для преобразования
     * @return DTO AttractionDto
     */
    @Mapping(target = "addressId", source = "address.id")
    @Mapping(target = "serviceIds", source = "services", qualifiedByName = "mapServiceIds")
    @Mapping(target = "ticketInfoId", source = "ticketInfo.id")
    public abstract AttractionDto toDto(Attraction attraction);

    /**
     * Обновляет сущность Attraction данными из DTO, игнорируя null-значения.
     *
     * @param dto DTO с новыми данными
     * @param entity сущность для обновления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateAttractionFromDto(AttractionDto dto, @MappingTarget Attraction entity);

    /**
     * Загружает Address по ID.
     *
     * @param addressId ID адреса
     * @return сущность Address
     * @throws RuntimeException если адрес не найден
     */
    @Named("mapAddress")
    protected Address mapAddress(UUID addressId) {
        if (addressId == null) return null;
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
    }

    /**
     * Загружает Set<Service> по Set<UUID>.
     *
     * @param serviceIds набор ID услуг
     * @return набор сущностей Service
     * @throws RuntimeException если услуги не найдены
     */
    @Named("mapServices")
    protected Set<Service> mapServices(Set<UUID> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) return null;
        return serviceIds.stream()
                .map(id -> serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found with id: " + id)))
                .collect(Collectors.toSet());
    }

    /**
     * Загружает TicketInfo по ID.
     *
     * @param ticketInfoId ID информации о билетах
     * @return сущность TicketInfo
     * @throws RuntimeException если информация не найдена
     */
    @Named("mapTicketInfo")
    protected TicketInfo mapTicketInfo(UUID ticketInfoId) {
        if (ticketInfoId == null) return null;
        return ticketInfoRepository.findById(ticketInfoId)
                .orElseThrow(() -> new RuntimeException("TicketInfo not found with id: " + ticketInfoId));
    }

    /**
     * Преобразует Set<Service> в Set<UUID>.
     *
     * @param services набор сущностей Service
     * @return набор ID услуг
     */
    @Named("mapServiceIds")
    protected Set<UUID> mapServiceIds(Set<Service> services) {
        if (services == null) return null;
        return services.stream()
                .map(Service::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Устанавливает обратную ссылку после маппинга.
     *
     * @param dto исходный DTO
     * @param attraction целевая сущность
     */
    @AfterMapping
    protected void afterMapping(AttractionDto dto, @MappingTarget Attraction attraction) {
        // Устанавливаем обратную связь для ticketInfo
        if (attraction.getTicketInfo() != null) {
            attraction.getTicketInfo().setAttraction(attraction);
        }
    }
}