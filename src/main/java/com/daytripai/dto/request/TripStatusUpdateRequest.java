package com.daytripai.dto.request;

import com.daytripai.enums.TripStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating the operational status of a Trip.
 */
public class TripStatusUpdateRequest {

    @NotNull(message = "Trip status is required")
    private TripStatus status;

    public TripStatusUpdateRequest() {
    }

    public TripStatusUpdateRequest(TripStatus status) {
        this.status = status;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }
}
