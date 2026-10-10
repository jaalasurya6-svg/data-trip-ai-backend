package com.daytripai.entity;

import com.daytripai.audit.BaseAuditableEntity;
import com.daytripai.enums.TripStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Trip Entity.
 * Represents a planned one-day trip customized with starting point, budget, and time boundaries.
 */
@Entity
@Table(name = "trips", indexes = {
        @Index(name = "idx_trips_user_id", columnList = "user_id"),
        @Index(name = "idx_trips_city", columnList = "destination_city"),
        @Index(name = "idx_trips_date", columnList = "trip_date")
})
public class Trip extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "starting_location", nullable = false, length = 150)
    private String startingLocation;

    @Column(name = "destination_city", nullable = false, length = 100)
    private String destinationCity;

    @Column(name = "trip_date", nullable = false)
    private LocalDate tripDate;

    @Column(nullable = false)
    private Double budget;

    @Column(name = "available_hours", nullable = false)
    private Integer availableHours = 8;

    @Column(name = "group_size", nullable = false)
    private Integer groupSize = 1;

    @Column(name = "travel_preference", nullable = false, length = 100)
    private String travelPreference;

    @Column(name = "estimated_total_cost")
    private Double estimatedTotalCost = 0.0;

    @Column(name = "actual_total_spent")
    private Double actualTotalSpent = 0.0;

    @Column(name = "remaining_budget")
    private Double remainingBudget = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TripStatus status = TripStatus.PLANNED;

    @Column(name = "weather_forecast_summary", length = 255)
    private String weatherForecastSummary;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Itinerary itinerary;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    public Trip() {
    }

    public Trip(User user, String startingLocation, String destinationCity, LocalDate tripDate,
                Double budget, Integer availableHours, Integer groupSize, String travelPreference) {
        this.user = user;
        this.startingLocation = startingLocation;
        this.destinationCity = destinationCity;
        this.tripDate = tripDate;
        this.budget = budget;
        this.availableHours = availableHours;
        this.groupSize = groupSize;
        this.travelPreference = travelPreference;
        this.remainingBudget = budget;
        this.status = TripStatus.PLANNED;
    }

    // Helper methods for expenses
    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setTrip(this);
        recalculateBudget();
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setTrip(null);
        recalculateBudget();
    }

    public void recalculateBudget() {
        double total = 0.0;
        for (Expense exp : expenses) {
            if (exp.getAmount() != null) {
                total += exp.getAmount();
            }
        }
        this.actualTotalSpent = total;
        this.remainingBudget = (this.budget != null ? this.budget : 0.0) - total;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public LocalDate getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Integer getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(Integer availableHours) {
        this.availableHours = availableHours;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    public String getTravelPreference() {
        return travelPreference;
    }

    public void setTravelPreference(String travelPreference) {
        this.travelPreference = travelPreference;
    }

    public Double getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(Double estimatedTotalCost) {
        this.estimatedTotalCost = estimatedTotalCost;
    }

    public Double getActualTotalSpent() {
        return actualTotalSpent;
    }

    public void setActualTotalSpent(Double actualTotalSpent) {
        this.actualTotalSpent = actualTotalSpent;
    }

    public Double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(Double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public String getWeatherForecastSummary() {
        return weatherForecastSummary;
    }

    public void setWeatherForecastSummary(String weatherForecastSummary) {
        this.weatherForecastSummary = weatherForecastSummary;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        this.itinerary = itinerary;
        if (itinerary != null) {
            itinerary.setTrip(this);
        }
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
