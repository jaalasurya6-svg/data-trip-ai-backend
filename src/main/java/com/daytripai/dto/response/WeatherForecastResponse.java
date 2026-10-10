package com.daytripai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * WeatherForecastResponse DTO.
 * Summarizes temperature, atmospheric condition, precipitation risk,
 * and travel recommendations for a trip destination.
 */
@Schema(description = "Weather forecast and travel suitability recommendation")
public class WeatherForecastResponse {

    @Schema(description = "Destination city or location name", example = "Mumbai")
    private String city;

    @Schema(description = "Latitude", example = "18.9220")
    private Double latitude;

    @Schema(description = "Longitude", example = "72.8347")
    private Double longitude;

    @Schema(description = "Current ambient temperature in Celsius", example = "28.5")
    private Double currentTemperature;

    @Schema(description = "Daily maximum temperature in Celsius", example = "32.0")
    private Double maxTemperature;

    @Schema(description = "Daily minimum temperature in Celsius", example = "25.0")
    private Double minTemperature;

    @Schema(description = "Weather condition description", example = "Partly Cloudy")
    private String condition;

    @Schema(description = "WMO Weather Interpretation Code", example = "2")
    private Integer weatherCode;

    @Schema(description = "Precipitation probability percentage (0-100%)", example = "15")
    private Integer precipitationProbability;

    @Schema(description = "Relative humidity percentage", example = "65")
    private Integer humidity;

    @Schema(description = "Wind speed in km/h", example = "14.2")
    private Double windSpeed;

    @Schema(description = "AI travel and packing recommendation based on weather conditions",
            example = "Great weather for outdoor exploration! Light cotton clothing and sun protection recommended.")
    private String recommendation;

    @Schema(description = "Provider powering this forecast", example = "Open-Meteo API (Live)")
    private String provider;

    public WeatherForecastResponse() {
    }

    public WeatherForecastResponse(String city, Double latitude, Double longitude, Double currentTemperature,
                                   Double maxTemperature, Double minTemperature, String condition,
                                   Integer weatherCode, Integer precipitationProbability, Integer humidity,
                                   Double windSpeed, String recommendation, String provider) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentTemperature = currentTemperature;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.condition = condition;
        this.weatherCode = weatherCode;
        this.precipitationProbability = precipitationProbability;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.recommendation = recommendation;
        this.provider = provider;
    }

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(Integer weatherCode) {
        this.weatherCode = weatherCode;
    }

    public Integer getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(Integer precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
