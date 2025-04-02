package com.AstonProgect.repository;

import com.AstonProgect.model.Service;
import com.AstonProgect.model.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link Service}.
 * <p>
 * Предоставляет стандартные CRUD операции JPA и дополнительные методы поиска услуг.
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    // Поиск всех услуг с пагинацией
    Page<Service> findAll(Pageable pageable);

    // Поиск по типу услуги
    Page<Service> findByServiceType(ServiceType serviceType, Pageable pageable);

    // Поиск по доступности
    Page<Service> findByAvailability(boolean availability, Pageable pageable);

    // Поиск по диапазону цен
    Page<Service> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Комплексный поиск услуг с фильтрацией по названию и типу.
     * Поддерживает частичное совпадение названия без учета регистра.
     *
     * @param name часть названия услуги (может быть null)
     * @param type тип услуги (может быть null)
     * @param pageable параметры пагинации
     * @return страница с результатами поиска
     */
    @Query("SELECT s FROM Service s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:type IS NULL OR s.serviceType = :type)")
    Page<Service> searchServices(
            @Param("name") String name,
            @Param("type") ServiceType type,
            Pageable pageable);
}