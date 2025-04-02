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
 * Предоставляет стандартные CRUD операции JPA и дополнительные методы поиска адресов.
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
     * Комплексный поиск адресов по нескольким параметрам.
     * Поддерживает частичное совпадение и регистронезависимый поиск.
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