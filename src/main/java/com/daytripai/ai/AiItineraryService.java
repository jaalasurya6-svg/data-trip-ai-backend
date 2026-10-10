package com.daytripai.ai;

import com.daytripai.ai.dto.AiParsedItinerary;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Trip;

import java.util.List;

/**
 * Common abstraction implemented by all AI itinerary generator backends.
 */
public interface AiItineraryService {

    /**
     * Generates a structured itinerary using the AI engine.
     *
     * @param trip               The trip entity containing constraints (city, budget, hours, group size, preference).
     * @param candidates         Available attraction destinations matching the trip city.
     * @param customInstructions Optional customized prompt instructions from traveler.
     * @param transportMode      Preferred primary transport mode.
     * @return Structured parsed itinerary.
     * @throws Exception If communication or parsing fails.
     */
    AiParsedItinerary generate(Trip trip, List<Destination> candidates, String customInstructions, String transportMode) throws Exception;

    /**
     * Returns the name of the AI provider (e.g. 'GEMINI', 'OPENAI', 'LOCAL_FALLBACK').
     */
    String getProviderName();

    /**
     * Returns the model identifier (e.g. 'gemini-1.5-flash', 'gpt-4o-mini', 'rule-based-engine').
     */
    String getModelName();

    /**
     * Returns true if required credentials/endpoints are configured and ready.
     */
    boolean isAvailable();
}
