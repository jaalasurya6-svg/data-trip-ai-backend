package com.daytripai.controller;

import com.daytripai.dto.request.DestinationRequest;
import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.DestinationResponse;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.enums.DestinationCategory;
import com.daytripai.service.DestinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Destination Discovery REST Controller.
 * Exposes endpoints for searching, browsing, filtering, and managing tourist attractions.
 */
@RestController
@RequestMapping("/api/v1/destinations")
@Tag(name = "Destinations", description = "Tourist destination discovery, search, and management")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping
    @Operation(summary = "Search & Filter Destinations", description = "Returns a paginated list of destinations matching optional filters like keyword, city, category, and max cost")
    public ResponseEntity<ApiResponse<PagedResponse<DestinationResponse>>> getAllDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) DestinationCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double maxCost) {

        PagedResponse<DestinationResponse> response = destinationService.getAllDestinations(
                page, size, sortBy, sortDir, city, category, keyword, maxCost
        );
        return ResponseEntity.ok(ApiResponse.success("Destinations retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Destination by ID", description = "Returns full details, opening hours, ticket fee, and coordinates of a destination")
    public ResponseEntity<ApiResponse<DestinationResponse>> getDestinationById(@PathVariable Long id) {
        DestinationResponse response = destinationService.getDestinationById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/popular")
    @Operation(summary = "Get Popular Destinations", description = "Returns top recommended destinations sorted by user rating")
    public ResponseEntity<ApiResponse<List<DestinationResponse>>> getPopularDestinations(
            @RequestParam(defaultValue = "6") int limit) {
        List<DestinationResponse> popular = destinationService.getPopularDestinations(limit);
        return ResponseEntity.ok(ApiResponse.success("Popular destinations retrieved successfully", popular));
    }

    @GetMapping("/cities")
    @Operation(summary = "Get Available Destination Cities", description = "Returns all distinct destination cities available in the database")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableCities() {
        List<String> cities = destinationService.getAvailableCities();
        return ResponseEntity.ok(ApiResponse.success("Available cities retrieved successfully", cities));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get Destination Categories", description = "Returns all tourist activity categories")
    public ResponseEntity<ApiResponse<List<DestinationCategory>>> getAvailableCategories() {
        List<DestinationCategory> categories = destinationService.getAvailableCategories();
        return ResponseEntity.ok(ApiResponse.success("Available categories retrieved successfully", categories));
    }

    @PostMapping
    @Operation(summary = "Create New Destination", description = "Registers a new destination with validated coordinates, entry cost, and category")
    public ResponseEntity<ApiResponse<DestinationResponse>> createDestination(
            @Valid @RequestBody DestinationRequest request) {
        DestinationResponse created = destinationService.createDestination(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Destination created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Destination", description = "Modifies existing destination attributes")
    public ResponseEntity<ApiResponse<DestinationResponse>> updateDestination(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request) {
        DestinationResponse updated = destinationService.updateDestination(id, request);
        return ResponseEntity.ok(ApiResponse.success("Destination updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Destination", description = "Deletes a destination by ID")
    public ResponseEntity<ApiResponse<Void>> deleteDestination(@PathVariable Long id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.ok(ApiResponse.success("Destination deleted successfully", null));
    }
}
