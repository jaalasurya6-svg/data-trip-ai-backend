package com.daytripai.controller;

import com.daytripai.dto.request.ItineraryItemRequest;
import com.daytripai.dto.request.ItineraryReorderRequest;
import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.ItineraryResponse;
import com.daytripai.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Itinerary Management REST Controller.
 * Handles AI/heuristic itinerary generation, ordered timeline retrieval,
 * stop customization, and drag-and-drop reordering.
 */
@RestController
@RequestMapping("/api/v1/trips/{tripId}/itinerary")
@Tag(name = "Itinerary Engine", description = "One-day itinerary generation, timeline scheduling, and stop customization")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate Smart Itinerary", description = "Generates a budget-aware, time-optimized sequence of stops for a Trip")
    public ResponseEntity<ApiResponse<ItineraryResponse>> generateItinerary(
            @PathVariable Long tripId,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ItineraryResponse response = itineraryService.generateItinerary(tripId, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Itinerary generated successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get Trip Itinerary", description = "Retrieves the ordered timeline of stops and budget breakdown for a Trip")
    public ResponseEntity<ApiResponse<ItineraryResponse>> getItinerary(
            @PathVariable Long tripId,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ItineraryResponse response = itineraryService.getItineraryByTripId(tripId, userId);
        return ResponseEntity.ok(ApiResponse.success("Itinerary retrieved successfully", response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add Custom Stop", description = "Appends a new attraction or dining stop to the itinerary")
    public ResponseEntity<ApiResponse<ItineraryResponse>> addItem(
            @PathVariable Long tripId,
            @Valid @RequestBody ItineraryItemRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ItineraryResponse response = itineraryService.addItem(tripId, request, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stop added to itinerary successfully", response));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update Itinerary Stop", description = "Edits timings, activity description, or cost for a stop")
    public ResponseEntity<ApiResponse<ItineraryResponse>> updateItem(
            @PathVariable Long tripId,
            @PathVariable Long itemId,
            @Valid @RequestBody ItineraryItemRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ItineraryResponse response = itineraryService.updateItem(tripId, itemId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Itinerary stop updated successfully", response));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete Itinerary Stop", description = "Removes a stop and re-indexes remaining stops sequentially")
    public ResponseEntity<ApiResponse<ItineraryResponse>> deleteItem(
            @PathVariable Long tripId,
            @PathVariable Long itemId,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ItineraryResponse response = itineraryService.deleteItem(tripId, itemId, userId);
        return ResponseEntity.ok(ApiResponse.success("Stop removed from itinerary", response));
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reorder Stops", description = "Reorders stops according to the ordered list of stop IDs")
    public ResponseEntity<ApiResponse<ItineraryResponse>> reorderItems(
            @PathVariable Long tripId,
            @Valid @RequestBody ItineraryReorderRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ItineraryResponse response = itineraryService.reorderItems(tripId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Stops reordered successfully", response));
    }

    @DeleteMapping
    @Operation(summary = "Reset Itinerary", description = "Clears the itinerary for the specified trip")
    public ResponseEntity<ApiResponse<Void>> deleteItinerary(
            @PathVariable Long tripId,
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        itineraryService.deleteItinerary(tripId, userId);
        return ResponseEntity.ok(ApiResponse.success("Itinerary cleared successfully", null));
    }
}
