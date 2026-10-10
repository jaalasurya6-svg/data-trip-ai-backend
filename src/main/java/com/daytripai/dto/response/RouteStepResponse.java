package com.daytripai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RouteStepResponse DTO.
 * Step-by-step navigation maneuver and sub-segment metrics.
 */
@Schema(description = "Turn-by-turn navigation maneuver step")
public class RouteStepResponse {

    @Schema(description = "Step navigation cue", example = "Follow main arterial road towards destination")
    private String instruction;

    @Schema(description = "Segment distance in kilometers", example = "4.2")
    private Double distanceKm;

    @Schema(description = "Segment duration in minutes", example = "12")
    private Integer durationMinutes;

    public RouteStepResponse() {
    }

    public RouteStepResponse(String instruction, Double distanceKm, Integer durationMinutes) {
        this.instruction = instruction;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
