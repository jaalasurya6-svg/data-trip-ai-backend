package com.daytripai.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * Request DTO for creating or updating a single scheduled stop within an itinerary.
 */
public class ItineraryItemRequest {

    private Long destinationId;

    @NotBlank(message = "Place name is required")
    @Size(min = 2, max = 150, message = "Place name must be between 2 and 150 characters")
    private String placeName;

    private String activityDescription;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Estimated cost is required")
    @Min(value = 0, message = "Estimated cost cannot be negative")
    private Double estimatedCost = 0.0;

    private String transportModeToNext = "CAB";
    private Integer travelMinutesToNext = 15;
    private Double distanceKmToNext = 3.0;
    private String notes;

    public ItineraryItemRequest() {
    }

    public ItineraryItemRequest(Long destinationId, String placeName, String activityDescription,
                                LocalTime startTime, LocalTime endTime, Double estimatedCost,
                                String transportModeToNext, Integer travelMinutesToNext, Double distanceKmToNext) {
        this.destinationId = destinationId;
        this.placeName = placeName;
        this.activityDescription = activityDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedCost = estimatedCost;
        this.transportModeToNext = transportModeToNext;
        this.travelMinutesToNext = travelMinutesToNext;
        this.distanceKmToNext = distanceKmToNext;
    }

    // Getters and Setters
    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getTransportModeToNext() {
        return transportModeToNext;
    }

    public void setTransportModeToNext(String transportModeToNext) {
        this.transportModeToNext = transportModeToNext;
    }

    public Integer getTravelMinutesToNext() {
        return travelMinutesToNext;
    }

    public void setTravelMinutesToNext(Integer travelMinutesToNext) {
        this.travelMinutesToNext = travelMinutesToNext;
    }

    public Double getDistanceKmToNext() {
        return distanceKmToNext;
    }

    public void setDistanceKmToNext(Double distanceKmToNext) {
        this.distanceKmToNext = distanceKmToNext;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
