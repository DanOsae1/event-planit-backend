package com.osaebros.eventplanner.utils;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class GeometryUtils {

    private final GeometryFactory geometryFactory;

    public Point createPoint(@NotNull Double lng, @NotNull Double lat) {
        return geometryFactory.createPoint(new Coordinate(lng, lat));
    }
}
