package com.daytripai.dto.request;

import com.daytripai.enums.DestinationCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * Request DTO for creating or updating a tourist destination.
 */
public class DestinationRequest {

    @NotBlank(message = "Destination name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    private String description;

    @NotBlank(message = "City is required")
    @Size(max = 80, message = "City name cannot exceed 80 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 80, message = "State name cannot exceed 80 characters")
    private String state;

    private String country = "India";

    @NotNull(message = "Category is required")
    private DestinationCategory category;

    @NotNull(message = "Estimated cost is required")
    @Min(value = 0, message = "Estimated cost cannot be negative")
    private Double estimatedCost;

    private LocalTime openingTime;
    private LocalTime closingTime;

    @DecimalMin(value = "0.5", message = "Average duration must be at least 0.5 hours")
    private Double averageVisitDurationHours = 1.5;

    private Double latitude;
    private Double longitude;

    private String imageUrl;
    private String address;
    private boolean isPopular = false;

    public DestinationRequest() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public DestinationCategory getCategory() {
        return category;
    }

    public void setCategory(DestinationCategory category) {
        this.category = category;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Double getAverageVisitDurationHours() {
        return averageVisitDurationHours;
    }

    public void setAverageVisitDurationHours(Double averageVisitDurationHours) {
        this.averageVisitDurationHours = averageVisitDurationHours;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }
}
