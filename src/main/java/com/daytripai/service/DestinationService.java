package com.daytripai.service;

import com.daytripai.dto.request.DestinationRequest;
import com.daytripai.dto.response.DestinationResponse;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.enums.DestinationCategory;

import java.util.List;

public interface DestinationService {

    PagedResponse<DestinationResponse> getAllDestinations(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String city,
            DestinationCategory category,
            String keyword,
            Double maxCost
    );

    DestinationResponse getDestinationById(Long id);

    List<DestinationResponse> getPopularDestinations(int limit);

    List<DestinationResponse> getDestinationsByCity(String city);

    List<String> getAvailableCities();

    List<DestinationCategory> getAvailableCategories();

    DestinationResponse createDestination(DestinationRequest request);

    DestinationResponse updateDestination(Long id, DestinationRequest request);

    void deleteDestination(Long id);
}
