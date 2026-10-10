package com.daytripai.controller;

import com.daytripai.dto.response.ApiResponse;
import com.daytripai.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Database Status REST Controller.
 * Verifies relational schema creation, entity mappings, and database seed statistics.
 */
@RestController
@RequestMapping("/api/v1/health/db-status")
@Tag(name = "Health Check", description = "System readiness and heartbeat endpoints")
public class DatabaseStatusController {

    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final ItineraryItemRepository itineraryItemRepository;
    private final ExpenseRepository expenseRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    public DatabaseStatusController(UserRepository userRepository,
                                    DestinationRepository destinationRepository,
                                    TripRepository tripRepository,
                                    ItineraryRepository itineraryRepository,
                                    ItineraryItemRepository itineraryItemRepository,
                                    ExpenseRepository expenseRepository,
                                    FavoriteRepository favoriteRepository,
                                    ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
        this.itineraryItemRepository = itineraryItemRepository;
        this.expenseRepository = expenseRepository;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    @Operation(summary = "Check Database Entity Statistics", description = "Returns MySQL table counts, distinct destination cities, and query latency")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseStatus() {
        long startTime = System.currentTimeMillis();

        Map<String, Object> entityCounts = new LinkedHashMap<>();
        entityCounts.put("users", userRepository.count());
        entityCounts.put("destinations", destinationRepository.count());
        entityCounts.put("trips", tripRepository.count());
        entityCounts.put("itineraries", itineraryRepository.count());
        entityCounts.put("itineraryItems", itineraryItemRepository.count());
        entityCounts.put("expenses", expenseRepository.count());
        entityCounts.put("favorites", favoriteRepository.count());
        entityCounts.put("reviews", reviewRepository.count());

        long queryLatency = System.currentTimeMillis() - startTime;

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("databaseConnection", "HEALTHY");
        status.put("databaseEngine", "MySQL 8.0");
        status.put("queryLatencyMs", queryLatency);
        status.put("availableCities", destinationRepository.findDistinctCities());
        status.put("entityCounts", entityCounts);

        return ResponseEntity.ok(ApiResponse.success("MySQL database entities verified successfully", status));
    }
}
