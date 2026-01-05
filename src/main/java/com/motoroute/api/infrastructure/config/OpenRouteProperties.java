package com.motoroute.api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openroute")
public record OpenRouteProperties(
        String apiKey,
        String baseUrl
) {}
