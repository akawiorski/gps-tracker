package org.net.gpstracer.application.port.in;

import org.net.gpstracer.domain.UserLocation;

public interface UserLocationRecorder {
    void recordUserLocation(final UserLocation userLocation);
}
