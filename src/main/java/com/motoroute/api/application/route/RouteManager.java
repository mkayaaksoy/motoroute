package com.motoroute.api.application.route;

import com.motoroute.api.common.dto.request.CalculateRouteRequest;
import com.motoroute.api.common.dto.response.RouteResponse;
import com.motoroute.api.domain.route.service.RouteService;
import com.motoroute.api.domain.route.vo.CalculateRouteVO;
import com.motoroute.api.domain.route.vo.RouteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteManager {

    private final RouteService routeService;

    public RouteResponse calculateRoute(Long userId, CalculateRouteRequest request) {
        log.info("Processing route calculation for user: {}", userId);

        CalculateRouteVO calculateRouteVO = new CalculateRouteVO(
                userId,
                request.start().latitude(),
                request.start().longitude(),
                request.end().latitude(),
                request.end().longitude()
        );

        RouteVO routeVO = routeService.calculateRoute(calculateRouteVO);

        return toRouteResponse(routeVO);
    }

    public Page<RouteResponse> getRouteHistory(Long userId, Pageable pageable) {
        log.info("Getting route history for user: {}", userId);

        return routeService.getRouteHistory(userId, pageable)
                .map(this::toRouteResponse);
    }

    private RouteResponse toRouteResponse(RouteVO routeVO) {
        return new RouteResponse(
                routeVO.id(),
                new RouteResponse.LocationDto(routeVO.startLat(), routeVO.startLon()),
                new RouteResponse.LocationDto(routeVO.endLat(), routeVO.endLon()),
                routeVO.distance(),
                routeVO.duration(),
                routeVO.coordinates(),
                routeVO.createdAt()
        );
    }
}
