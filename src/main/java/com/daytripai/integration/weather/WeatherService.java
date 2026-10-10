package com.daytripai.integration.weather;

import com.daytripai.dto.response.WeatherForecastResponse;

/**
 * Weather Service Contract.
 * Provides meteorological forecasts and travel weather advice for destinations.
 */
public interface WeatherService {

    WeatherForecastResponse getWeatherForecast(Double latitude, Double longitude, String cityName);

    WeatherForecastResponse getWeatherForDestination(Long destinationId);
}
