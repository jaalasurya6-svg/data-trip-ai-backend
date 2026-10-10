package com.daytripai.service;

import com.daytripai.dto.request.ReviewRequest;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.dto.response.ReviewResponse;

public interface ReviewService {

    PagedResponse<ReviewResponse> getReviewsForDestination(Long destinationId, int page, int size);

    ReviewResponse createReview(Long destinationId, ReviewRequest request, Long authenticatedUserId);

    void deleteReview(Long reviewId, Long authenticatedUserId);
}
