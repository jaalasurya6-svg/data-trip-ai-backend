package com.daytripai.entity;

import com.daytripai.audit.BaseAuditableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Itinerary Entity.
 * Represents the structured timeline generated for a specific Trip.
 */
@Entity
@Table(name = "itineraries")
public class Itinerary extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    @JsonIgnore
    private Trip trip;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "total_distance_km")
    private Double totalDistanceKm = 0.0;

    @Column(name = "total_duration_minutes")
    private Integer totalDurationMinutes = 0;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stopOrder ASC")
    private List<ItineraryItem> items = new ArrayList<>();

    public Itinerary() {
    }

    public Itinerary(Trip trip, String summary) {
        this.trip = trip;
        this.summary = summary;
    }

    public void addItem(ItineraryItem item) {
        items.add(item);
        item.setItinerary(this);
    }

    public void removeItem(ItineraryItem item) {
        items.remove(item);
        item.setItinerary(null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
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

    public List<ItineraryItem> getItems() {
        return items;
    }

    public void setItems(List<ItineraryItem> items) {
        this.items = items;
    }
}
