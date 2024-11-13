package org.net.gpstracer.application.service;

import org.net.gpstracer.application.domainevent.DomainEventBroadcaster;
import org.net.gpstracer.application.domainevent.UserLocationRecordedEvent;
import org.net.gpstracer.application.port.in.UserLocationRecorder;
import org.net.gpstracer.application.port.out.UserLocationRepository;
import org.net.gpstracer.domain.UserLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserLocationService implements UserLocationRecorder {

    private static Logger log = LoggerFactory.getLogger(UserLocationService.class);
    private final UserLocationRepository userLocationRepository;
    private final DomainEventBroadcaster domainEventBroadcaster;
    public UserLocationService(final UserLocationRepository userLocationRepository,
                               final DomainEventBroadcaster domainEventBroadcaster) {
        this.userLocationRepository = userLocationRepository;
        this.domainEventBroadcaster = domainEventBroadcaster;
    }

    public void recordUserLocation(final UserLocation userLocation) {
        log.info("Recording user location for user {}", userLocation.userId());
        userLocationRepository.recordUserLocation(userLocation);
        domainEventBroadcaster.emmit(toEvent(userLocation));
    }

    private UserLocationRecordedEvent toEvent(final UserLocation userLocation) {
        return new UserLocationRecordedEvent(
                userLocation.userId(),
                userLocation.eventTime(),
                userLocation.latitude(),
                userLocation.longitude()
        );
    }
}
