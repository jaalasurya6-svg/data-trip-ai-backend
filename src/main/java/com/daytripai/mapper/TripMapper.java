package com.daytripai.mapper;

import com.daytripai.dto.request.TripRequest;
import com.daytripai.dto.response.TripResponse;
import com.daytripai.dto.response.TripSummaryResponse;
import com.daytripai.entity.Trip;
import com.daytripai.entity.User;
import com.daytripai.enums.TripStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper component for Trip entity and DTO transformations.
 */
@Component
public class TripMapper {

    public TripResponse toResponse(Trip trip) {
        if (trip == null) {
            return null;
        }

        TripResponse response = new TripResponse();
        response.setId(trip.getId());
        if (trip.getUser() != null) {
            response.setUserId(trip.getUser().getId());
            response.setUserEmail(trip.getUser().getEmail());
            response.setUserFullName(trip.getUser().getFullName());
        }
        response.setStartingLocation(trip.getStartingLocation());
        response.setDestinationCity(trip.getDestinationCity());
        response.setTripDate(trip.getTripDate());
        response.setBudget(trip.getBudget());
        response.setAvailableHours(trip.getAvailableHours());
        response.setGroupSize(trip.getGroupSize());
        response.setTravelPreference(trip.getTravelPreference());
        response.setEstimatedTotalCost(trip.getEstimatedTotalCost() != null ? trip.getEstimatedTotalCost() : 0.0);
        response.setActualTotalSpent(trip.getActualTotalSpent() != null ? trip.getActualTotalSpent() : 0.0);
        response.setRemainingBudget(trip.getRemainingBudget() != null ? trip.getRemainingBudget() : trip.getBudget());
        response.setStatus(trip.getStatus());
        response.setWeatherForecastSummary(trip.getWeatherForecastSummary());
        response.setHasItinerary(trip.getItinerary() != null);
        response.setExpenseCount(trip.getExpenses() != null ? trip.getExpenses().size() : 0);
        response.setCreatedAt(trip.getCreatedAt());
        response.setUpdatedAt(trip.getUpdatedAt());

        return response;
    }

    public Trip toEntity(TripRequest request, User user) {
        if (request == null) {
            return null;
        }

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setStartingLocation(request.getStartingLocation());
        trip.setDestinationCity(request.getDestinationCity());
        trip.setTripDate(request.getTripDate());
        trip.setBudget(request.getBudget());
        trip.setAvailableHours(request.getAvailableHours());
        trip.setGroupSize(request.getGroupSize());
        trip.setTravelPreference(request.getTravelPreference());
        trip.setRemainingBudget(request.getBudget());
        trip.setActualTotalSpent(0.0);
        trip.setEstimatedTotalCost(0.0);
        trip.setStatus(TripStatus.PLANNED);

        return trip;
    }

    public void updateEntityFromRequest(Trip trip, TripRequest request) {
        if (trip == null || request == null) {
            return;
        }

        trip.setStartingLocation(request.getStartingLocation());
        trip.setDestinationCity(request.getDestinationCity());
        trip.setTripDate(request.getTripDate());
        trip.setBudget(request.getBudget());
        trip.setAvailableHours(request.getAvailableHours());
        trip.setGroupSize(request.getGroupSize());
        trip.setTravelPreference(request.getTravelPreference());
        trip.recalculateBudget();
    }

    public TripSummaryResponse toSummaryResponse(Trip trip) {
        if (trip == null) {
            return null;
        }

        double budget = trip.getBudget() != null ? trip.getBudget() : 0.0;
        double actualSpent = trip.getActualTotalSpent() != null ? trip.getActualTotalSpent() : 0.0;
        double remaining = trip.getRemainingBudget() != null ? trip.getRemainingBudget() : budget;
        double utilization = budget > 0 ? (actualSpent / budget) * 100.0 : 0.0;
        boolean overBudget = actualSpent > budget;
        int expenseCount = trip.getExpenses() != null ? trip.getExpenses().size() : 0;

        return new TripSummaryResponse(
                trip.getId(),
                trip.getDestinationCity(),
                trip.getTripDate(),
                budget,
                actualSpent,
                remaining,
                Math.round(utilization * 100.0) / 100.0,
                overBudget,
                expenseCount,
                trip.getStatus()
        );
    }
}
