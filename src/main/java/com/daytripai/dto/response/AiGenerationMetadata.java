package com.daytripai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metadata detailing the AI model execution, latency, and fallback state.
 */
public class AiGenerationMetadata {

    private String providerUsed;
    private String modelName;

    @JsonProperty("isFallback")
    private boolean isFallback;

    private String fallbackReason;
    private Long latencyMs;
    private Integer promptTokens;
    private Integer completionTokens;

    public AiGenerationMetadata() {
    }

    public AiGenerationMetadata(String providerUsed, String modelName, boolean isFallback,
                                String fallbackReason, Long latencyMs, Integer promptTokens, Integer completionTokens) {
        this.providerUsed = providerUsed;
        this.modelName = modelName;
        this.isFallback = isFallback;
        this.fallbackReason = fallbackReason;
        this.latencyMs = latencyMs;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
    }

    // Getters and Setters
    public String getProviderUsed() {
        return providerUsed;
    }

    public void setProviderUsed(String providerUsed) {
        this.providerUsed = providerUsed;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public boolean isFallback() {
        return isFallback;
    }

    public void setFallback(boolean fallback) {
        isFallback = fallback;
    }

    public String getFallbackReason() {
        return fallbackReason;
    }

    public void setFallbackReason(String fallbackReason) {
        this.fallbackReason = fallbackReason;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }
}
