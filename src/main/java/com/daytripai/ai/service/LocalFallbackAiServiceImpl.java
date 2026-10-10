package com.daytripai.ai.service;

import com.daytripai.ai.AiItineraryService;
import com.daytripai.ai.dto.AiParsedItinerary;
import com.daytripai.ai.dto.AiParsedStop;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic local rule engine that guarantees 100% availability even when
 * internet connectivity, external AI quotas, or API keys are unavailable.
 */
@Service("localFallbackAiService")
public class LocalFallbackAiServiceImpl implements AiItineraryService {

    private static final Logger log = LoggerFactory.getLogger(LocalFallbackAiServiceImpl.class);

    @Override
    public AiParsedItinerary generate(Trip trip, List<Destination> candidates, String customInstructions, String transportMode) {
        log.info("Executing Deterministic Fallback Engine for Trip ID {} in {}", trip.getId(), trip.getDestinationCity());

        String defaultTransport = (transportMode != null && !transportMode.isBlank()) ? transportMode : "CAB";
        int availableMinutes = trip.getAvailableHours() * 60;
        int consumedMinutes = 0;
        int groupSize = Math.max(trip.getGroupSize(), 1);

        LocalTime currentTime = LocalTime.of(9, 30);
        List<AiParsedStop> stops = new ArrayList<>();
        boolean lunchAdded = false;

        // Sort candidates by preference matching
        String preference = trip.getTravelPreference() != null ? trip.getTravelPreference().toLowerCase() : "";
        List<Destination> sorted = new ArrayList<>(candidates);
        sorted.sort((d1, d2) -> {
            boolean d1Matches = d1.getName().toLowerCase().contains(preference) ||
                    d1.getCategory().name().toLowerCase().contains(preference);
            boolean d2Matches = d2.getName().toLowerCase().contains(preference) ||
                    d2.getCategory().name().toLowerCase().contains(preference);
            if (d1Matches && !d2Matches) return -1;
            if (!d1Matches && d2Matches) return 1;
            return Double.compare(d2.getRating(), d1.getRating());
        });

        int destIndex = 0;
        while (consumedMinutes < availableMinutes - 60 && destIndex < sorted.size()) {
            // Check for lunch slot around 13:30
            if (!lunchAdded && currentTime.isAfter(LocalTime.of(13, 15))) {
                LocalTime lunchStart = currentTime;
                LocalTime lunchEnd = lunchStart.plusMinutes(75);
                double lunchCost = 300.0 * groupSize;

                stops.add(new AiParsedStop(
                        null,
                        String.format("Culinary Lunch: Authentic %s Specialties", trip.getDestinationCity()),
                        "Relax and savor regional culinary favorites and refreshments with the group.",
                        lunchStart, lunchEnd, lunchCost,
                        defaultTransport, 20, 4.0
                ));
                currentTime = lunchEnd.plusMinutes(20);
                consumedMinutes += (75 + 20);
                lunchAdded = true;
                continue;
            }

            Destination dest = sorted.get(destIndex++);
            int visitMinutes = (int) Math.round(dest.getAverageVisitDurationHours() * 60);
            if (visitMinutes <= 0) visitMinutes = 90;

            if (consumedMinutes + visitMinutes > availableMinutes) {
                break;
            }

            LocalTime arrival = currentTime;
            LocalTime departure = arrival.plusMinutes(visitMinutes);
            double stopCost = (dest.getEstimatedCost() != null ? dest.getEstimatedCost() : 0.0) * groupSize;
            int transitMinutes = 15;
            double transitDistanceKm = 3.5;

            stops.add(new AiParsedStop(
                    dest.getId(),
                    dest.getName(),
                    dest.getDescription(),
                    arrival, departure, stopCost,
                    defaultTransport, transitMinutes, transitDistanceKm
            ));

            currentTime = departure.plusMinutes(transitMinutes);
            consumedMinutes += (visitMinutes + transitMinutes);
        }

        String summary = String.format("Handcrafted %d-hour day trip across %s tailored for %s (%d travelers)",
                trip.getAvailableHours(), trip.getDestinationCity(), trip.getTravelPreference(), trip.getGroupSize());

        String narrative = String.format("Welcome to %s! This custom day tour is optimized for %d traveler(s) focusing on %s. " +
                "Estimated departure starts at 09:30 AM with seamless transit transitions.",
                trip.getDestinationCity(), trip.getGroupSize(), trip.getTravelPreference());

        AiParsedItinerary result = new AiParsedItinerary(summary, narrative, stops);
        result.setPromptTokens(150);
        result.setCompletionTokens(320);
        return result;
    }

    @Override
    public String getProviderName() {
        return "LOCAL_FALLBACK";
    }

    @Override
    public String getModelName() {
        return "deterministic-constraint-engine";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
