package org.net.gpstracer.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.net.gpstracer.BaseUnitTest;
import org.net.gpstracer.application.domainevent.DomainEventBroadcaster;
import org.net.gpstracer.application.domainevent.UserLocationRecordedEvent;
import org.net.gpstracer.application.port.out.UserLocationRepository;
import org.net.gpstracer.domain.UserLocation;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserLocationServiceTest extends BaseUnitTest {

    private UserLocationService userLocationService;

    @Mock
    private UserLocationRepository userLocationRepository;

    @Mock
    private DomainEventBroadcaster domainEventBroadcaster;

    @BeforeEach
    void setUp() {
        userLocationService = new UserLocationService(userLocationRepository, domainEventBroadcaster);
    }

    @Test
    void should_record_user_location_and_emmit_event() {
        // given
        final UUID userId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        final UserLocation userLocation = new UserLocation(userId, Instant.parse("2020-01-01T00:00:00Z"), 1.0, 1.0);
        // when
        userLocationService.recordUserLocation(userLocation);
        // then
        verify(userLocationRepository).recordUserLocation(userLocation);
        verify(domainEventBroadcaster).emmit(new UserLocationRecordedEvent(
                userId,
                Instant.parse("2020-01-01T00:00:00Z"),
                1.0,
                1.0)
        );
    }
}