package com.daytripai.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating a planned Day Trip.
 */
public class TripRequest {

    private Long userId;

    @NotBlank(message = "Starting location is required")
    @Size(min = 2, max = 150, message = "Starting location must be between 2 and 150 characters")
    private String startingLocation;

    @NotBlank(message = "Destination city is required")
    @Size(min = 2, max = 100, message = "Destination city must be between 2 and 100 characters")
    private String destinationCity;

    @NotNull(message = "Trip date is required")
    @FutureOrPresent(message = "Trip date cannot be in the past")
    private LocalDate tripDate;

    @NotNull(message = "Budget is required")
    @Positive(message = "Budget must be greater than zero")
    @Min(value = 100, message = "Minimum trip budget must be at least ₹100")
    private Double budget;

    @NotNull(message = "Available hours is required")
    @Min(value = 2, message = "Available time must be at least 2 hours")
    @Max(value = 16, message = "Available time cannot exceed 16 hours for a one-day trip")
    private Integer availableHours = 8;

    @NotNull(message = "Group size is required")
    @Min(value = 1, message = "Group size must be at least 1 traveler")
    @Max(value = 50, message = "Group size cannot exceed 50 travelers")
    private Integer groupSize = 1;

    @NotBlank(message = "Travel preference is required")
    @Size(max = 100, message = "Travel preference cannot exceed 100 characters")
    private String travelPreference;

    public TripRequest() {
    }

    public TripRequest(String startingLocation, String destinationCity, LocalDate tripDate,
                       Double budget, Integer availableHours, Integer groupSize, String travelPreference) {
        this.startingLocation = startingLocation;
        this.destinationCity = destinationCity;
        this.tripDate = tripDate;
        this.budget = budget;
        this.availableHours = availableHours;
        this.groupSize = groupSize;
        this.travelPreference = travelPreference;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public LocalDate getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Integer getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(Integer availableHours) {
        this.availableHours = availableHours;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    public String getTravelPreference() {
        return travelPreference != null && !travelPreference.isBlank() ? travelPreference : "BALANCED";
    }

    public void setTravelPreference(String travelPreference) {
        this.travelPreference = travelPreference;
    }

    // Flexible alias setters for UI payload compatibility
    public void setBudgetLimit(Double budgetLimit) {
        if (this.budget == null) {
            this.budget = budgetLimit;
        }
    }

    public void setStartLocationName(String startLocationName) {
        if (this.startingLocation == null || this.startingLocation.isBlank()) {
            this.startingLocation = startLocationName;
        }
    }

    public void setPacingPreference(String pacingPreference) {
        if (this.travelPreference == null || this.travelPreference.isBlank()) {
            this.travelPreference = pacingPreference;
        }
    }

    public void setPreferredCategory(String category) {
        if (this.travelPreference == null || this.travelPreference.isBlank()) {
            this.travelPreference = category;
        }
    }

    public void setTitle(String title) {
        // Ignored or used as fallback starting location if needed
        if ((this.startingLocation == null || this.startingLocation.isBlank()) && title != null && !title.isBlank()) {
            this.startingLocation = title;
        }
    }

    public void setDestinationIds(java.util.List<Long> destinationIds) {
        // Handled or ignored gracefully
    }
}
