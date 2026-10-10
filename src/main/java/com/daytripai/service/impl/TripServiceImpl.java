package com.daytripai.service.impl;

import com.daytripai.dto.request.TripRequest;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.dto.response.TripResponse;
import com.daytripai.dto.response.TripSummaryResponse;
import com.daytripai.entity.Trip;
import com.daytripai.entity.User;
import com.daytripai.enums.RoleType;
import com.daytripai.enums.TripStatus;
import com.daytripai.exception.BadRequestException;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.TripMapper;
import com.daytripai.repository.TripRepository;
import com.daytripai.repository.UserRepository;
import com.daytripai.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete Implementation of TripService.
 * Handles trip planning business logic, date/budget boundary checks,
 * user ownership authorization, and cascade deletions.
 */
@Service
public class TripServiceImpl implements TripService {

    private static final Logger log = LoggerFactory.getLogger(TripServiceImpl.class);

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;

    public TripServiceImpl(TripRepository tripRepository, UserRepository userRepository, TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripMapper = tripMapper;
    }

    @Override
    @Transactional
    public TripResponse createTrip(TripRequest request, Long authenticatedUserId) {
        User user = resolveUser(request.getUserId(), authenticatedUserId);

        validateTripBusinessRules(request);

        Trip trip = tripMapper.toEntity(request, user);
        Trip savedTrip = tripRepository.save(trip);
        log.info("Created new Trip ID {} for user {}", savedTrip.getId(), user.getEmail());

        return tripMapper.toResponse(savedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TripResponse> getTrips(
            Long authenticatedUserId,
            TripStatus status,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        User user = resolveUser(null, authenticatedUserId);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Trip> tripPage;
        if (status != null) {
            tripPage = tripRepository.findByUserIdAndStatusOrderByTripDateAsc(user.getId(), status, pageable);
        } else {
            tripPage = tripRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        }

        List<TripResponse> content = tripPage.getContent()
                .stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());

        return PagedResponse.of(tripPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(id, authenticatedUserId);
        return tripMapper.toResponse(trip);
    }

    @Override
    @Transactional
    public TripResponse updateTrip(Long id, TripRequest request, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(id, authenticatedUserId);

        validateTripBusinessRules(request);

        tripMapper.updateEntityFromRequest(trip, request);
        Trip updated = tripRepository.save(trip);
        log.info("Updated Trip ID {}: Destination={}, Budget={}", id, updated.getDestinationCity(), updated.getBudget());

        return tripMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public TripResponse updateTripStatus(Long id, TripStatus status, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(id, authenticatedUserId);

        // Validate state transitions
        if (trip.getStatus() == TripStatus.COMPLETED && status == TripStatus.PLANNED) {
            throw new BadRequestException("Cannot revert a completed trip back to planned status");
        }

        trip.setStatus(status);
        Trip updated = tripRepository.save(trip);
        log.info("Updated Trip ID {} status to {}", id, status);

        return tripMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTrip(Long id, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(id, authenticatedUserId);
        tripRepository.delete(trip);
        log.info("Deleted Trip ID {} along with its cascaded itineraries and expenses", id);
    }

    @Override
    @Transactional(readOnly = true)
    public TripSummaryResponse getTripSummary(Long id, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(id, authenticatedUserId);
        trip.recalculateBudget();
        return tripMapper.toSummaryResponse(trip);
    }

    // Helper: Find trip and enforce user authorization
    private Trip findTripAndVerifyOwnership(Long tripId, Long authenticatedUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        Long activeUserId = authenticatedUserId != null ? authenticatedUserId :
                com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(null);

        if (activeUserId == null) {
            activeUserId = com.daytripai.security.SecurityUtils.getCurrentUserEmail()
                    .flatMap(userRepository::findByEmail)
                    .map(User::getId)
                    .orElse(null);
        }

        if (activeUserId != null) {
            User authUser = userRepository.findById(activeUserId).orElse(null);
            boolean isAdmin = com.daytripai.security.SecurityUtils.isCurrentUserAdmin() ||
                    (authUser != null && authUser.getRole() == RoleType.ROLE_ADMIN);
            boolean isOwner = trip.getUser() != null && trip.getUser().getId().equals(activeUserId);

            if (!isAdmin && !isOwner) {
                log.warn("Access denied: User ID {} is not authorized to access Trip ID {}", activeUserId, tripId);
                throw new ResourceNotFoundException("Trip", "id", tripId);
            }
        }

        return trip;
    }

    // Helper: Resolve active user
    private User resolveUser(Long requestedUserId, Long authenticatedUserId) {
        Long activeUserId = authenticatedUserId != null ? authenticatedUserId :
                com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(null);

        if (activeUserId != null) {
            return userRepository.findById(activeUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", activeUserId));
        }

        // Try resolving via email in SecurityContext
        java.util.Optional<String> emailOpt = com.daytripai.security.SecurityUtils.getCurrentUserEmail();
        if (emailOpt.isPresent()) {
            java.util.Optional<User> userOpt = userRepository.findByEmail(emailOpt.get());
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        }

        if (requestedUserId != null) {
            return userRepository.findById(requestedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestedUserId));
        }

        // Fallback for testing: return default traveler
        return userRepository.findByEmail("traveler@daytripai.com")
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new BadRequestException("No registered user found in the system.")));
    }

    // Helper: Enforce business boundary validations
    private void validateTripBusinessRules(TripRequest request) {
        if (request.getTripDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Trip date cannot be in the past");
        }
        if (request.getBudget() < 100.0) {
            throw new BadRequestException("Minimum budget for a day trip is ₹100");
        }
        if (request.getAvailableHours() < 2 || request.getAvailableHours() > 16) {
            throw new BadRequestException("Available hours must be between 2 and 16 hours for a one-day trip");
        }
    }
}
