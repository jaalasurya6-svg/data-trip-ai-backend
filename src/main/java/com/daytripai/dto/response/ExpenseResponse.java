package com.daytripai.dto.response;

import com.daytripai.enums.ExpenseCategory;

import java.time.LocalDateTime;

/**
 * Response DTO representing an individual logged expense.
 */
public class ExpenseResponse {

    private Long id;
    private Long tripId;
    private String title;
    private Double amount;
    private ExpenseCategory category;
    private LocalDateTime spentAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExpenseResponse() {
    }

    public ExpenseResponse(Long id, Long tripId, String title, Double amount, ExpenseCategory category,
                           LocalDateTime spentAt, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tripId = tripId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.spentAt = spentAt;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public LocalDateTime getSpentAt() {
        return spentAt;
    }

    public void setSpentAt(LocalDateTime spentAt) {
        this.spentAt = spentAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
