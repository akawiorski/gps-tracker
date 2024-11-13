package org.net.gpstracer.domain;

import java.time.Instant;
import java.util.UUID;

public record UserLocation(
        UUID userId,
        Instant eventTime,
        Double latitude,
        Double longitude
) {
    public UserLocation {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        if (eventTime == null) throw new IllegalArgumentException("eventTime must not be null");
        if (latitude == null) throw new IllegalArgumentException("latitude must not be null");
        if (longitude == null) throw new IllegalArgumentException("longitude must not be null");
    }

    public Location getLocation() {
        return new Location(latitude, longitude);
    }
}
