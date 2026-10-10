package com.daytripai.dto.response;

import com.daytripai.enums.TripStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Summary Response DTO containing high-level analytics and budget utilization for a Trip.
 */
public class TripSummaryResponse {

    private Long tripId;
    private String destinationCity;
    private LocalDate tripDate;
    private Double budget;
    private Double actualTotalSpent;
    private Double remainingBudget;
    private Double budgetUtilizationPercentage;

    @JsonProperty("isOverBudget")
    private boolean isOverBudget;

    private int expenseCount;
    private TripStatus status;

    public TripSummaryResponse() {
    }

    public TripSummaryResponse(Long tripId, String destinationCity, LocalDate tripDate, Double budget,
                               Double actualTotalSpent, Double remainingBudget, Double budgetUtilizationPercentage,
                               boolean isOverBudget, int expenseCount, TripStatus status) {
        this.tripId = tripId;
        this.destinationCity = destinationCity;
        this.tripDate = tripDate;
        this.budget = budget;
        this.actualTotalSpent = actualTotalSpent;
        this.remainingBudget = remainingBudget;
        this.budgetUtilizationPercentage = budgetUtilizationPercentage;
        this.isOverBudget = isOverBudget;
        this.expenseCount = expenseCount;
        this.status = status;
    }

    // Getters and Setters
    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
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

    public Double getBudgetUtilizationPercentage() {
        return budgetUtilizationPercentage;
    }

    public void setBudgetUtilizationPercentage(Double budgetUtilizationPercentage) {
        this.budgetUtilizationPercentage = budgetUtilizationPercentage;
    }

    public boolean isOverBudget() {
        return isOverBudget;
    }

    public void setOverBudget(boolean overBudget) {
        isOverBudget = overBudget;
    }

    public int getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(int expenseCount) {
        this.expenseCount = expenseCount;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }
}
