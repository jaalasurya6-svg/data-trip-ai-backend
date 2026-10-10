package com.daytripai.dto.request;

import com.daytripai.enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for logging or updating an actual expense during a trip.
 */
public class ExpenseRequest {

    @NotBlank(message = "Expense title is required")
    @Size(min = 2, max = 150, message = "Expense title must be between 2 and 150 characters")
    private String title;

    @NotNull(message = "Expense amount is required")
    @Positive(message = "Expense amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Expense category is required")
    private ExpenseCategory category;

    private LocalDateTime spentAt;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;

    public ExpenseRequest() {
    }

    public ExpenseRequest(String title, Double amount, ExpenseCategory category, LocalDateTime spentAt, String notes) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.spentAt = spentAt;
        this.notes = notes;
    }

    // Getters and Setters
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
