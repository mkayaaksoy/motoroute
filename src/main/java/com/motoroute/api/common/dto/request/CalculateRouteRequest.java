package com.motoroute.api.common.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CalculateRouteRequest(
        @NotNull(message = "Start location is required")
        @Valid
        LocationDto start,

        @NotNull(message = "End location is required")
        @Valid
        LocationDto end
) {
    public record LocationDto(
            @NotNull(message = "Latitude is required")
            BigDecimal latitude,

            @NotNull(message = "Longitude is required")
            BigDecimal longitude
    ) {}
}
