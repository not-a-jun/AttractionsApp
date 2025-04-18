package com.AstonProgect.service;

import com.AstonProgect.dto.AddressDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.AddressMapper;
import com.AstonProgect.model.Address;
import com.AstonProgect.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления адресами.
 * <p>
 * Предоставляет полный набор операций для работы с адресами:
 * <ul>
 *   <li>Создание, чтение, обновление и удаление (CRUD)</li>
 *   <li>Поиск по различным критериям</li>
 *   <li>Поддержку пагинации для всех операций чтения</li>
 * </ul>
 * <p>
 * Все операции выполняются в транзакционном контексте с логированием действий.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    /**
     * Создаёт новый адрес на основе предоставленных данных.
     *
     * @param addressDto DTO с данными для создания адреса
     * @return DTO созданного адреса
     * @throws DataIntegrityViolationException при нарушении ограничений базы данных
     * @throws IllegalStateException при ошибке в процессе создания
     */
    public AddressDto createAddress(AddressDto addressDto) {
        log.info("Creating address: {}", addressDto);
        return Optional.of(addressDto)
                .map(addressMapper::toEntity)
                .map(addressRepository::save)
                .map(addressMapper::toDto)
                .orElseThrow(() -> new IllegalStateException("Failed to create address"));
    }

    /**
     * Получает адрес по его уникальному идентификатору.
     *
     * @param id UUID адреса
     * @return DTO найденного адреса
     * @throws ResourceNotFoundException если адрес не найден
     */
    @Transactional(readOnly = true)
    public AddressDto getAddressById(UUID id) {
        log.debug("Fetching address with ID: {}", id);
        return addressRepository.findById(id)
                .map(addressMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }

    /**
     * Получает все адреса с поддержкой пагинации.
     *
     * @param pageable параметры пагинации
     * @return страница с DTO адресов
     */
    @Transactional(readOnly = true)
    public Page<AddressDto> getAllAddresses(Pageable pageable) {
        log.info("Fetching all addresses with pagination");
        return addressRepository.findAll(pageable)
                .map(addressMapper::toDto);
    }

    /**
     * Обновляет существующий адрес.
     *
     * @param id UUID обновляемого адреса
     * @param addressDto DTO с новыми данными
     * @return DTO обновленного адреса
     * @throws ResourceNotFoundException если адрес не найден
     */
    public AddressDto updateAddress(UUID id, AddressDto addressDto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressMapper.updateAddressFromDto(addressDto, address);
        return addressMapper.toDto(addressRepository.save(address));
    }

    /**
     * Удаляет адрес по его идентификатору.
     *
     * @param id UUID удаляемого адреса
     * @throws ResourceNotFoundException если адрес не найден
     */
    public void deleteAddress(UUID id) {
        log.info("Deleting address with id: {}", id);
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
    }

    /**
     * Выполняет поиск адресов по различным критериям.
     *
     * @param city название города (частичное совпадение)
     * @param region название региона (частичное совпадение)
     * @param street название улицы (частичное совпадение)
     * @param pageable параметры пагинации
     * @return страница с найденными адресами
     */
    @Transactional(readOnly = true)
    public Page<AddressDto> searchAddresses(String city, String region, String street, Pageable pageable) {
        log.info("Searching addresses with city={}, region={}, street={}", city, region, street);
        return addressRepository.searchAddresses(city, region, street, pageable)
                .map(addressMapper::toDto);
    }
}
