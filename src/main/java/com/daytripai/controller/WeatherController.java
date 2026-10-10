package com.daytripai.controller;

import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.WeatherForecastResponse;
import com.daytripai.integration.weather.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Weather Forecast REST Controller.
 * Provides live meteorological forecasts, precipitation risks, and AI travel tips for destinations.
 */
@RestController
@RequestMapping("/api/v1/weather")
@Tag(name = "Weather & Climate", description = "Live Open-Meteo weather forecasts and intelligent packing/travel advice")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    @Operation(summary = "Get weather forecast by geographic coordinates or city",
            description = "Returns current temp, high/low, rain likelihood, and tailored day-trip recommendation")
    public ResponseEntity<ApiResponse<WeatherForecastResponse>> getWeatherForecast(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "Destination") String city) {

        WeatherForecastResponse forecast = weatherService.getWeatherForecast(lat, lon, city);
        return ResponseEntity.ok(ApiResponse.success("Weather forecast retrieved successfully", forecast));
    }

    @GetMapping("/destinations/{destinationId}")
    @Operation(summary = "Get weather forecast for a specific destination ID",
            description = "Fetches destination coordinates from database and retrieves real-time weather")
    public ResponseEntity<ApiResponse<WeatherForecastResponse>> getWeatherForDestination(
            @PathVariable Long destinationId) {

        WeatherForecastResponse forecast = weatherService.getWeatherForDestination(destinationId);
        return ResponseEntity.ok(ApiResponse.success("Weather forecast retrieved successfully", forecast));
    }
}
