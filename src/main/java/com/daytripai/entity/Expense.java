package com.daytripai.entity;

import com.daytripai.audit.BaseAuditableEntity;
import com.daytripai.enums.ExpenseCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Expense Entity.
 * Tracks actual expenditures incurred during a day trip categorized by purpose.
 */
@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_expenses_trip_id", columnList = "trip_id"),
        @Index(name = "idx_expenses_category", columnList = "category")
})
public class Expense extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonIgnore
    private Trip trip;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseCategory category;

    @Column(name = "spent_at", nullable = false)
    private LocalDateTime spentAt = LocalDateTime.now();

    @Column(length = 255)
    private String notes;

    public Expense() {
    }

    public Expense(Trip trip, String title, Double amount, ExpenseCategory category, LocalDateTime spentAt, String notes) {
        this.trip = trip;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.spentAt = spentAt != null ? spentAt : LocalDateTime.now();
        this.notes = notes;
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
}
