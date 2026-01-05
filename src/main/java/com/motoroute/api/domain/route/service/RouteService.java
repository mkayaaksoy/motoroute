package com.motoroute.api.domain.route.service;

import com.motoroute.api.common.exception.MotoRouteApiBusinessException;
import com.motoroute.api.domain.route.entity.RouteHistory;
import com.motoroute.api.domain.route.repository.RouteHistoryRepository;
import com.motoroute.api.domain.route.vo.CalculateRouteVO;
import com.motoroute.api.domain.route.vo.RouteVO;
import com.motoroute.api.domain.user.entity.User;
import com.motoroute.api.domain.user.repository.UserRepository;
import com.motoroute.api.infrastructure.client.openroute.OpenRouteClient;
import com.motoroute.api.infrastructure.client.openroute.OpenRouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteHistoryRepository routeHistoryRepository;
    private final UserRepository userRepository;
    private final OpenRouteClient openRouteClient;

    @Cacheable(value = "routes", key = "#calculateRouteVO.startLat() + '-' + #calculateRouteVO.startLon() + '-' + #calculateRouteVO.endLat() + '-' + #calculateRouteVO.endLon()")
    @Transactional(readOnly = true)
    public RouteVO calculateRoute(CalculateRouteVO calculateRouteVO) {
        log.debug("Calculating route from ({}, {}) to ({}, {})",
                calculateRouteVO.startLat(), calculateRouteVO.startLon(),
                calculateRouteVO.endLat(), calculateRouteVO.endLon());

        try {
            OpenRouteResponse response = openRouteClient.calculateRoute(
                    calculateRouteVO.startLat(),
                    calculateRouteVO.startLon(),
                    calculateRouteVO.endLat(),
                    calculateRouteVO.endLon()
            );

            // Save route history asynchronously
            saveRouteHistoryAsync(calculateRouteVO, response);

            return new RouteVO(
                    null,
                    calculateRouteVO.userId(),
                    calculateRouteVO.startLat(),
                    calculateRouteVO.startLon(),
                    calculateRouteVO.endLat(),
                    calculateRouteVO.endLon(),
                    response.distance(),
                    response.duration(),
                    response.coordinates(),
                    null
            );
        } catch (Exception e) {
            log.error("Failed to calculate route", e);
            throw new MotoRouteApiBusinessException("error.route.calculation_failed", e);
        }
    }

    @Async
    @Transactional
    public void saveRouteHistoryAsync(CalculateRouteVO calculateRouteVO, OpenRouteResponse response) {
        log.debug("Saving route history asynchronously for user id: {}", calculateRouteVO.userId());

        User user = userRepository.findById(calculateRouteVO.userId())
                .orElseThrow(() -> new MotoRouteApiBusinessException("error.user.not_found"));

        RouteHistory routeHistory = new RouteHistory();
        routeHistory.setUser(user);
        routeHistory.setStartLat(calculateRouteVO.startLat());
        routeHistory.setStartLon(calculateRouteVO.startLon());
        routeHistory.setEndLat(calculateRouteVO.endLat());
        routeHistory.setEndLon(calculateRouteVO.endLon());
        routeHistory.setDistance(response.distance());
        routeHistory.setDuration(response.duration());
        routeHistory.setRouteGeometry(response.geometry());

        routeHistoryRepository.save(routeHistory);
        log.info("Route history saved successfully");
    }

    @Transactional(readOnly = true)
    public Page<RouteVO> getRouteHistory(Long userId, Pageable pageable) {
        log.debug("Getting route history for user id: {}", userId);

        return routeHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toRouteVO);
    }

    private RouteVO toRouteVO(RouteHistory routeHistory) {
        List<List<BigDecimal>> coordinates = parseGeometry(routeHistory.getRouteGeometry());
        
        return new RouteVO(
                routeHistory.getId(),
                routeHistory.getUser().getId(),
                routeHistory.getStartLat(),
                routeHistory.getStartLon(),
                routeHistory.getEndLat(),
                routeHistory.getEndLon(),
                routeHistory.getDistance(),
                routeHistory.getDuration(),
                coordinates,
                routeHistory.getCreatedAt()
        );
    }

    private List<List<BigDecimal>> parseGeometry(String geometry) {
        // TODO: Implement proper GeoJSON parsing
        // Currently returns empty list - in production, use Jackson's GeoJSON support
        // or a dedicated library to parse the geometry string back to coordinates
        if (geometry == null || geometry.isEmpty()) {
            return List.of();
        }
        return List.of();
    }
}
