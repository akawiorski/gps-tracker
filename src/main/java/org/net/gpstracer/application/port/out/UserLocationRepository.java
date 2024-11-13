package org.net.gpstracer.application.port.out;

import org.net.gpstracer.domain.UserLocation;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserLocationRepository {
    void recordUserLocation(UserLocation userLocation);

    Optional<UserLocation> getPreviousUserLocation(UUID userId, Instant eventTime);
}
