package com.daytripai.dto.response;

/**
 * Projected Cost & Budget Breakdown DTO for an Itinerary.
 */
public class BudgetBreakdownResponse {

    private Double totalBudget;
    private Double estimatedTotalCost;
    private Double projectedRemainingBudget;
    private Double ticketsEstimatedCost;
    private Double foodEstimatedCost;
    private Double transportEstimatedCost;
    private Double contingencyEstimatedCost;
    private boolean isBudgetSufficient;

    public BudgetBreakdownResponse() {
    }

    public BudgetBreakdownResponse(Double totalBudget, Double estimatedTotalCost, Double projectedRemainingBudget,
                                   Double ticketsEstimatedCost, Double foodEstimatedCost, Double transportEstimatedCost,
                                   Double contingencyEstimatedCost, boolean isBudgetSufficient) {
        this.totalBudget = totalBudget;
        this.estimatedTotalCost = estimatedTotalCost;
        this.projectedRemainingBudget = projectedRemainingBudget;
        this.ticketsEstimatedCost = ticketsEstimatedCost;
        this.foodEstimatedCost = foodEstimatedCost;
        this.transportEstimatedCost = transportEstimatedCost;
        this.contingencyEstimatedCost = contingencyEstimatedCost;
        this.isBudgetSufficient = isBudgetSufficient;
    }

    // Getters and Setters
    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Double getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(Double estimatedTotalCost) {
        this.estimatedTotalCost = estimatedTotalCost;
    }

    public Double getProjectedRemainingBudget() {
        return projectedRemainingBudget;
    }

    public void setProjectedRemainingBudget(Double projectedRemainingBudget) {
        this.projectedRemainingBudget = projectedRemainingBudget;
    }

    public Double getTicketsEstimatedCost() {
        return ticketsEstimatedCost;
    }

    public void setTicketsEstimatedCost(Double ticketsEstimatedCost) {
        this.ticketsEstimatedCost = ticketsEstimatedCost;
    }

    public Double getFoodEstimatedCost() {
        return foodEstimatedCost;
    }

    public void setFoodEstimatedCost(Double foodEstimatedCost) {
        this.foodEstimatedCost = foodEstimatedCost;
    }

    public Double getTransportEstimatedCost() {
        return transportEstimatedCost;
    }

    public void setTransportEstimatedCost(Double transportEstimatedCost) {
        this.transportEstimatedCost = transportEstimatedCost;
    }

    public Double getContingencyEstimatedCost() {
        return contingencyEstimatedCost;
    }

    public void setContingencyEstimatedCost(Double contingencyEstimatedCost) {
        this.contingencyEstimatedCost = contingencyEstimatedCost;
    }

    public boolean isBudgetSufficient() {
        return isBudgetSufficient;
    }

    public void setBudgetSufficient(boolean budgetSufficient) {
        isBudgetSufficient = budgetSufficient;
    }
}
