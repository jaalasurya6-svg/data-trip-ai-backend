package com.daytripai.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request DTO for re-ordering stops within an itinerary.
 */
public class ItineraryReorderRequest {

    @NotEmpty(message = "Ordered item IDs list cannot be empty")
    private List<Long> orderedItemIds;

    public ItineraryReorderRequest() {
    }

    public ItineraryReorderRequest(List<Long> orderedItemIds) {
        this.orderedItemIds = orderedItemIds;
    }

    public List<Long> getOrderedItemIds() {
        return orderedItemIds;
    }

    public void setOrderedItemIds(List<Long> orderedItemIds) {
        this.orderedItemIds = orderedItemIds;
    }
}
