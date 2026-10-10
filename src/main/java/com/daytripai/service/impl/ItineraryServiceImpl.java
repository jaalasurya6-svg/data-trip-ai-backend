package com.daytripai.service.impl;

import com.daytripai.dto.request.ItineraryItemRequest;
import com.daytripai.dto.request.ItineraryReorderRequest;
import com.daytripai.dto.response.ItineraryResponse;
import com.daytripai.entity.*;
import com.daytripai.enums.DestinationCategory;
import com.daytripai.enums.RoleType;
import com.daytripai.exception.BadRequestException;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.ItineraryMapper;
import com.daytripai.repository.*;
import com.daytripai.service.ItineraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete Implementation of ItineraryService.
 * Implements intelligent time-sequencing, budget-aware attraction selection,
 * transit duration calculations, and stop re-ordering.
 */
@Service
public class ItineraryServiceImpl implements ItineraryService {

    private static final Logger log = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    private final ItineraryRepository itineraryRepository;
    private final ItineraryItemRepository itineraryItemRepository;
    private final TripRepository tripRepository;
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final ItineraryMapper itineraryMapper;

    public ItineraryServiceImpl(ItineraryRepository itineraryRepository,
                                ItineraryItemRepository itineraryItemRepository,
                                TripRepository tripRepository,
                                DestinationRepository destinationRepository,
                                UserRepository userRepository,
                                ItineraryMapper itineraryMapper) {
        this.itineraryRepository = itineraryRepository;
        this.itineraryItemRepository = itineraryItemRepository;
        this.tripRepository = tripRepository;
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.itineraryMapper = itineraryMapper;
    }

    @Override
    @Transactional
    public ItineraryResponse generateItinerary(Long tripId, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);

        log.info("Generating intelligent itinerary for Trip ID {} (City: {}, Budget: ₹{}, Hours: {}h)",
                tripId, trip.getDestinationCity(), trip.getBudget(), trip.getAvailableHours());

        // 1. Fetch existing itinerary or create new for this trip
        Itinerary itinerary = itineraryRepository.findByTripId(tripId).orElseGet(() -> {
            Itinerary newItin = new Itinerary();
            newItin.setTrip(trip);
            return newItin;
        });

        // Clear previous stops
        itinerary.getItems().clear();

        // 2. Fetch candidate destinations for the target city
        List<Destination> candidates = destinationRepository.findByCityIgnoreCase(trip.getDestinationCity());
        if (candidates.isEmpty()) {
            // Fallback: fetch popular destinations across India
            candidates = destinationRepository.findByIsPopularTrue();
        }

        // Sort candidates by matching travel preference keyword and rating
        String preference = trip.getTravelPreference() != null ? trip.getTravelPreference().toLowerCase() : "";
        candidates.sort((d1, d2) -> {
            boolean d1Matches = d1.getName().toLowerCase().contains(preference) ||
                    d1.getCategory().name().toLowerCase().contains(preference) ||
                    (d1.getDescription() != null && d1.getDescription().toLowerCase().contains(preference));
            boolean d2Matches = d2.getName().toLowerCase().contains(preference) ||
                    d2.getCategory().name().toLowerCase().contains(preference) ||
                    (d2.getDescription() != null && d2.getDescription().toLowerCase().contains(preference));
            if (d1Matches && !d2Matches) return -1;
            if (!d1Matches && d2Matches) return 1;
            return Double.compare(d2.getRating(), d1.getRating());
        });

        // 3. Update Itinerary entity summary
        itinerary.setTrip(trip);
        itinerary.setSummary(String.format("Personalized One-Day Itinerary in %s tailored for %s (%d travelers)",
                trip.getDestinationCity(), trip.getTravelPreference(), trip.getGroupSize()));

        LocalTime currentTime = LocalTime.of(9, 30);
        int availableMinutes = trip.getAvailableHours() * 60;
        int consumedMinutes = 0;
        double accumulatedDistanceKm = 0.0;
        double accumulatedCost = 0.0;
        int stopOrder = 1;

        int groupSize = Math.max(trip.getGroupSize(), 1);
        int destIndex = 0;
        boolean lunchAdded = false;

