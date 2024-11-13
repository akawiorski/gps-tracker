package org.net.gpstracer.infrastructure.api;

import org.net.gpstracer.application.port.in.GeofenceRecorder;
import org.net.gpstracer.domain.Geofence;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeofenceController {

    private final GeofenceRecorder geofenceRecorder;

    public GeofenceController(GeofenceRecorder geofenceRecorder) {
        this.geofenceRecorder = geofenceRecorder;
    }

    @PostMapping("/geofence")
    public void registerGeofence(@RequestBody final GeofenceDto geofenceDto) {
        geofenceRecorder.registerGeofence(toDomain(geofenceDto));
    }

    private Geofence toDomain(final GeofenceDto geofenceDto) {
        return new Geofence(
                geofenceDto.id(),
                geofenceDto.latitude(),
                geofenceDto.longitude(),
                geofenceDto.radiusInMeters()
        );
    }
}
