package com.AstonProgect.repository;

import com.AstonProgect.model.Attraction;
import com.AstonProgect.model.AttractionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Attraction}.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>Базовые операции CRUD</li>
 *   <li>Поиск по различным критериям</li>
 *   <li>Загрузку связанных сущностей</li>
 * </ul>
 */
@Repository
public interface AttractionRepository extends JpaRepository<Attraction, UUID> {

    // Поиск всех достопримечательностей с пагинацией
    Page<Attraction> findAll(Pageable pageable);

    // Поиск по типу достопримечательности
    Page<Attraction> findByAttractionType(AttractionType type, Pageable pageable);

    // Поиск по городу (через связанный Address)
    Page<Attraction> findByAddress_City(String city, Pageable pageable);

    // Поиск по региону (через связанный Address)
    Page<Attraction> findByAddress_Region(String region, Pageable pageable);

    /**
     * Выполняет комплексный поиск достопримечательностей.
     * <p>
     * Поддерживает:
     * <ul>
     *   <li>Частичный поиск по названию</li>
     *   <li>Фильтрацию по типу</li>
     *   <li>Поиск по городу через связанный адрес</li>
     * </ul>
     *
     * @param name часть названия (может быть null)
     * @param type тип достопримечательности (может быть null)
     * @param city город (может быть null)
     * @param pageable параметры пагинации
     * @return страница с результатами поиска
     */
    @Query("SELECT a FROM Attraction a WHERE " +
            "(:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:type IS NULL OR a.attractionType = :type) AND " +
            "(:city IS NULL OR LOWER(a.address.city) LIKE LOWER(CONCAT('%', :city, '%')))")
    Page<Attraction> searchAttractions(
            @Param("name") String name,
            @Param("type") AttractionType type,
            @Param("city") String city,
            Pageable pageable);

    /**
     * Ищет достопримечательности по ID связанной услуги.
     *
     * @param serviceId ID услуги
     * @param pageable параметры пагинации
     * @return страница с достопримечательностями, предоставляющими указанную услугу
     */
    @Query("SELECT a FROM Attraction a JOIN a.services s WHERE s.id = :serviceId")
    Page<Attraction> findByServiceId(@Param("serviceId") UUID serviceId, Pageable pageable);

    /**
     * Находит достопримечательность по ID с загруженным адресом.
     *
     * @param id ID достопримечательности
     * @return Optional с достопримечательностью и адресом
     */
    @Query("SELECT a FROM Attraction a JOIN FETCH a.address WHERE a.id = :id")
    Optional<Attraction> findWithAddressById(@Param("id") UUID id);
}