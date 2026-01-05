package com.motoroute.api.domain.route.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RouteVO(
        Long id,
        Long userId,
        BigDecimal startLat,
        BigDecimal startLon,
        BigDecimal endLat,
        BigDecimal endLon,
        BigDecimal distance,
        BigDecimal duration,
        List<List<BigDecimal>> coordinates,
        LocalDateTime createdAt
) {}
