package com.daytripai.ai.service;

import com.daytripai.ai.AiItineraryService;
import com.daytripai.ai.dto.AiParsedItinerary;
import com.daytripai.ai.dto.AiParsedStop;
import com.daytripai.dto.request.AiItineraryGenerateRequest;
import com.daytripai.dto.response.AiGenerationMetadata;
import com.daytripai.dto.response.AiItineraryResponse;
import com.daytripai.dto.response.ItineraryResponse;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Itinerary;
import com.daytripai.entity.ItineraryItem;
import com.daytripai.entity.Trip;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.ItineraryMapper;
import com.daytripai.repository.DestinationRepository;
import com.daytripai.repository.ItineraryRepository;
import com.daytripai.repository.TripRepository;
import com.daytripai.service.ItineraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Intelligent Orchestrator coordinating AI providers with guaranteed deterministic fallback.
 */
@Service
@Transactional
public class AiItineraryOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiItineraryOrchestrator.class);

    @Value("${app.ai.provider:gemini}")
    private String defaultProvider;

    private final GeminiAiServiceImpl geminiService;
    private final OpenAiServiceImpl openAiService;
    private final LocalFallbackAiServiceImpl fallbackService;

    private final TripRepository tripRepository;
    private final DestinationRepository destinationRepository;
    private final ItineraryRepository itineraryRepository;
    private final ItineraryMapper itineraryMapper;
    private final ItineraryService itineraryService;

    public AiItineraryOrchestrator(GeminiAiServiceImpl geminiService,
                                   OpenAiServiceImpl openAiService,
                                   LocalFallbackAiServiceImpl fallbackService,
                                   TripRepository tripRepository,
                                   DestinationRepository destinationRepository,
                                   ItineraryRepository itineraryRepository,
                                   ItineraryMapper itineraryMapper,
                                   ItineraryService itineraryService) {
        this.geminiService = geminiService;
        this.openAiService = openAiService;
        this.fallbackService = fallbackService;
        this.tripRepository = tripRepository;
        this.destinationRepository = destinationRepository;
        this.itineraryRepository = itineraryRepository;
        this.itineraryMapper = itineraryMapper;
        this.itineraryService = itineraryService;
    }

    public AiItineraryResponse generateItinerary(Long tripId, AiItineraryGenerateRequest request, Long authenticatedUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        if (request == null) {
            request = new AiItineraryGenerateRequest();
        }

        List<Destination> candidates = destinationRepository.findByCityIgnoreCase(trip.getDestinationCity());
        if (candidates.isEmpty()) {
            candidates = destinationRepository.findByIsPopularTrue();
        }

        Map<Long, Destination> candidateMap = candidates.stream()
                .collect(Collectors.toMap(Destination::getId, Function.identity(), (d1, d2) -> d1));

        String targetProvider = request.getProvider() != null && !request.getProvider().isBlank()
                ? request.getProvider().toLowerCase()
                : defaultProvider.toLowerCase();

        AiParsedItinerary parsedItinerary = null;
        boolean isFallback = false;
        String fallbackReason = null;
        String providerUsed = "LOCAL_FALLBACK";
        String modelUsed = fallbackService.getModelName();
        long startMs = System.currentTimeMillis();

        // 1. Try Target AI Service
        try {
            if ("gemini".equals(targetProvider)) {
                if (geminiService.isAvailable()) {
                    parsedItinerary = geminiService.generate(trip, candidates, request.getCustomInstructions(), request.getPreferredTransportMode());
                    providerUsed = "GEMINI";
                    modelUsed = geminiService.getModelName();
                } else {
                    isFallback = true;
                    fallbackReason = "Google Gemini API key not configured (GEMINI_API_KEY environment variable is missing)";
                }
            } else if ("openai".equals(targetProvider)) {
                if (openAiService.isAvailable()) {
                    parsedItinerary = openAiService.generate(trip, candidates, request.getCustomInstructions(), request.getPreferredTransportMode());
                    providerUsed = "OPENAI";
                    modelUsed = openAiService.getModelName();
                } else {
                    isFallback = true;
                    fallbackReason = "OpenAI API key not configured (OPENAI_API_KEY environment variable is missing)";
                }
            } else {
                isFallback = true;
                fallbackReason = "Deterministic rule engine requested directly";
            }
        } catch (Exception ex) {
            log.warn("External AI call failed with error: {}. Executing seamless local fallback.", ex.getMessage());
            isFallback = true;
            fallbackReason = "AI service error: " + ex.getMessage();
        }

        // 2. Seamless Fallback Execution
        if (parsedItinerary == null) {
            parsedItinerary = fallbackService.generate(trip, candidates, request.getCustomInstructions(), request.getPreferredTransportMode());
            providerUsed = "LOCAL_FALLBACK";
            modelUsed = fallbackService.getModelName();
        }

        long latencyMs = System.currentTimeMillis() - startMs;

        // 3. Persist and Associate Itinerary
        Itinerary itinerary = itineraryRepository.findByTripId(tripId).orElseGet(() -> {
            Itinerary newItin = new Itinerary();
            newItin.setTrip(trip);
            return newItin;
        });

        itinerary.getItems().clear();
        itinerary.setSummary(parsedItinerary.getSummary());

        double totalDistanceKm = 0.0;
        int totalDurationMins = 0;
        int stopOrder = 1;

        for (AiParsedStop stop : parsedItinerary.getStops()) {
            Destination dest = stop.getDestinationId() != null ? candidateMap.get(stop.getDestinationId()) : null;

            ItineraryItem item = new ItineraryItem(
                    itinerary,
                    dest,
                    stopOrder++,
                    stop.getPlaceName(),
                    stop.getActivityDescription(),
                    stop.getStartTime(),
                    stop.getEndTime(),
                    stop.getEstimatedCost(),
                    stop.getTransportModeToNext(),
                    stop.getTravelMinutesToNext(),
                    stop.getDistanceKmToNext()
            );

            itinerary.addItem(item);
            totalDistanceKm += stop.getDistanceKmToNext();
            totalDurationMins += (stop.getTravelMinutesToNext() != null ? stop.getTravelMinutesToNext() : 0);
        }

        itinerary.setTotalDistanceKm(Math.round(totalDistanceKm * 10.0) / 10.0);
        itinerary.setTotalDurationMinutes(Math.max(totalDurationMins, trip.getAvailableHours() * 60));

        Itinerary savedItinerary = itineraryRepository.save(itinerary);

        // Calculate and sync estimated costs
        double totalItemCost = savedItinerary.getItems().stream()
                .mapToDouble(i -> i.getEstimatedCost() != null ? i.getEstimatedCost() : 0.0)
                .sum();
        trip.setEstimatedTotalCost(Math.round(totalItemCost * 100.0) / 100.0);
        trip.setRemainingBudget(Math.round((trip.getBudget() - totalItemCost) * 100.0) / 100.0);
        trip.setItinerary(savedItinerary);
        tripRepository.save(trip);

        // 4. Build Response DTO
        ItineraryResponse itineraryResponse = itineraryService.getItineraryByTripId(tripId, authenticatedUserId);

        AiGenerationMetadata metadata = new AiGenerationMetadata(
                providerUsed,
                modelUsed,
                isFallback,
                fallbackReason,
                latencyMs,
                parsedItinerary.getPromptTokens(),
                parsedItinerary.getCompletionTokens()
        );

        log.info("AI Itinerary generated successfully for Trip ID {} (Provider: {}, Fallback: {}, Latency: {}ms)",
                tripId, providerUsed, isFallback, latencyMs);

        return new AiItineraryResponse(itineraryResponse, parsedItinerary.getNarrativeAdvice(), metadata);
    }

    public Map<String, Object> getAiStatus() {
        return Map.of(
                "defaultProvider", defaultProvider,
                "providers", Map.of(
                        "gemini", Map.of(
                                "configured", geminiService.isAvailable(),
                                "model", geminiService.getModelName()
                        ),
                        "openai", Map.of(
                                "configured", openAiService.isAvailable(),
                                "model", openAiService.getModelName()
                        ),
                        "localFallback", Map.of(
                                "configured", true,
                                "model", fallbackService.getModelName()
                        )
                )
        );
    }
}
