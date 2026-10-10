package com.daytripai.integration.maps;

import com.daytripai.dto.response.RouteEstimateResponse;

/**
 * Maps & Routing Service Contract.
 * Computes road distance, transit times, navigation maneuvers, and cost estimates.
 */
public interface MapsRoutingService {

    Double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2);

    RouteEstimateResponse calculateRoute(double originLat, double originLon,
                                         double destLat, double destLon,
                                         String transportMode);
}
