package com.motoroute.api.domain.route.vo;

import java.math.BigDecimal;

public record CalculateRouteVO(
        Long userId,
        BigDecimal startLat,
        BigDecimal startLon,
        BigDecimal endLat,
        BigDecimal endLon
) {}
