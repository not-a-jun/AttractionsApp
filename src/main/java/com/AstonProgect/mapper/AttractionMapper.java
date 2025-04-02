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
 * Маппер для преобразования между сущностью Attraction и DTO AttractionDto.
 * <p>
 * Этот класс обращается к репозиториям для загрузки связанных
 * сущностей (адреса, услуги, информации о билетах) при
 * преобразовании DTO в сущность.
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
     * Преобразует DTO достопримечательности в сущность.
     * <p>
     * При преобразовании загружает связанные сущности (адрес, услуги, информацию о билетах)
     * с использованием соответствующих репозиториев.
     *
     * @param attractionDto DTO достопримечательности
     * @return сущность достопримечательности
     */
    @Mapping(target = "address", source = "addressId", qualifiedByName = "mapAddress")
    @Mapping(target = "services", source = "serviceIds", qualifiedByName = "mapServices")
    @Mapping(target = "ticketInfo", source = "ticketInfoId", qualifiedByName = "mapTicketInfo")
    public abstract Attraction toEntity(AttractionDto attractionDto);

    /**
     * Преобразует сущность достопримечательности в DTO.
     * <p>
     * При преобразовании извлекает идентификаторы связанных сущностей
     * (адреса, услуг, информации о билетах) для включения в DTO.
     *
     * @param attraction сущность достопримечательности
     * @return DTO достопримечательности
     */
    @Mapping(target = "addressId", source = "address.id")
    @Mapping(target = "serviceIds", source = "services", qualifiedByName = "mapServiceIds")
    @Mapping(target = "ticketInfoId", source = "ticketInfo.id")
    public abstract AttractionDto toDto(Attraction attraction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateAttractionFromDto(AttractionDto dto, @MappingTarget Attraction entity);
    /**
     * Загружает сущность адреса по идентификатору.
     *
     * @param addressId идентификатор адреса
     * @return сущность адреса
     * @throws java.util.NoSuchElementException если адрес с таким ID не найден
     */
    @Named("mapAddress")
    protected Address mapAddress(UUID addressId) {
        if (addressId == null) return null;
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
    }

    /**
     * Загружает список сущностей услуг по списку идентификаторов.
     *
     * @param serviceIds список идентификаторов услуг
     * @return список сущностей услуг
     * @throws java.util.NoSuchElementException если какая-либо услуга не найдена
     */
    @Named("mapServices")
    protected Set<Service> mapServices(Set<UUID> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) return null;
        return serviceIds.stream()
                .map(id -> serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found with id: " + id)))
                .collect(Collectors.toSet());
    }

    @Named("mapTicketInfo")
    protected TicketInfo mapTicketInfo(UUID ticketInfoId) {
        if (ticketInfoId == null) return null;
        return ticketInfoRepository.findById(ticketInfoId)
                .orElseThrow(() -> new RuntimeException("TicketInfo not found with id: " + ticketInfoId));
    }

    @Named("mapServiceIds")
    protected Set<UUID> mapServiceIds(Set<Service> services) {
        if (services == null) return null;
        return services.stream()
                .map(Service::getId)
                .collect(Collectors.toSet());
    }

    @AfterMapping
    protected void afterMapping(AttractionDto dto, @MappingTarget Attraction attraction) {
        // Устанавливаем обратную связь для ticketInfo
        if (attraction.getTicketInfo() != null) {
            attraction.getTicketInfo().setAttraction(attraction);
        }

        // Устанавливаем обратные ссылки для услуг
        if (attraction.getServices() != null) {
            attraction.getServices().forEach(service -> {
                if (service.getAttractions() == null || !service.getAttractions().contains(attraction)) {
                    service.getAttractions().add(attraction);
                }
            });
        }
    }
}