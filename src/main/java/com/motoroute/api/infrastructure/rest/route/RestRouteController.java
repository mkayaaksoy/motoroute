package com.motoroute.api.infrastructure.rest.route;

import com.motoroute.api.application.route.RouteManager;
import com.motoroute.api.common.dto.request.CalculateRouteRequest;
import com.motoroute.api.common.dto.response.RouteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Tag(name = "Routes", description = "Route calculation and history endpoints")
public class RestRouteController {

    private final RouteManager routeManager;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate route between two points")
    public ResponseEntity<RouteResponse> calculateRoute(
            @Valid @RequestBody CalculateRouteRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        RouteResponse response = routeManager.calculateRoute(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get route history for current user")
    public ResponseEntity<Page<RouteResponse>> getRouteHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<RouteResponse> response = routeManager.getRouteHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
