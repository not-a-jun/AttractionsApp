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
 * Предоставляет методы для выполнения бизнес-логики, связанной с адресами:
 * создание, получение, обновление, удаление и поиск по различным критериям.
 * <p>
 * Использует транзакционный подход с откатом при любых исключениях для
 * обеспечения целостности данных.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    /**
     * Создаёт новый адрес.
     * <p>
     * Конвертирует DTO в сущность, сохраняет её в базе данных и
     * возвращает созданный адрес в виде DTO.
     *
     * @param addressDto DTO с данными адреса для создания
     * @return DTO созданного адреса с заполненным ID
     * @throws DataIntegrityViolationException если нарушены ограничения целостности данных
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
     * Получает адрес по его идентификатору.
     * <p>
     * Ищет адрес в базе данных по ID и, если находит, возвращает
     * его в виде DTO.
     *
     * @param id уникальный идентификатор адреса
     * @return DTO найденного адреса
     * @throws ResourceNotFoundException если адрес с указанным ID не найден
     */
    @Transactional(readOnly = true)
    public AddressDto getAddressById(UUID id) {
        log.debug("Fetching address with ID: {}", id);
        return addressRepository.findById(id)
                .map(addressMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }

    /**
     * Получает список всех адресов.
     * <p>
     * Извлекает все адреса из базы данных, конвертирует их в DTO
     * и возвращает в виде списка.
     *
     * @return список DTO всех адресов
     */
    @Transactional(readOnly = true)
    public Page<AddressDto> getAllAddresses(Pageable pageable) {
        log.info("Fetching all addresses with pagination");
        return addressRepository.findAll(pageable)
                .map(addressMapper::toDto);
    }

    /**
     * Обновляет существующий адрес.
     * <p>
     * Ищет адрес по ID, обновляет его поля и сохраняет изменения
     * в базе данных.
     *
     * @param id уникальный идентификатор адреса для обновления
     * @param addressDto DTO с новыми данными адреса
     * @return DTO обновленного адреса
     * @throws ResourceNotFoundException если адрес с указанным ID не найден
     */
    public AddressDto updateAddress(UUID id, AddressDto addressDto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressMapper.updateAddressFromDto(addressDto, address);
        return addressMapper.toDto(addressRepository.save(address));
    }

    /**
     * Удаляет адрес по его идентификатору.
     * <p>
     * Проверяет наличие адреса с указанным ID и, если он существует,
     * удаляет его из базы данных.
     *
     * @param id уникальный идентификатор адреса для удаления
     * @throws ResourceNotFoundException если адрес с указанным ID не найден
     */
    public void deleteAddress(UUID id) {
        log.info("Deleting address with id: {}", id);
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<AddressDto> searchAddresses(String city, String region, String street, Pageable pageable) {
        log.info("Searching addresses with city={}, region={}, street={}", city, region, street);
        return addressRepository.searchAddresses(city, region, street, pageable)
                .map(addressMapper::toDto);
    }
}
