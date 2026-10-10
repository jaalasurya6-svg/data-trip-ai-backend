package com.daytripai.dto.response;

/**
 * Top-level response for AI-driven itinerary generation, combining the calculated itinerary,
 * AI narrative advice, and provider execution metadata.
 */
public class AiItineraryResponse {

    private ItineraryResponse itinerary;
    private String aiNarrative;
    private AiGenerationMetadata metadata;

    public AiItineraryResponse() {
    }

    public AiItineraryResponse(ItineraryResponse itinerary, String aiNarrative, AiGenerationMetadata metadata) {
        this.itinerary = itinerary;
        this.aiNarrative = aiNarrative;
        this.metadata = metadata;
    }

    // Getters and Setters
    public ItineraryResponse getItinerary() {
        return itinerary;
    }

    public void setItinerary(ItineraryResponse itinerary) {
        this.itinerary = itinerary;
    }

    public String getAiNarrative() {
        return aiNarrative;
    }

    public void setAiNarrative(String aiNarrative) {
        this.aiNarrative = aiNarrative;
    }

    public AiGenerationMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AiGenerationMetadata metadata) {
        this.metadata = metadata;
    }
}
