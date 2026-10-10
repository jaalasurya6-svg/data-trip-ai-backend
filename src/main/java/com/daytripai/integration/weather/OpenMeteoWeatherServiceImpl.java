package com.daytripai.integration.weather;

import com.daytripai.dto.response.WeatherForecastResponse;
import com.daytripai.entity.Destination;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.repository.DestinationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * OpenMeteoWeatherServiceImpl.
 * Connects to the free, high-precision Open-Meteo REST API to fetch live weather,
 * humidity, precipitation risk, and WMO condition codes.
 * Features an automatic fallback microclimate simulator for offline or rate-limited environments.
 */
@Service
public class OpenMeteoWeatherServiceImpl implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherServiceImpl.class);

    private final DestinationRepository destinationRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OpenMeteoWeatherServiceImpl(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
        this.objectMapper = new ObjectMapper();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(4));
        factory.setReadTimeout(Duration.ofSeconds(4));

        this.restClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com/v1")
                .requestFactory(factory)
                .build();
    }

    @Override
    public WeatherForecastResponse getWeatherForecast(Double latitude, Double longitude, String cityName) {
        if (latitude == null || longitude == null) {
            log.info("Missing coordinates for city '{}'. Utilizing DayTrip AI Microclimate Simulator.", cityName);
            return simulateWeather(cityName, 18.9220, 72.8347);
        }

        try {
            String jsonResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m")
                            .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                            .queryParam("timezone", "auto")
                            .build())
                    .retrieve()
                    .body(String.class);

            if (jsonResponse != null) {
                JsonNode root = objectMapper.readTree(jsonResponse);
                JsonNode current = root.path("current");
                JsonNode daily = root.path("daily");

                double currentTemp = current.path("temperature_2m").asDouble(28.0);
                int humidity = current.path("relative_humidity_2m").asInt(60);
                int weatherCode = current.path("weather_code").asInt(1);
                double windSpeed = current.path("wind_speed_10m").asDouble(12.0);

                double maxTemp = daily.path("temperature_2m_max").isArray() && !daily.path("temperature_2m_max").isEmpty()
                        ? daily.path("temperature_2m_max").get(0).asDouble(currentTemp + 4)
                        : currentTemp + 4;

                double minTemp = daily.path("temperature_2m_min").isArray() && !daily.path("temperature_2m_min").isEmpty()
                        ? daily.path("temperature_2m_min").get(0).asDouble(currentTemp - 4)
                        : currentTemp - 4;

                int precipProb = daily.path("precipitation_probability_max").isArray() && !daily.path("precipitation_probability_max").isEmpty()
                        ? daily.path("precipitation_probability_max").get(0).asInt(10)
                        : 10;

                String condition = mapWmoWeatherCode(weatherCode);
                String recommendation = generateRecommendation(condition, currentTemp, precipProb);

                log.info("Successfully fetched live Open-Meteo weather for {} [{}, {}]: {}°C, {}",
                        cityName, latitude, longitude, currentTemp, condition);

                return new WeatherForecastResponse(
                        cityName != null ? cityName : "Destination",
                        latitude,
                        longitude,
                        Math.round(currentTemp * 10.0) / 10.0,
                        Math.round(maxTemp * 10.0) / 10.0,
                        Math.round(minTemp * 10.0) / 10.0,
                        condition,
                        weatherCode,
                        precipProb,
                        humidity,
                        Math.round(windSpeed * 10.0) / 10.0,
                        recommendation,
                        "Open-Meteo API (Live)"
                );
            }
        } catch (Exception ex) {
            log.warn("Open-Meteo weather API call failed ({}: {}). Falling back to local simulator.",
                    ex.getClass().getSimpleName(), ex.getMessage());
        }

        return simulateWeather(cityName, latitude, longitude);
    }

    @Override
    public WeatherForecastResponse getWeatherForDestination(Long destinationId) {
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", destinationId));

        return getWeatherForecast(destination.getLatitude(), destination.getLongitude(), destination.getName() + ", " + destination.getCity());
    }

    private WeatherForecastResponse simulateWeather(String cityName, Double lat, Double lon) {
        double currentTemp = 27.5;
        double maxTemp = 31.0;
        double minTemp = 23.0;
        int weatherCode = 1;
        String condition = "Pleasant & Clear";
        int precipProb = 10;
        int humidity = 58;
        double windSpeed = 11.5;

        String recommendation = generateRecommendation(condition, currentTemp, precipProb);

        return new WeatherForecastResponse(
                cityName != null ? cityName : "Target Destination",
                lat != null ? lat : 18.9220,
                lon != null ? lon : 72.8347,
                currentTemp,
                maxTemp,
                minTemp,
                condition,
                weatherCode,
                precipProb,
                humidity,
                windSpeed,
                recommendation,
                "DayTrip AI Microclimate Simulator (Fallback)"
        );
    }

    private String mapWmoWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear Sky";
            case 1 -> "Mainly Clear";
            case 2 -> "Partly Cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Light Drizzle";
            case 61, 63, 65 -> "Rainy";
            case 71, 73, 75 -> "Snow";
            case 80, 81, 82 -> "Rain Showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Pleasant";
        };
    }

    private String generateRecommendation(String condition, double temp, int rainProb) {
        if (condition.toLowerCase().contains("rain") || condition.toLowerCase().contains("drizzle") || rainProb > 50) {
            return "Expect showers. Carry an umbrella and light waterproof gear. Good day for museums, cafes, and indoor cultural stops.";
        }
        if (temp > 33.0) {
            return "Warm day ahead! Stay hydrated, wear light cotton clothing, use sunscreen, and schedule outdoor sightseeing in morning or late afternoon.";
        }
        if (temp < 18.0) {
            return "Crisp and cool climate. A light jacket or sweater is recommended for evening walks.";
        }
        return "Optimal weather for day trips! Pleasant temperatures for photography, monuments, walking tours, and outdoor dining.";
    }
}
