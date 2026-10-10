package com.daytripai.dto.response;

import com.daytripai.enums.DestinationCategory;

import java.time.LocalDateTime;

/**
 * Response DTO representing a bookmarked favorite destination.
 */
public class FavoriteResponse {

    private Long id;
    private Long destinationId;
    private String destinationName;
    private String city;
    private String state;
    private DestinationCategory category;
    private Double estimatedCost;
    private Double rating;
    private String imageUrl;
    private LocalDateTime savedAt;

    public FavoriteResponse() {
    }

    public FavoriteResponse(Long id, Long destinationId, String destinationName, String city, String state,
                            DestinationCategory category, Double estimatedCost, Double rating,
                            String imageUrl, LocalDateTime savedAt) {
        this.id = id;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.city = city;
        this.state = state;
        this.category = category;
        this.estimatedCost = estimatedCost;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.savedAt = savedAt;
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

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
