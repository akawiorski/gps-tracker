package org.net.gpstracer.application.service;

import org.net.gpstracer.application.domainevent.DomainEventListener;
import org.net.gpstracer.application.domainevent.UserLocationRecordedEvent;
import org.net.gpstracer.application.port.in.GeofenceRecorder;
import org.net.gpstracer.application.port.out.BusinessEventPublisher;
import org.net.gpstracer.application.port.out.GeofenceRepository;
import org.net.gpstracer.application.port.out.UserLocationRepository;
import org.net.gpstracer.domain.Geofence;
import org.net.gpstracer.domain.UserLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Service
public class GeofenceService implements GeofenceRecorder, DomainEventListener<UserLocationRecordedEvent> {

    private static final Logger log = LoggerFactory.getLogger(GeofenceService.class);
    private final GeofenceRepository geofenceRepository;
    private final BusinessEventPublisher businessEventPublisher;

    private final UserLocationRepository userLocationRepository;

    public GeofenceService(final GeofenceRepository geofenceRepository,
                           final BusinessEventPublisher businessEventPublisher,
                           final UserLocationRepository userLocationRepository) {
        this.geofenceRepository = geofenceRepository;
        this.businessEventPublisher = businessEventPublisher;
        this.userLocationRepository = userLocationRepository;
    }

    @Override
    public void registerGeofence(final Geofence geofence) {
        geofenceRepository.registerGeofence(geofence);
    }

    @Override
    public Class<UserLocationRecordedEvent> getEventType() {
        return UserLocationRecordedEvent.class;
    }

    @Override
    public void onEvent(final UserLocationRecordedEvent event) {

        userLocationRepository.getPreviousUserLocation(event.userId(), event.eventTime())
                .ifPresent(previousUserLocation -> {
                    log.info("Previous user location: {}", previousUserLocation);
                    geofenceRepository.findAllGeofences().forEach(geofence -> {
                        if (geofence.isInside(event.getLocation()) && !geofence.isInside(previousUserLocation.getLocation())){
                            log.info("User {} entered geofence {}", event.userId(), geofence.id());
                            businessEventPublisher.geofenceEntered(geofence.id(), event.userId(), "GEOFENCE_TRANSITION_ENTER");
                        } else if (!geofence.isInside(event.getLocation()) && geofence.isInside(previousUserLocation.getLocation())) {
                            businessEventPublisher.geofenceEntered(geofence.id(), event.userId(), "GEOFENCE_TRANSITION_EXIT");
                            log.info("User {} exited geofence {}", event.userId(), geofence.id());
                        } else {
                            log.info("User {} did not cross geofence {} border", event.userId(), geofence.id());
                        }
                    });
                });
    }
}
