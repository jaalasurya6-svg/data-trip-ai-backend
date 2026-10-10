package com.daytripai.dto.request;

/**
 * Request DTO for configuring AI Itinerary Generation.
 */
public class AiItineraryGenerateRequest {

    /**
     * Preferred provider: 'gemini', 'openai', or 'fallback' (rule engine).
     */
    private String provider;

    /**
     * Optional custom traveler prompt instructions (e.g., 'Prefer kid-friendly spots, avoid steep stairs').
     */
    private String customInstructions;

    /**
     * Preferred primary transport mode ('CAB', 'WALK', 'METRO', 'BUS').
     */
    private String preferredTransportMode = "CAB";

    public AiItineraryGenerateRequest() {
    }

    public AiItineraryGenerateRequest(String provider, String customInstructions, String preferredTransportMode) {
        this.provider = provider;
        this.customInstructions = customInstructions;
        this.preferredTransportMode = preferredTransportMode;
    }

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCustomInstructions() {
        return customInstructions;
    }

    public void setCustomInstructions(String customInstructions) {
        this.customInstructions = customInstructions;
    }

    public String getPreferredTransportMode() {
        return preferredTransportMode;
    }

    public void setPreferredTransportMode(String preferredTransportMode) {
        this.preferredTransportMode = preferredTransportMode;
    }
}
