package com.AstonProgect.repository;

import com.AstonProgect.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Address}.
 * <p>
 * Предоставляет:
 * <ul>
 *   <li>Стандартные CRUD-операции через JpaRepository</li>
 *   <li>Специализированные методы поиска с поддержкой пагинации</li>
 *   <li>Комплексные запросы с фильтрацией по нескольким параметрам</li>
 * </ul>
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    // Поиск всех адресов с пагинацией
    Page<Address> findAll(Pageable pageable);

    // Поиск по городу с пагинацией
    Page<Address> findByCity(String city, Pageable pageable);

    // Поиск по региону с пагинацией
    Page<Address> findByRegion(String region, Pageable pageable);

    // Поиск адресов с заполненными координатами
    Page<Address> findByLongitudeIsNotNullAndLatitudeIsNotNull(Pageable pageable);

    /**
     * Выполняет комплексный поиск адресов по нескольким параметрам.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Поддерживает частичное совпадение (LIKE)</li>
     *   <li>Регистронезависимый поиск</li>
     *   <li>Параметры могут быть null (игнорируются в запросе)</li>
     * </ul>
     *
     * @param city название города (может быть null)
     * @param region название региона (может быть null)
     * @param street название улицы (может быть null)
     * @param pageable параметры пагинации
     * @return страница с результатами поиска
     */
    @Query("SELECT a FROM Address a WHERE " +
            "(:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:region IS NULL OR LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))) AND " +
            "(:street IS NULL OR LOWER(a.street) LIKE LOWER(CONCAT('%', :street, '%')))")
    Page<Address> searchAddresses(
            @Param("city") String city,
            @Param("region") String region,
            @Param("street") String street,
            Pageable pageable);
}