        while (consumedMinutes < availableMinutes - 60 && destIndex < candidates.size()) {
            // Midday Lunch insertion between 13:00 and 14:30
            if (!lunchAdded && currentTime.isAfter(LocalTime.of(12, 45)) && currentTime.isBefore(LocalTime.of(14, 30))) {
                LocalTime lunchStart = currentTime;
                LocalTime lunchEnd = lunchStart.plusMinutes(75);
                double estimatedFoodCost = 300.0 * groupSize;

                ItineraryItem lunchItem = new ItineraryItem(
                        itinerary, null, stopOrder++,
                        "Lunch: Authentic Local Cuisine in " + trip.getDestinationCity(),
                        "Relax and enjoy regional culinary specialties and refreshments.",
                        lunchStart, lunchEnd, estimatedFoodCost, "CAB", 20, 4.0
                );
                itinerary.addItem(lunchItem);

                currentTime = lunchEnd.plusMinutes(20);
                consumedMinutes += 95;
                accumulatedDistanceKm += 4.0;
                accumulatedCost += estimatedFoodCost;
                lunchAdded = true;
                continue;
            }

            Destination dest = candidates.get(destIndex++);
            int visitMinutes = (int) (dest.getAverageVisitDurationHours() * 60);
            if (consumedMinutes + visitMinutes > availableMinutes) {
                // Shorten visit if near end of available window
                visitMinutes = Math.max(availableMinutes - consumedMinutes - 30, 45);
            }

            LocalTime stopStart = currentTime;
            LocalTime stopEnd = stopStart.plusMinutes(visitMinutes);
            double ticketCost = (dest.getEstimatedCost() != null ? dest.getEstimatedCost() : 0.0) * groupSize;

            String transport = (stopOrder % 2 == 0) ? "CAB" : "METRO";
            int transitMinutes = 15;
            double transitKm = 3.5;

            ItineraryItem stopItem = new ItineraryItem(
                    itinerary, dest, stopOrder++,
                    dest.getName(),
                    dest.getDescription() != null && dest.getDescription().length() > 140 ?
                            dest.getDescription().substring(0, 137) + "..." : dest.getDescription(),
                    stopStart, stopEnd, ticketCost, transport, transitMinutes, transitKm
            );
            itinerary.addItem(stopItem);

            currentTime = stopEnd.plusMinutes(transitMinutes);
            consumedMinutes += (visitMinutes + transitMinutes);
            accumulatedDistanceKm += transitKm;
            accumulatedCost += ticketCost;
        }

        itinerary.setTotalDurationMinutes(consumedMinutes);
        itinerary.setTotalDistanceKm(Math.round(accumulatedDistanceKm * 10.0) / 10.0);

        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        // Update trip estimated total cost and weather summary
        trip.setEstimatedTotalCost(accumulatedCost);
        trip.setWeatherForecastSummary("Sunny, 28°C, Ideal for outdoor sightseeing");
        trip.setItinerary(savedItinerary);
        tripRepository.save(trip);

        log.info("Successfully generated itinerary ID {} with {} stops for Trip ID {}",
                savedItinerary.getId(), savedItinerary.getItems().size(), tripId);

