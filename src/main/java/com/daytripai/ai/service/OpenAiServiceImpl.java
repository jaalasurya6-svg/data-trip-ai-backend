package com.daytripai.ai.service;

import com.daytripai.ai.AiItineraryService;
import com.daytripai.ai.dto.AiParsedItinerary;
import com.daytripai.ai.dto.AiParsedStop;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Trip;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OpenAI GPT-4o Mini implementation for itinerary generation.
 */
@Service("openAiService")
public class OpenAiServiceImpl implements AiItineraryService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiServiceImpl.class);

    @Value("${app.ai.openai.api-key:}")
    private String apiKey;

    @Value("${app.ai.openai.model:gpt-4o-mini}")
    private String model;

    @Value("${app.ai.openai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OpenAiServiceImpl(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isBlank() && !apiKey.equalsIgnoreCase("mock");
    }

    @Override
    public String getProviderName() {
        return "OPENAI";
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public AiParsedItinerary generate(Trip trip, List<Destination> candidates, String customInstructions, String transportMode) throws Exception {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI API Key is not configured (OPENAI_API_KEY environment variable is empty)");
        }

        log.info("Sending generative prompt to OpenAI ({}) for trip ID {}", model, trip.getId());
        String systemPrompt = "You are a senior travel itinerary architect. You output only valid JSON matching the requested structure.";
        String userPrompt = buildPrompt(trip, candidates, customInstructions, transportMode);

        Map<String, Object> requestPayload = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.5
        );

        String rawResponse = restClient.post()
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestPayload)
                .retrieve()
                .body(String.class);

        return parseOpenAiResponse(rawResponse);
    }

    private String buildPrompt(Trip trip, List<Destination> candidates, String customInstructions, String transportMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Create a one-day trip for %s (India).\n", trip.getDestinationCity()));
        sb.append(String.format("Starting at: %s, Date: %s, Available: %d hrs, Group: %d, Budget: INR %s.\n",
                trip.getStartingLocation(), trip.getTripDate(), trip.getAvailableHours(), trip.getGroupSize(), trip.getBudget()));
        sb.append(String.format("Interests: %s, Transport: %s.\n", trip.getTravelPreference(), transportMode));

        if (customInstructions != null && !customInstructions.isBlank()) {
            sb.append("Traveler notes: ").append(customInstructions).append("\n");
        }

        sb.append("\nCandidate Attractions:\n");
        for (Destination d : candidates) {
            sb.append(String.format("- ID %d: %s (%s, Rating: %.1f, Visit: %.1fh, Entry: INR %.0f)\n",
                    d.getId(), d.getName(), d.getCategory(), d.getRating(), d.getAverageVisitDurationHours(), d.getEstimatedCost()));
        }

        sb.append("\nReturn JSON schema:\n");
        sb.append("{\n");
        sb.append("  \"summary\": \"Trip summary sentence\",\n");
        sb.append("  \"narrativeAdvice\": \"Tips on pacing and cuisine\",\n");
        sb.append("  \"stops\": [\n");
        sb.append("    {\n");
        sb.append("      \"destinationId\": 1,\n");
        sb.append("      \"placeName\": \"Attraction\",\n");
        sb.append("      \"activityDescription\": \"Description\",\n");
        sb.append("      \"startTime\": \"09:30\",\n");
        sb.append("      \"endTime\": \"11:00\",\n");
        sb.append("      \"estimatedCost\": 100.0,\n");
        sb.append("      \"transportModeToNext\": \"CAB\",\n");
        sb.append("      \"travelMinutesToNext\": 15,\n");
        sb.append("      \"distanceKmToNext\": 3.5\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private AiParsedItinerary parseOpenAiResponse(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode choices = root.path("choices");
        if (choices.isEmpty()) {
            throw new RuntimeException("OpenAI returned no choices: " + rawResponse);
        }

        String jsonContent = choices.get(0).path("message").path("content").asText();
        JsonNode itineraryJson = objectMapper.readTree(jsonContent);

        String summary = itineraryJson.path("summary").asText("One-Day Itinerary");
        String narrative = itineraryJson.path("narrativeAdvice").asText("Enjoy your day!");

        List<AiParsedStop> stops = new ArrayList<>();
        JsonNode stopsNode = itineraryJson.path("stops");
        if (stopsNode.isArray()) {
            for (JsonNode s : stopsNode) {
                Long destId = s.has("destinationId") && !s.path("destinationId").isNull() ? s.path("destinationId").asLong() : null;
                String placeName = s.path("placeName").asText("Stop");
                String activity = s.path("activityDescription").asText("");
                LocalTime start = parseTime(s.path("startTime").asText("09:30"));
                LocalTime end = parseTime(s.path("endTime").asText("11:00"));
                double cost = s.path("estimatedCost").asDouble(0.0);
                String transport = s.path("transportModeToNext").asText("CAB");
                int travelMins = s.path("travelMinutesToNext").asInt(15);
                double distanceKm = s.path("distanceKmToNext").asDouble(3.0);
                stops.add(new AiParsedStop(destId, placeName, activity, start, end, cost, transport, travelMins, distanceKm));
            }
        }

        AiParsedItinerary result = new AiParsedItinerary(summary, narrative, stops);
        result.setPromptTokens(root.path("usage").path("prompt_tokens").asInt(0));
        result.setCompletionTokens(root.path("usage").path("completion_tokens").asInt(0));
        return result;
    }

    private LocalTime parseTime(String timeStr) {
        try {
            if (timeStr.length() > 5) timeStr = timeStr.substring(0, 5);
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            return LocalTime.of(9, 30);
        }
    }
}
