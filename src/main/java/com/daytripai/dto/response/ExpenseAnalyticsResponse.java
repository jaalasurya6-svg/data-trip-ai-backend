package com.daytripai.dto.response;

import com.daytripai.enums.ExpenseCategory;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Analytical breakdown of trip expenditures across categories with budget comparison.
 */
public class ExpenseAnalyticsResponse {

    private Long tripId;
    private Double totalBudget;
    private Double actualTotalSpent;
    private Double remainingBudget;
    private Double budgetUtilizationPercentage;

    @JsonProperty("isOverBudget")
    private boolean isOverBudget;

    private Double overBudgetAmount;
    private Map<ExpenseCategory, Double> categoryTotals;
    private Map<ExpenseCategory, Double> categoryPercentages;
    private int totalExpenseCount;

    public ExpenseAnalyticsResponse() {
    }

    public ExpenseAnalyticsResponse(Long tripId, Double totalBudget, Double actualTotalSpent, Double remainingBudget,
                                    Double budgetUtilizationPercentage, boolean isOverBudget, Double overBudgetAmount,
                                    Map<ExpenseCategory, Double> categoryTotals, Map<ExpenseCategory, Double> categoryPercentages,
                                    int totalExpenseCount) {
        this.tripId = tripId;
        this.totalBudget = totalBudget;
        this.actualTotalSpent = actualTotalSpent;
        this.remainingBudget = remainingBudget;
        this.budgetUtilizationPercentage = budgetUtilizationPercentage;
        this.isOverBudget = isOverBudget;
        this.overBudgetAmount = overBudgetAmount;
        this.categoryTotals = categoryTotals;
        this.categoryPercentages = categoryPercentages;
        this.totalExpenseCount = totalExpenseCount;
    }

    // Getters and Setters
    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
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

    public Double getBudgetUtilizationPercentage() {
        return budgetUtilizationPercentage;
    }

    public void setBudgetUtilizationPercentage(Double budgetUtilizationPercentage) {
        this.budgetUtilizationPercentage = budgetUtilizationPercentage;
    }

    public boolean isOverBudget() {
        return isOverBudget;
    }

    public void setOverBudget(boolean overBudget) {
        isOverBudget = overBudget;
    }

    public Double getOverBudgetAmount() {
        return overBudgetAmount;
    }

    public void setOverBudgetAmount(Double overBudgetAmount) {
        this.overBudgetAmount = overBudgetAmount;
    }

    public Map<ExpenseCategory, Double> getCategoryTotals() {
        return categoryTotals;
    }

    public void setCategoryTotals(Map<ExpenseCategory, Double> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }

    public Map<ExpenseCategory, Double> getCategoryPercentages() {
        return categoryPercentages;
    }

    public void setCategoryPercentages(Map<ExpenseCategory, Double> categoryPercentages) {
        this.categoryPercentages = categoryPercentages;
    }

    public int getTotalExpenseCount() {
        return totalExpenseCount;
    }

    public void setTotalExpenseCount(int totalExpenseCount) {
        this.totalExpenseCount = totalExpenseCount;
    }
}
