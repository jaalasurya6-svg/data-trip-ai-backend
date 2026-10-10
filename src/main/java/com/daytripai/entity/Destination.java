package com.daytripai.entity;

import com.daytripai.audit.BaseAuditableEntity;
import com.daytripai.enums.DestinationCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Destination Entity.
 * Represents tourist attractions, points of interest, landmarks, and activity spots.
 */
@Entity
@Table(name = "destinations", indexes = {
        @Index(name = "idx_destinations_city", columnList = "city"),
        @Index(name = "idx_destinations_category", columnList = "category"),
        @Index(name = "idx_destinations_popular", columnList = "is_popular")
})
public class Destination extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 80)
    private String state;

    @Column(nullable = false, length = 80)
    private String country = "India";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DestinationCategory category;

    @Column(name = "estimated_cost", nullable = false)
    private Double estimatedCost = 0.0;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "average_visit_duration_hours")
    private Double averageVisitDurationHours = 1.5;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private Double rating = 4.5;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String address;

    @Column(name = "is_popular", nullable = false)
    private boolean isPopular = false;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    public Destination() {
    }

    public Destination(String name, String description, String city, String state, DestinationCategory category,
                       Double estimatedCost, LocalTime openingTime, LocalTime closingTime,
                       Double averageVisitDurationHours, Double latitude, Double longitude,
                       Double rating, Integer reviewCount, String imageUrl, String address, boolean isPopular) {
        this.name = name;
        this.description = description;
        this.city = city;
        this.state = state;
        this.category = category;
        this.estimatedCost = estimatedCost;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.averageVisitDurationHours = averageVisitDurationHours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageUrl = imageUrl;
        this.address = address;
        this.isPopular = isPopular;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
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

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }
}
