package com.daytripai.controller;

import com.daytripai.dto.request.ReviewRequest;
import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.dto.response.ReviewResponse;
import com.daytripai.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Reviews & Ratings", description = "Endpoints for traveler ratings and reviews on destinations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/destinations/{destinationId}/reviews")
    @Operation(summary = "Get reviews for a destination with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getReviews(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<ReviewResponse> response = reviewService.getReviewsForDestination(destinationId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", response));
    }

    @PostMapping("/destinations/{destinationId}/reviews")
    @Operation(summary = "Submit a rating and review for a destination")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long destinationId,
            @Valid @RequestBody ReviewRequest request,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ReviewResponse response = reviewService.createReview(destinationId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review and recompute destination average rating")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
