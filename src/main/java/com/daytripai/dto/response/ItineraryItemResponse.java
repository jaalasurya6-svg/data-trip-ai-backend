package com.daytripai.dto.response;

import com.daytripai.enums.DestinationCategory;

import java.time.LocalTime;

/**
 * Response DTO representing an individual scheduled stop within an itinerary timeline.
 */
public class ItineraryItemResponse {

    private Long id;
    private Long destinationId;
    private Integer stopOrder;
    private String placeName;
    private String activityDescription;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Double estimatedCost;
    private String transportModeToNext;
    private Integer travelMinutesToNext;
    private Double distanceKmToNext;
    private String imageUrl;
    private DestinationCategory category;
    private String notes;

    public ItineraryItemResponse() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public Integer getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(Integer stopOrder) {
        this.stopOrder = stopOrder;
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DestinationCategory getCategory() {
        return category;
    }

    public void setCategory(DestinationCategory category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
