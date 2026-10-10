package com.daytripai.mapper;

import com.daytripai.dto.request.ItineraryItemRequest;
import com.daytripai.dto.response.BudgetBreakdownResponse;
import com.daytripai.dto.response.ItineraryItemResponse;
import com.daytripai.dto.response.ItineraryResponse;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Itinerary;
import com.daytripai.entity.ItineraryItem;
import com.daytripai.entity.Trip;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for Itinerary entity to response DTO transformations.
 */
@Component
public class ItineraryMapper {

    public ItineraryResponse toResponse(Itinerary itinerary) {
        if (itinerary == null) {
            return null;
        }

        ItineraryResponse response = new ItineraryResponse();
        response.setId(itinerary.getId());
        if (itinerary.getTrip() != null) {
            response.setTripId(itinerary.getTrip().getId());
        }
        response.setSummary(itinerary.getSummary());
        response.setTotalDistanceKm(itinerary.getTotalDistanceKm());
        response.setTotalDurationMinutes(itinerary.getTotalDurationMinutes());

        List<ItineraryItemResponse> itemResponses = itinerary.getItems() != null ?
                itinerary.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()) :
                new ArrayList<>();

        response.setItems(itemResponses);
        response.setStopCount(itemResponses.size());

        double totalCost = itemResponses.stream()
                .mapToDouble(i -> i.getEstimatedCost() != null ? i.getEstimatedCost() : 0.0)
                .sum();
        response.setTotalEstimatedCost(totalCost);

        // Calculate detailed budget breakdown
        Trip trip = itinerary.getTrip();
        if (trip != null) {
            double tripBudget = trip.getBudget() != null ? trip.getBudget() : 0.0;
            double ticketsCost = itemResponses.stream()
                    .filter(i -> i.getDestinationId() != null)
                    .mapToDouble(i -> i.getEstimatedCost() != null ? i.getEstimatedCost() : 0.0)
                    .sum();
            double foodCost = itemResponses.stream()
                    .filter(i -> i.getDestinationId() == null || (i.getPlaceName() != null && i.getPlaceName().toLowerCase().contains("lunch")))
                    .mapToDouble(i -> i.getEstimatedCost() != null ? i.getEstimatedCost() : 0.0)
                    .sum();
            double transportCost = itemResponses.stream()
                    .mapToDouble(i -> i.getDistanceKmToNext() != null ? i.getDistanceKmToNext() * 25.0 : 0.0)
                    .sum();
            double contingencyCost = Math.round(tripBudget * 0.10);
            double remaining = tripBudget - totalCost;

            BudgetBreakdownResponse breakdown = new BudgetBreakdownResponse(
                    tripBudget,
                    totalCost,
                    remaining,
                    ticketsCost,
                    foodCost,
                    Math.round(transportCost * 100.0) / 100.0,
                    contingencyCost,
                    remaining >= 0
            );
            response.setBudgetBreakdown(breakdown);
        }

        return response;
    }

    public ItineraryItemResponse toItemResponse(ItineraryItem item) {
        if (item == null) {
            return null;
        }

        ItineraryItemResponse response = new ItineraryItemResponse();
        response.setId(item.getId());
        response.setStopOrder(item.getStopOrder());
        response.setPlaceName(item.getPlaceName());
        response.setActivityDescription(item.getActivityDescription());
        response.setStartTime(item.getStartTime());
        response.setEndTime(item.getEndTime());

        if (item.getStartTime() != null && item.getEndTime() != null) {
            long duration = Duration.between(item.getStartTime(), item.getEndTime()).toMinutes();
            response.setDurationMinutes((int) Math.max(duration, 0));
        }

        response.setEstimatedCost(item.getEstimatedCost());
        response.setTransportModeToNext(item.getTransportModeToNext());
        response.setTravelMinutesToNext(item.getTravelMinutesToNext());
        response.setDistanceKmToNext(item.getDistanceKmToNext());
        response.setNotes(item.getNotes());

        Destination dest = item.getDestination();
        if (dest != null) {
            response.setDestinationId(dest.getId());
            response.setImageUrl(dest.getImageUrl());
            response.setCategory(dest.getCategory());
        }

        return response;
    }

    public ItineraryItem toEntity(ItineraryItemRequest request, Itinerary itinerary, Destination destination, int stopOrder) {
        ItineraryItem item = new ItineraryItem();
        item.setItinerary(itinerary);
        item.setDestination(destination);
        item.setStopOrder(stopOrder);
        item.setPlaceName(request.getPlaceName());
        item.setActivityDescription(request.getActivityDescription());
        item.setStartTime(request.getStartTime());
        item.setEndTime(request.getEndTime());
        item.setEstimatedCost(request.getEstimatedCost());
        item.setTransportModeToNext(request.getTransportModeToNext());
        item.setTravelMinutesToNext(request.getTravelMinutesToNext() != null ? request.getTravelMinutesToNext() : 15);
        item.setDistanceKmToNext(request.getDistanceKmToNext() != null ? request.getDistanceKmToNext() : 3.0);
        item.setNotes(request.getNotes());

        return item;
    }
}
