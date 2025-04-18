package com.AstonProgect.repository;

import com.AstonProgect.model.TicketInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link TicketInfo}.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>Базовые операции CRUD</li>
 *   <li>Поиск по различным параметрам билетов</li>
 *   <li>Аналитические запросы (минимальная/максимальная цена)</li>
 * </ul>
 */
@Repository
public interface TicketInfoRepository extends JpaRepository<TicketInfo, UUID> {

    // Поиск всей информации о билетах с пагинацией
    Page<TicketInfo> findAll(Pageable pageable);

    // Поиск по валюте
    Page<TicketInfo> findByCurrency(String currency, Pageable pageable);

    // Поиск по доступности
    Page<TicketInfo> findByAvailability(boolean availability, Pageable pageable);

    // Поиск по диапазону цен
    Page<TicketInfo> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Поиск по ID достопримечательности
    Page<TicketInfo> findByAttraction_Id(UUID attractionId, Pageable pageable);

    /**
     * Проверяет существование информации о билетах для достопримечательности.
     *
     * @param attractionId ID достопримечательности
     * @return true если информация существует, false в противном случае
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM TicketInfo t WHERE t.attraction.id = :attractionId")
    boolean existsByAttraction_Id(@Param("attractionId") UUID attractionId);

    /**
     * Находит информацию о билетах по ID достопримечательности.
     *
     * @param attractionId ID достопримечательности
     * @return Optional с информацией о билетах
     */
    @Query("SELECT t FROM TicketInfo t WHERE t.attraction.id = :attractionId")
    Optional<TicketInfo> findByAttraction_Id(@Param("attractionId") UUID attractionId);

    /**
     * Выполняет комплексный поиск информации о билетах.
     * <p>
     * Поддерживает фильтрацию по:
     * <ul>
     *   <li>Валюте</li>
     *   <li>Доступности</li>
     *   <li>Ценовому диапазону</li>
     * </ul>
     *
     * @param currency валюта (может быть null)
     * @param available флаг доступности (может быть null)
     * @param minPrice минимальная цена (может быть null)
     * @param maxPrice максимальная цена (может быть null)
     * @param pageable параметры пагинации
     * @return страница с результатами поиска
     */
    @Query("SELECT t FROM TicketInfo t WHERE " +
            "(:currency IS NULL OR t.currency = :currency) AND " +
            "(:available IS NULL OR t.availability = :available) AND " +
            "(:minPrice IS NULL OR t.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR t.price <= :maxPrice)")
    Page<TicketInfo> searchTicketInfos(
            @Param("currency") String currency,
            @Param("available") Boolean available,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Находит минимальную цену для указанной валюты.
     *
     * @param currency валюта (3-символьный код)
     * @return минимальная цена или null если нет билетов в этой валюте
     */
    @Query("SELECT MIN(t.price) FROM TicketInfo t WHERE t.currency = :currency")
    BigDecimal findMinPriceByCurrency(@Param("currency") String currency);

    /**
     * Находит максимальную цену для указанной валюты.
     *
     * @param currency валюта (3-символьный код)
     * @return максимальная цена или null если нет билетов в этой валюте
     */
    @Query("SELECT MAX(t.price) FROM TicketInfo t WHERE t.currency = :currency")
    BigDecimal findMaxPriceByCurrency(@Param("currency") String currency);
}
