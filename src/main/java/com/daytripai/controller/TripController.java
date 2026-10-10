package com.daytripai.controller;

import com.daytripai.dto.request.TripRequest;
import com.daytripai.dto.request.TripStatusUpdateRequest;
import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.dto.response.TripResponse;
import com.daytripai.dto.response.TripSummaryResponse;
import com.daytripai.enums.TripStatus;
import com.daytripai.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Trip Planning REST Controller.
 * Manages one-day trips: creation, retrieval, updates, status transitions, and budget summaries.
 */
@RestController
@RequestMapping("/api/v1/trips")
@Tag(name = "Trip Planner", description = "One-day trip creation, customization, and budget management")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @Operation(summary = "Create a New Day Trip", description = "Registers a new trip plan with budget, date, and time allocation")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @Valid @RequestBody TripRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        TripResponse created = tripService.createTrip(request, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Trip created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get Trips for Current User", description = "Returns a paginated list of planned or completed trips")
    public ResponseEntity<ApiResponse<PagedResponse<TripResponse>>> getTrips(
            @RequestParam(required = false) TripStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        PagedResponse<TripResponse> response = tripService.getTrips(
                userId, status, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success("Trips retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Trip by ID", description = "Returns detailed trip itinerary and budget parameters")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        TripResponse response = tripService.getTripById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Trip Details", description = "Updates budget, destination, time, or preferences for an existing trip")
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        TripResponse updated = tripService.updateTrip(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Trip updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update Trip Status", description = "Transitions trip status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)")
    public ResponseEntity<ApiResponse<TripResponse>> updateTripStatus(
            @PathVariable Long id,
            @Valid @RequestBody TripStatusUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        TripResponse updated = tripService.updateTripStatus(id, request.getStatus(), userId);
        return ResponseEntity.ok(ApiResponse.success("Trip status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Trip", description = "Deletes a trip along with its associated itinerary stops and expenses")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        tripService.deleteTrip(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully", null));
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get Trip Financial Summary", description = "Calculates budget utilization, total spent, and remaining budget")
    public ResponseEntity<ApiResponse<TripSummaryResponse>> getTripSummary(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        TripSummaryResponse summary = tripService.getTripSummary(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Trip summary calculated successfully", summary));
    }
}
