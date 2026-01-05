package com.motoroute.api.domain.route.entity;

import com.motoroute.api.common.entity.AbstractIdStatusEntity;
import com.motoroute.api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "route_history", indexes = {
        @Index(name = "idx_route_user_id", columnList = "user_id")
})
public class RouteHistory extends AbstractIdStatusEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal startLat;

    @Column(name = "start_lon", nullable = false, precision = 11, scale = 8)
    private BigDecimal startLon;

    @Column(name = "end_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal endLat;

    @Column(name = "end_lon", nullable = false, precision = 11, scale = 8)
    private BigDecimal endLon;

    @Column(name = "distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal distance;

    @Column(name = "duration", nullable = false, precision = 10, scale = 2)
    private BigDecimal duration;

    @Column(name = "route_geometry", columnDefinition = "text")
    private String routeGeometry;
}
