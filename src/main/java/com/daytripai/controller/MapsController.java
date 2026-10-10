package com.daytripai.controller;

import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.RouteEstimateResponse;
import com.daytripai.integration.maps.MapsRoutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Maps & Routing REST Controller.
 * Provides geospatial distance calculation, transit duration estimation,
 * step-by-step maneuvers, and transport fare estimations.
 */
@RestController
@RequestMapping("/api/v1/maps")
@Tag(name = "Maps & Routing", description = "Geospatial distance and multi-modal transit calculations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MapsController {

    private final MapsRoutingService mapsRoutingService;

    public MapsController(MapsRoutingService mapsRoutingService) {
        this.mapsRoutingService = mapsRoutingService;
    }

    @GetMapping("/route")
    @Operation(summary = "Calculate route, distance, travel duration, and navigation steps",
            description = "Calculates route metrics between origin and destination coordinates for driving, transit, or walking")
    public ResponseEntity<ApiResponse<RouteEstimateResponse>> getRoute(
            @RequestParam double fromLat,
            @RequestParam double fromLon,
            @RequestParam double toLat,
            @RequestParam double toLon,
            @RequestParam(defaultValue = "DRIVING") String mode) {

        RouteEstimateResponse route = mapsRoutingService.calculateRoute(fromLat, fromLon, toLat, toLon, mode);
        return ResponseEntity.ok(ApiResponse.success("Route computed successfully", route));
    }

    @GetMapping("/distance")
    @Operation(summary = "Calculate road network distance between two coordinate pairs",
            description = "Uses Haversine formula adjusted for urban road network winding factors")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDistance(
            @RequestParam double lat1,
            @RequestParam double lon1,
            @RequestParam double lat2,
            @RequestParam double lon2) {

        Double distanceKm = mapsRoutingService.calculateDistanceKm(lat1, lon1, lat2, lon2);
        return ResponseEntity.ok(ApiResponse.success("Distance computed successfully",
                Map.of("distanceKm", distanceKm, "unit", "kilometers")));
    }
}
