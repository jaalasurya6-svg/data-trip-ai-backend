package com.daytripai.dto.response;

import com.daytripai.enums.TripStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO representing detailed information of a Trip.
 */
public class TripResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String startingLocation;
    private String destinationCity;
    private LocalDate tripDate;
    private Double budget;
    private Integer availableHours;
    private Integer groupSize;
    private String travelPreference;
    private Double estimatedTotalCost;
    private Double actualTotalSpent;
    private Double remainingBudget;
    private TripStatus status;
    private String weatherForecastSummary;
    private boolean hasItinerary;
    private int expenseCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TripResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
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
        return travelPreference;
    }

    public void setTravelPreference(String travelPreference) {
        this.travelPreference = travelPreference;
    }

    public Double getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(Double estimatedTotalCost) {
        this.estimatedTotalCost = estimatedTotalCost;
    }

    public Double getActualTotalSpent() {
        return actualTotalSpent;
    }

    public void setActualTotalSpent(Double actualTotalSpent) {
        this.actualTotalSpent = actualTotalSpent;
    }

    public Double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(Double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public String getWeatherForecastSummary() {
        return weatherForecastSummary;
    }

    public void setWeatherForecastSummary(String weatherForecastSummary) {
        this.weatherForecastSummary = weatherForecastSummary;
    }

    public boolean isHasItinerary() {
        return hasItinerary;
    }

    public void setHasItinerary(boolean hasItinerary) {
        this.hasItinerary = hasItinerary;
    }

    public int getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(int expenseCount) {
        this.expenseCount = expenseCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
