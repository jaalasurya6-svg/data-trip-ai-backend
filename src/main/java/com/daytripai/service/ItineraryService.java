package com.daytripai.service;

import com.daytripai.dto.request.ItineraryItemRequest;
import com.daytripai.dto.request.ItineraryReorderRequest;
import com.daytripai.dto.response.ItineraryResponse;

public interface ItineraryService {

    ItineraryResponse generateItinerary(Long tripId, Long authenticatedUserId);

    ItineraryResponse getItineraryByTripId(Long tripId, Long authenticatedUserId);

    ItineraryResponse addItem(Long tripId, ItineraryItemRequest request, Long authenticatedUserId);

    ItineraryResponse updateItem(Long tripId, Long itemId, ItineraryItemRequest request, Long authenticatedUserId);

    ItineraryResponse deleteItem(Long tripId, Long itemId, Long authenticatedUserId);

    ItineraryResponse reorderItems(Long tripId, ItineraryReorderRequest request, Long authenticatedUserId);

    void deleteItinerary(Long tripId, Long authenticatedUserId);
}
