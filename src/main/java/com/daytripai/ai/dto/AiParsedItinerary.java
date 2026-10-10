package com.daytripai.ai.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Structured container holding the parsed itinerary result from an AI model.
 */
public class AiParsedItinerary {

    private String summary;
    private String narrativeAdvice;
    private List<AiParsedStop> stops = new ArrayList<>();
    private int promptTokens = 0;
    private int completionTokens = 0;

    public AiParsedItinerary() {
    }

    public AiParsedItinerary(String summary, String narrativeAdvice, List<AiParsedStop> stops) {
        this.summary = summary;
        this.narrativeAdvice = narrativeAdvice;
        this.stops = stops != null ? stops : new ArrayList<>();
    }

    // Getters and Setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getNarrativeAdvice() {
        return narrativeAdvice;
    }

    public void setNarrativeAdvice(String narrativeAdvice) {
        this.narrativeAdvice = narrativeAdvice;
    }

    public List<AiParsedStop> getStops() {
        return stops;
    }

    public void setStops(List<AiParsedStop> stops) {
        this.stops = stops;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }
}
