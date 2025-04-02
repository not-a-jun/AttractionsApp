package com.AstonProgect.service;

import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.TicketInfoMapper;
import com.AstonProgect.model.TicketInfo;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для управления информацией о билетах.
 * <p>
 * Предоставляет методы для выполнения бизнес-логики, связанной с информацией о билетах:
 * создание, получение, обновление, удаление и поиск по различным критериям.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TicketInfoService {

    private final TicketInfoRepository ticketInfoRepository;
    private final AttractionRepository attractionRepository;
    private final TicketInfoMapper ticketInfoMapper;

    @Transactional
    public TicketInfoDto createTicketInfo(TicketInfoDto ticketInfoDto) {
        log.info("Creating ticket info for attraction: {}", ticketInfoDto.getAttractionId());

        // Проверка существования attraction
        UUID attractionId = ticketInfoDto.getAttractionId();
        if (attractionId == null || !attractionRepository.existsById(attractionId)) {
            throw new ResourceNotFoundException("Attraction not found with id: " + attractionId);
        }

        // Проверка, что у attraction еще нет ticketInfo
        if (ticketInfoRepository.existsByAttraction_Id(attractionId)) {
            throw new IllegalStateException("Attraction already has ticket info");
        }

        TicketInfo ticketInfo = ticketInfoMapper.toEntity(ticketInfoDto);
        TicketInfo savedTicket = ticketInfoRepository.save(ticketInfo);
        return ticketInfoMapper.toDto(savedTicket);
    }

    @Transactional(readOnly = true)
    public TicketInfoDto getTicketInfoById(UUID id) {
        log.debug("Fetching ticket info with id: {}", id);
        return ticketInfoRepository.findById(id)
                .map(ticketInfoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("TicketInfo not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<TicketInfoDto> getAllTicketInfos(Pageable pageable) {
        log.info("Fetching all ticket infos with pagination");
        return ticketInfoRepository.findAll(pageable)
                .map(ticketInfoMapper::toDto);
    }

    @Transactional
    public TicketInfoDto updateTicketInfo(UUID id, TicketInfoDto ticketInfoDto) {
        log.info("Updating ticket info with id: {}", id);

        TicketInfo existingTicket = ticketInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TicketInfo not found with id: " + id));

        // Проверка существования нового attraction (если меняется)
        if (ticketInfoDto.getAttractionId() != null &&
                !ticketInfoDto.getAttractionId().equals(existingTicket.getAttraction().getId()) &&
                !attractionRepository.existsById(ticketInfoDto.getAttractionId())) {
            throw new ResourceNotFoundException("Attraction not found with id: " + ticketInfoDto.getAttractionId());
        }

        ticketInfoMapper.updateTicketInfoFromDto(ticketInfoDto, existingTicket);
        TicketInfo updatedTicket = ticketInfoRepository.save(existingTicket);
        return ticketInfoMapper.toDto(updatedTicket);
    }

    @Transactional
    public void deleteTicketInfo(UUID id) {
        log.info("Deleting ticket info with id: {}", id);
        if (!ticketInfoRepository.existsById(id)) {
            throw new ResourceNotFoundException("TicketInfo not found with id: " + id);
        }
        ticketInfoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<TicketInfoDto> getByAttractionId(UUID attractionId, Pageable pageable) {
        log.info("Fetching ticket infos for attraction with id: {}", attractionId);
        if (!attractionRepository.existsById(attractionId)) {
            throw new ResourceNotFoundException("Attraction not found with id: " + attractionId);
        }
        return ticketInfoRepository.findByAttraction_Id(attractionId, pageable)
                .map(ticketInfoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TicketInfoDto getByAttraction(UUID attractionId) {
        log.info("Fetching ticket info for attraction with id: {}", attractionId);
        return ticketInfoRepository.findByAttraction_Id(attractionId)
                .map(ticketInfoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TicketInfo not found for attraction with id: " + attractionId));
    }
}