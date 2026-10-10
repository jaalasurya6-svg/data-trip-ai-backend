package com.daytripai.service;

import com.daytripai.dto.request.TripRequest;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.dto.response.TripResponse;
import com.daytripai.dto.response.TripSummaryResponse;
import com.daytripai.enums.TripStatus;

public interface TripService {

    TripResponse createTrip(TripRequest request, Long authenticatedUserId);

    PagedResponse<TripResponse> getTrips(
            Long authenticatedUserId,
            TripStatus status,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    TripResponse getTripById(Long id, Long authenticatedUserId);

    TripResponse updateTrip(Long id, TripRequest request, Long authenticatedUserId);

    TripResponse updateTripStatus(Long id, TripStatus status, Long authenticatedUserId);

    void deleteTrip(Long id, Long authenticatedUserId);

    TripSummaryResponse getTripSummary(Long id, Long authenticatedUserId);
}
