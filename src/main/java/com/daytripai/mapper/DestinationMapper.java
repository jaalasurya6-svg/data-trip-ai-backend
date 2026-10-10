package com.daytripai.mapper;

import com.daytripai.dto.request.DestinationRequest;
import com.daytripai.dto.response.DestinationResponse;
import com.daytripai.entity.Destination;
import org.springframework.stereotype.Component;

/**
 * Mapper component for Destination entity and DTO transformations.
 */
@Component
public class DestinationMapper {

    public DestinationResponse toResponse(Destination destination) {
        if (destination == null) {
            return null;
        }

        DestinationResponse response = new DestinationResponse();
        response.setId(destination.getId());
        response.setName(destination.getName());
        response.setDescription(destination.getDescription());
        response.setCity(destination.getCity());
        response.setState(destination.getState());
        response.setCountry(destination.getCountry());
        response.setCategory(destination.getCategory());
        response.setEstimatedCost(destination.getEstimatedCost());
        response.setOpeningTime(destination.getOpeningTime());
        response.setClosingTime(destination.getClosingTime());
        response.setAverageVisitDurationHours(destination.getAverageVisitDurationHours());
        response.setLatitude(destination.getLatitude());
        response.setLongitude(destination.getLongitude());
        response.setRating(destination.getRating());
        response.setReviewCount(destination.getReviewCount());
        response.setImageUrl(destination.getImageUrl());
        response.setAddress(destination.getAddress());
        response.setPopular(destination.isPopular());
        response.setCreatedAt(destination.getCreatedAt());
        response.setUpdatedAt(destination.getUpdatedAt());

        return response;
    }

    public Destination toEntity(DestinationRequest request) {
        if (request == null) {
            return null;
        }

        Destination destination = new Destination();
        destination.setName(request.getName());
        destination.setDescription(request.getDescription());
        destination.setCity(request.getCity());
        destination.setState(request.getState());
        destination.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        destination.setCategory(request.getCategory());
        destination.setEstimatedCost(request.getEstimatedCost());
        destination.setOpeningTime(request.getOpeningTime());
        destination.setClosingTime(request.getClosingTime());
        destination.setAverageVisitDurationHours(
                request.getAverageVisitDurationHours() != null ? request.getAverageVisitDurationHours() : 1.5
        );
        destination.setLatitude(request.getLatitude());
        destination.setLongitude(request.getLongitude());
        destination.setImageUrl(request.getImageUrl());
        destination.setAddress(request.getAddress());
        destination.setPopular(request.isPopular());
        destination.setRating(4.5);
        destination.setReviewCount(0);

        return destination;
    }

    public void updateEntityFromRequest(Destination destination, DestinationRequest request) {
        if (destination == null || request == null) {
            return;
        }

        destination.setName(request.getName());
        destination.setDescription(request.getDescription());
        destination.setCity(request.getCity());
        destination.setState(request.getState());
        if (request.getCountry() != null) {
            destination.setCountry(request.getCountry());
        }
        destination.setCategory(request.getCategory());
        destination.setEstimatedCost(request.getEstimatedCost());
        destination.setOpeningTime(request.getOpeningTime());
        destination.setClosingTime(request.getClosingTime());
        if (request.getAverageVisitDurationHours() != null) {
            destination.setAverageVisitDurationHours(request.getAverageVisitDurationHours());
        }
        destination.setLatitude(request.getLatitude());
        destination.setLongitude(request.getLongitude());
        destination.setImageUrl(request.getImageUrl());
        destination.setAddress(request.getAddress());
        destination.setPopular(request.isPopular());
    }
}
