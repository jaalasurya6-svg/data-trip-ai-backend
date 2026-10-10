package com.daytripai.service.impl;

import com.daytripai.dto.request.DestinationRequest;
import com.daytripai.dto.response.DestinationResponse;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.entity.Destination;
import com.daytripai.enums.DestinationCategory;
import com.daytripai.exception.DuplicateResourceException;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.DestinationMapper;
import com.daytripai.repository.DestinationRepository;
import com.daytripai.service.DestinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete Implementation of DestinationService.
 * Handles destination queries, filtering, full-text like searches, and CRUD operations.
 */
@Service
public class DestinationServiceImpl implements DestinationService {

    private static final Logger log = LoggerFactory.getLogger(DestinationServiceImpl.class);

    private final DestinationRepository destinationRepository;
    private final DestinationMapper destinationMapper;

    public DestinationServiceImpl(DestinationRepository destinationRepository, DestinationMapper destinationMapper) {
        this.destinationRepository = destinationRepository;
        this.destinationMapper = destinationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DestinationResponse> getAllDestinations(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String city,
            DestinationCategory category,
            String keyword,
            Double maxCost) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Destination> destinationsPage = destinationRepository.searchDestinations(
                keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null,
                city != null && !city.trim().isEmpty() ? city.trim() : null,
                category,
                maxCost,
                pageable
        );

        List<DestinationResponse> mappedList = destinationsPage.getContent()
                .stream()
                .map(destinationMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.of(destinationsPage, mappedList);
    }

    @Override
    @Transactional(readOnly = true)
    public DestinationResponse getDestinationById(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", id));
        return destinationMapper.toResponse(destination);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> getPopularDestinations(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("rating").descending());
        return destinationRepository.findByIsPopularTrue(pageable)
                .getContent()
                .stream()
                .map(destinationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> getDestinationsByCity(String city) {
        return destinationRepository.findByCityIgnoreCase(city)
                .stream()
                .map(destinationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableCities() {
        return destinationRepository.findDistinctCities();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationCategory> getAvailableCategories() {
        return Arrays.asList(DestinationCategory.values());
    }

    @Override
    @Transactional
    public DestinationResponse createDestination(DestinationRequest request) {
        // Business Rule: Check for duplicate destination in the same city
        List<Destination> existing = destinationRepository.findByCityIgnoreCase(request.getCity());
        boolean duplicate = existing.stream().anyMatch(d -> d.getName().equalsIgnoreCase(request.getName()));
        if (duplicate) {
            throw new DuplicateResourceException(
                    "A destination named '" + request.getName() + "' already exists in " + request.getCity()
            );
        }

        Destination destination = destinationMapper.toEntity(request);
        Destination saved = destinationRepository.save(destination);
        log.info("Created new destination: {} (ID: {}) in {}", saved.getName(), saved.getId(), saved.getCity());

        return destinationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DestinationResponse updateDestination(Long id, DestinationRequest request) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", id));

        destinationMapper.updateEntityFromRequest(destination, request);
        Destination updated = destinationRepository.save(destination);
        log.info("Updated destination ID {}: {}", id, updated.getName());

        return destinationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDestination(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", id));

        destinationRepository.delete(destination);
        log.info("Deleted destination ID {}: {}", id, destination.getName());
    }
}
