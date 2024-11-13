package org.net.gpstracer.application.port.in;

import org.net.gpstracer.domain.Geofence;

public interface GeofenceRecorder {
    void registerGeofence(Geofence geofence);
}
