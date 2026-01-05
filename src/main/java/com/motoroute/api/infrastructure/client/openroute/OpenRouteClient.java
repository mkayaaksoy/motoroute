package com.motoroute.api.infrastructure.client.openroute;

import com.fasterxml.jackson.databind.JsonNode;
import com.motoroute.api.infrastructure.config.OpenRouteProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OpenRouteClient {

    private final OpenRouteProperties openRouteProperties;
    private final WebClient webClient;

    public OpenRouteClient(OpenRouteProperties openRouteProperties) {
        this.openRouteProperties = openRouteProperties;
        this.webClient = WebClient.builder()
                .baseUrl(openRouteProperties.baseUrl())
                .defaultHeader("Authorization", openRouteProperties.apiKey())
                .build();
    }

    public OpenRouteResponse calculateRoute(BigDecimal startLat, BigDecimal startLon, 
                                           BigDecimal endLat, BigDecimal endLon) {
        log.debug("Calling OpenRouteService API for route calculation");

        Map<String, Object> requestBody = Map.of(
                "coordinates", List.of(
                        List.of(startLon, startLat),
                        List.of(endLon, endLat)
                ),
                "preference", "shortest",
                "units", "m"
        );

        try {
            JsonNode response = webClient.post()
                    .uri("/v2/directions/driving-car")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || !response.has("routes") || response.get("routes").isEmpty()) {
                throw new RuntimeException("No route found in OpenRouteService response");
            }

            JsonNode route = response.get("routes").get(0);
            JsonNode summary = route.get("summary");
            JsonNode geometry = route.get("geometry");

            BigDecimal distance = BigDecimal.valueOf(summary.get("distance").asDouble());
            BigDecimal duration = BigDecimal.valueOf(summary.get("duration").asDouble());
            
            List<List<BigDecimal>> coordinates = parseCoordinates(geometry);
            String geometryString = geometry.toString();

            log.info("Route calculated successfully: distance={}, duration={}", distance, duration);

            return new OpenRouteResponse(distance, duration, coordinates, geometryString);

        } catch (Exception e) {
            log.error("Failed to call OpenRouteService API", e);
            throw new RuntimeException("Failed to calculate route from OpenRouteService", e);
        }
    }

    private List<List<BigDecimal>> parseCoordinates(JsonNode geometry) {
        List<List<BigDecimal>> coordinates = new ArrayList<>();
        
        if (geometry.has("coordinates")) {
            JsonNode coords = geometry.get("coordinates");
            if (coords.isArray()) {
                for (JsonNode coord : coords) {
                    if (coord.isArray() && coord.size() >= 2) {
                        List<BigDecimal> point = List.of(
                                BigDecimal.valueOf(coord.get(0).asDouble()),
                                BigDecimal.valueOf(coord.get(1).asDouble())
                        );
                        coordinates.add(point);
                    }
                }
            }
        }
        
        return coordinates;
    }
}
