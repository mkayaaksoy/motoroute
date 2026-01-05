package com.motoroute.api.infrastructure.client.openroute;

import java.math.BigDecimal;
import java.util.List;

public record OpenRouteResponse(
        BigDecimal distance,
        BigDecimal duration,
        List<List<BigDecimal>> coordinates,
        String geometry
) {}
