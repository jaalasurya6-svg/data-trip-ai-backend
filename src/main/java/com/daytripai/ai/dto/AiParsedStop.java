package com.daytripai.ai.dto;

import java.time.LocalTime;

/**
 * Parsed stop item produced by AI generation models.
 */
public class AiParsedStop {

    private Long destinationId;
    private String placeName;
    private String activityDescription;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double estimatedCost = 0.0;
    private String transportModeToNext = "CAB";
    private Integer travelMinutesToNext = 15;
    private Double distanceKmToNext = 3.0;

    public AiParsedStop() {
    }

    public AiParsedStop(Long destinationId, String placeName, String activityDescription,
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
}
