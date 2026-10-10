package com.daytripai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;

/**
 * ItineraryItem Entity.
 * Represents an individual timed stop/activity within an itinerary timeline.
 */
@Entity
@Table(name = "itinerary_items", indexes = {
        @Index(name = "idx_itinerary_items_order", columnList = "itinerary_id, stop_order")
})
public class ItineraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    @JsonIgnore
    private Itinerary itinerary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder = 1;

    @Column(name = "place_name", nullable = false, length = 150)
    private String placeName;

    @Column(name = "activity_description", columnDefinition = "TEXT")
    private String activityDescription;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "estimated_cost", nullable = false)
    private Double estimatedCost = 0.0;

    @Column(name = "transport_mode_to_next", length = 50)
    private String transportModeToNext = "CAB";

    @Column(name = "travel_minutes_to_next")
    private Integer travelMinutesToNext = 15;

    @Column(name = "distance_km_to_next")
    private Double distanceKmToNext = 3.5;

    @Column(length = 255)
    private String notes;

    public ItineraryItem() {
    }

    public ItineraryItem(Itinerary itinerary, Destination destination, Integer stopOrder,
                         String placeName, String activityDescription, LocalTime startTime,
                         LocalTime endTime, Double estimatedCost, String transportModeToNext,
                         Integer travelMinutesToNext, Double distanceKmToNext) {
        this.itinerary = itinerary;
        this.destination = destination;
        this.stopOrder = stopOrder;
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
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
