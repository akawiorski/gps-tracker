package org.net.gpstracer.application.port.out;

import org.net.gpstracer.domain.Geofence;

import java.util.List;
import java.util.Map;

public interface GeofenceRepository {
    void registerGeofence(Geofence geofence);

    List<Geofence> findAllGeofences();
}
