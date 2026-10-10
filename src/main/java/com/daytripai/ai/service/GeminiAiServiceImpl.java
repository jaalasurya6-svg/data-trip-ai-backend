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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini 1.5 AI implementation for intelligent, generative travel planning.
 */
@Service("geminiAiService")
public class GeminiAiServiceImpl implements AiItineraryService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiServiceImpl.class);

    @Value("${app.ai.gemini.api-key:}")
    private String apiKey;

    @Value("${app.ai.gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${app.ai.gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiAiServiceImpl(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isBlank() && !apiKey.equalsIgnoreCase("mock");
    }

    @Override
    public String getProviderName() {
        return "GEMINI";
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public AiParsedItinerary generate(Trip trip, List<Destination> candidates, String customInstructions, String transportMode) throws Exception {
        if (!isAvailable()) {
            throw new IllegalStateException("Google Gemini API Key is not configured (GEMINI_API_KEY environment variable is empty)");
        }

        log.info("Sending generative prompt to Google Gemini ({}) for trip ID {}", model, trip.getId());
        String prompt = buildPrompt(trip, candidates, customInstructions, transportMode);

        Map<String, Object> requestPayload = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json",
                        "temperature", 0.4
                )
        );

        String endpoint = String.format("%s/%s:generateContent?key=%s", apiUrl, model, apiKey);

        String rawResponse = restClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestPayload)
                .retrieve()
                .body(String.class);

        return parseGeminiResponse(rawResponse);
    }

    private String buildPrompt(Trip trip, List<Destination> candidates, String customInstructions, String transportMode) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert travel architect for India. Design a realistic one-day itinerary.\n");
        sb.append(String.format("City: %s, Starting Location: %s\n", trip.getDestinationCity(), trip.getStartingLocation()));
        sb.append(String.format("Date: %s, Available Hours: %d hours, Group Size: %d, Budget: INR %s\n",
                trip.getTripDate(), trip.getAvailableHours(), trip.getGroupSize(), trip.getBudget()));
        sb.append(String.format("Interests: %s, Preferred Transport: %s\n", trip.getTravelPreference(), transportMode));

        if (customInstructions != null && !customInstructions.isBlank()) {
            sb.append("Special Instructions: ").append(customInstructions).append("\n");
        }

        sb.append("\nAvailable Candidate Tourist Attractions in database:\n");
        for (Destination d : candidates) {
            sb.append(String.format("- ID %d: %s (%s, Rating: %.1f, Avg Visit: %.1fh, Entry: INR %.0f)\n",
                    d.getId(), d.getName(), d.getCategory(), d.getRating(), d.getAverageVisitDurationHours(), d.getEstimatedCost()));
        }

        sb.append("\nReturn STRICT JSON adhering to this schema:\n");
        sb.append("{\n");
        sb.append("  \"summary\": \"One-sentence high-level overview of the day\",\n");
        sb.append("  \"narrativeAdvice\": \"Personalized paragraph with tips on weather, crowd timings, and local food\",\n");
        sb.append("  \"stops\": [\n");
        sb.append("    {\n");
        sb.append("      \"destinationId\": 1,\n");
        sb.append("      \"placeName\": \"Attraction Name\",\n");
        sb.append("      \"activityDescription\": \"What to do and see\",\n");
        sb.append("      \"startTime\": \"09:30\",\n");
        sb.append("      \"endTime\": \"11:30\",\n");
        sb.append("      \"estimatedCost\": 100.0,\n");
        sb.append("      \"transportModeToNext\": \"CAB\",\n");
        sb.append("      \"travelMinutesToNext\": 20,\n");
        sb.append("      \"distanceKmToNext\": 4.5\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private AiParsedItinerary parseGeminiResponse(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode candidatesNode = root.path("candidates");
        if (candidatesNode.isEmpty()) {
            throw new RuntimeException("Gemini returned no candidates: " + rawResponse);
        }

        String jsonText = candidatesNode.get(0).path("content").path("parts").get(0).path("text").asText();
        // Clean markdown backticks if present
        jsonText = cleanMarkdown(jsonText);

        JsonNode itineraryJson = objectMapper.readTree(jsonText);

        String summary = itineraryJson.path("summary").asText("Customized One-Day Tour");
        String narrativeAdvice = itineraryJson.path("narrativeAdvice").asText("Enjoy your trip!");

        List<AiParsedStop> stops = new ArrayList<>();
        JsonNode stopsNode = itineraryJson.path("stops");
        if (stopsNode.isArray()) {
            for (JsonNode s : stopsNode) {
                Long destId = s.has("destinationId") && !s.path("destinationId").isNull() ? s.path("destinationId").asLong() : null;
                String placeName = s.path("placeName").asText("Local Stop");
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

        AiParsedItinerary result = new AiParsedItinerary(summary, narrativeAdvice, stops);
        result.setPromptTokens(root.path("usageMetadata").path("promptTokenCount").asInt(0));
        result.setCompletionTokens(root.path("usageMetadata").path("candidatesTokenCount").asInt(0));
        return result;
    }

    private String cleanMarkdown(String text) {
        text = text.trim();
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        return text.trim();
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
