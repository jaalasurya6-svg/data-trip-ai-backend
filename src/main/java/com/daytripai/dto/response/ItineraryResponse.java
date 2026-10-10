package com.daytripai.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Complete Response DTO for a Trip's Itinerary.
 */
public class ItineraryResponse {

    private Long id;
    private Long tripId;
    private String summary;
    private Double totalDistanceKm;
    private Integer totalDurationMinutes;
    private Double totalEstimatedCost;
    private int stopCount;
    private List<ItineraryItemResponse> items = new ArrayList<>();
    private BudgetBreakdownResponse budgetBreakdown;

    public ItineraryResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public Double getTotalEstimatedCost() {
        return totalEstimatedCost;
    }

    public void setTotalEstimatedCost(Double totalEstimatedCost) {
        this.totalEstimatedCost = totalEstimatedCost;
    }

    public int getStopCount() {
        return stopCount;
    }

    public void setStopCount(int stopCount) {
        this.stopCount = stopCount;
    }

    public List<ItineraryItemResponse> getItems() {
        return items;
    }

    public void setItems(List<ItineraryItemResponse> items) {
        this.items = items;
    }

    public BudgetBreakdownResponse getBudgetBreakdown() {
        return budgetBreakdown;
    }

    public void setBudgetBreakdown(BudgetBreakdownResponse budgetBreakdown) {
        this.budgetBreakdown = budgetBreakdown;
    }
}
