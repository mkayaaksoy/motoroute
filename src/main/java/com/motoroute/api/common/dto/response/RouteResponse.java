package com.motoroute.api.common.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RouteResponse(
        Long id,
        LocationDto start,
        LocationDto end,
        BigDecimal distance,
        BigDecimal duration,
        List<List<BigDecimal>> coordinates,
        LocalDateTime calculatedAt
) {
    public record LocationDto(
            BigDecimal latitude,
            BigDecimal longitude
    ) {}
}
