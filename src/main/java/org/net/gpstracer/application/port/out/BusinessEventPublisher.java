package org.net.gpstracer.application.port.out;

import java.util.UUID;

public interface BusinessEventPublisher {
    void geofenceEntered(String id, UUID userId, String eventType);
}
