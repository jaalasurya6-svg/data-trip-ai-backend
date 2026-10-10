package com.daytripai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * RouteEstimateResponse DTO.
 * High-precision routing metric calculation between origin and destination coordinates.
 */
@Schema(description = "Route estimation with transit distance, duration, steps, and fare")
public class RouteEstimateResponse {

    @Schema(description = "Origin coordinates [lat, lon]", example = "[18.9220, 72.8347]")
    private List<Double> origin;

    @Schema(description = "Destination coordinates [lat, lon]", example = "[18.9633, 72.8311]")
    private List<Double> destination;

    @Schema(description = "Total estimated road distance in kilometers", example = "5.8")
    private Double distanceKm;

    @Schema(description = "Total estimated travel duration in minutes", example = "22")
    private Integer durationMinutes;

    @Schema(description = "Travel modality", example = "DRIVING")
    private String transportMode;

    @Schema(description = "Estimated transit fare or fuel cost in INR", example = "130.0")
    private Double estimatedFare;

    @Schema(description = "High-level route summary", example = "Fastest route via Dr. DN Road and Marine Lines")
    private String summary;

    @Schema(description = "Step-by-step navigation cues")
    private List<RouteStepResponse> steps;

    @Schema(description = "Geographic engine provider", example = "DayTrip AI Haversine GeoRouting Engine")
    private String provider;

    public RouteEstimateResponse() {
    }

    public RouteEstimateResponse(List<Double> origin, List<Double> destination, Double distanceKm,
                                 Integer durationMinutes, String transportMode, Double estimatedFare,
                                 String summary, List<RouteStepResponse> steps, String provider) {
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.transportMode = transportMode;
        this.estimatedFare = estimatedFare;
        this.summary = summary;
        this.steps = steps;
        this.provider = provider;
    }

    // Getters and Setters
    public List<Double> getOrigin() {
        return origin;
    }

    public void setOrigin(List<Double> origin) {
        this.origin = origin;
    }

    public List<Double> getDestination() {
        return destination;
    }

    public void setDestination(List<Double> destination) {
        this.destination = destination;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public Double getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(Double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<RouteStepResponse> getSteps() {
        return steps;
    }

    public void setSteps(List<RouteStepResponse> steps) {
        this.steps = steps;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
