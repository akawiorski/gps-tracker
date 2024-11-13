package org.net.gpstracer.infrastructure.api;

public record GeofenceDto(
        String id,
        Double latitude,
        Double longitude,
        Integer radiusInMeters
) {
}