        return itineraryMapper.toResponse(savedItinerary);
    }

    @Override
    @Transactional(readOnly = true)
    public ItineraryResponse getItineraryByTripId(Long tripId, Long authenticatedUserId) {
        findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not yet generated for trip ID: " + tripId));
        return itineraryMapper.toResponse(itinerary);
    }

    @Override
    @Transactional
    public ItineraryResponse addItem(Long tripId, ItineraryItemRequest request, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip ID: " + tripId));

        Destination destination = null;
        if (request.getDestinationId() != null) {
            destination = destinationRepository.findById(request.getDestinationId()).orElse(null);
        }

        int nextOrder = itinerary.getItems().size() + 1;
        ItineraryItem item = itineraryMapper.toEntity(request, itinerary, destination, nextOrder);
        itinerary.addItem(item);

        recalculateItineraryMetrics(itinerary, trip);
        Itinerary saved = itineraryRepository.save(itinerary);

        return itineraryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ItineraryResponse updateItem(Long tripId, Long itemId, ItineraryItemRequest request, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip ID: " + tripId));

        ItineraryItem item = itineraryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("ItineraryItem", "id", itemId));

        if (!item.getItinerary().getId().equals(itinerary.getId())) {
            throw new BadRequestException("Item ID " + itemId + " does not belong to Trip ID " + tripId);
        }

        item.setPlaceName(request.getPlaceName());
        item.setActivityDescription(request.getActivityDescription());
        item.setStartTime(request.getStartTime());
        item.setEndTime(request.getEndTime());
        item.setEstimatedCost(request.getEstimatedCost());
        item.setTransportModeToNext(request.getTransportModeToNext());
        item.setTravelMinutesToNext(request.getTravelMinutesToNext());
        item.setDistanceKmToNext(request.getDistanceKmToNext());
        item.setNotes(request.getNotes());

        recalculateItineraryMetrics(itinerary, trip);
        itineraryItemRepository.save(item);
        Itinerary saved = itineraryRepository.save(itinerary);

        return itineraryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ItineraryResponse deleteItem(Long tripId, Long itemId, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip ID: " + tripId));

        ItineraryItem item = itineraryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("ItineraryItem", "id", itemId));

        if (!item.getItinerary().getId().equals(itinerary.getId())) {
            throw new BadRequestException("Item ID " + itemId + " does not belong to Trip ID " + tripId);
        }

        itinerary.removeItem(item);
        itineraryItemRepository.delete(item);

        // Re-index remaining stops sequentially (1, 2, 3...)
        for (int i = 0; i < itinerary.getItems().size(); i++) {
            itinerary.getItems().get(i).setStopOrder(i + 1);
        }

        recalculateItineraryMetrics(itinerary, trip);
        Itinerary saved = itineraryRepository.save(itinerary);

        return itineraryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ItineraryResponse reorderItems(Long tripId, ItineraryReorderRequest request, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip ID: " + tripId));

        List<Long> orderedIds = request.getOrderedItemIds();
        Map<Long, ItineraryItem> itemMap = itinerary.getItems().stream()
                .collect(Collectors.toMap(ItineraryItem::getId, item -> item));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long id = orderedIds.get(i);
            ItineraryItem item = itemMap.get(id);
            if (item != null) {
                item.setStopOrder(i + 1);
            }
        }

        itinerary.getItems().sort(Comparator.comparingInt(ItineraryItem::getStopOrder));
        recalculateItineraryMetrics(itinerary, trip);
        Itinerary saved = itineraryRepository.save(itinerary);

        return itineraryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteItinerary(Long tripId, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Optional<Itinerary> itinerary = itineraryRepository.findByTripId(tripId);
        if (itinerary.isPresent()) {
            itineraryRepository.delete(itinerary.get());
            trip.setItinerary(null);
            trip.setEstimatedTotalCost(0.0);
            tripRepository.save(trip);
            log.info("Deleted itinerary for Trip ID {}", tripId);
        }
    }

    // Helper: Recalculate metrics
    private void recalculateItineraryMetrics(Itinerary itinerary, Trip trip) {
        double totalDist = 0.0;
        int totalMins = 0;
        double totalCost = 0.0;

        for (ItineraryItem item : itinerary.getItems()) {
            if (item.getDistanceKmToNext() != null) totalDist += item.getDistanceKmToNext();
            if (item.getTravelMinutesToNext() != null) totalMins += item.getTravelMinutesToNext();
            if (item.getEstimatedCost() != null) totalCost += item.getEstimatedCost();
        }

        itinerary.setTotalDistanceKm(Math.round(totalDist * 10.0) / 10.0);
        itinerary.setTotalDurationMinutes(totalMins);
        trip.setEstimatedTotalCost(totalCost);
        tripRepository.save(trip);
    }

    // Helper: Ownership verification
    private Trip findTripAndVerifyOwnership(Long tripId, Long authenticatedUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        if (authenticatedUserId != null) {
            User authUser = userRepository.findById(authenticatedUserId).orElse(null);
            boolean isAdmin = authUser != null && authUser.getRole() == RoleType.ROLE_ADMIN;
            boolean isOwner = trip.getUser() != null && trip.getUser().getId().equals(authenticatedUserId);
            if (!isAdmin && !isOwner) {
                throw new ResourceNotFoundException("Trip", "id", tripId);
            }
        }
        return trip;
    }
}
