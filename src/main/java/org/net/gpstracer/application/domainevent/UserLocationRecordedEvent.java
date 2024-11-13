package org.net.gpstracer.application.domainevent;

import org.net.gpstracer.domain.Location;

import java.time.Instant;
import java.util.UUID;

public record UserLocationRecordedEvent(
        UUID userId,
        Instant eventTime,
        Double latitude,
        Double longitude
) implements DomainEvent{

    public Location getLocation() {
        return new Location(latitude, longitude);
    }
}
