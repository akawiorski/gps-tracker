package org.net.gpstracer.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.net.gpstracer.BaseUnitTest;
import org.net.gpstracer.application.domainevent.UserLocationRecordedEvent;
import org.net.gpstracer.application.port.out.BusinessEventPublisher;
import org.net.gpstracer.application.port.out.GeofenceRepository;
import org.net.gpstracer.application.port.out.UserLocationRepository;
import org.net.gpstracer.domain.Geofence;
import org.net.gpstracer.domain.UserLocation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeofenceServiceTest extends BaseUnitTest {

    private GeofenceService geofenceService;

    @Mock
    private GeofenceRepository geofenceRepository;
    @Mock
    private UserLocationRepository userLocationRepository;
    @Mock
    private BusinessEventPublisher businessEventPublisher;

    @BeforeEach
    void setUp() {
        geofenceService = new GeofenceService(geofenceRepository, businessEventPublisher, userLocationRepository);
    }
    @Test
    void should_not_emmit_event_if_there_is_no_previous_user_location() {
        // given
        final UUID userId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Instant eventTime = Instant.parse("2020-01-01T00:00:00Z");
        when(userLocationRepository.getPreviousUserLocation(userId, eventTime)).thenReturn(Optional.empty());

        // when
        geofenceService.onEvent(
                new UserLocationRecordedEvent(
                        userId,
                        eventTime,
                        1.0,
                        1.0
                )
        );

        // then
        verify(businessEventPublisher, never()).geofenceEntered(any(), any(), any());
    }

    @Test
    void should_emmit_transition_geofence_entered_event_if_previous_user_location_was_outside_geofence() {
        // given
        final UUID userId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Instant eventTime = Instant.parse("2020-01-01T00:00:00Z");
        when(geofenceRepository.findAllGeofences()).thenReturn(List.of(
                new Geofence("geofence-any-id", 10.0, 20.0, 1_000)
        ));
        when(userLocationRepository.getPreviousUserLocation(userId, eventTime)).thenReturn(Optional.of(
                new UserLocation(userId, eventTime.minusSeconds(1), 11.1, 22.2)
        ));

        UserLocationRecordedEvent locationInsideGeofenceEvent = new UserLocationRecordedEvent(userId, eventTime, 10.0001, 20.00001);

        // when
        geofenceService.onEvent(locationInsideGeofenceEvent);

        // then
        verify(businessEventPublisher).geofenceEntered( "geofence-any-id", userId, "GEOFENCE_TRANSITION_ENTER");
    }

    @Test
    void should_emmit_transition_geofence_exit_event_if_previous_user_location_was_inside_geofence() {
        // given
        final UUID userId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Instant eventTime = Instant.parse("2020-01-01T00:00:00Z");
        when(geofenceRepository.findAllGeofences()).thenReturn(List.of(
                new Geofence("geofence-any-id", 10.0, 20.0, 1_000)
        ));
        when(userLocationRepository.getPreviousUserLocation(userId, eventTime)).thenReturn(Optional.of(
                new UserLocation(userId, eventTime.minusSeconds(1), 10.0001, 20.0002)
        ));

        UserLocationRecordedEvent locationOutsideGeofenceEvent = new UserLocationRecordedEvent(userId, eventTime, 11.1, 22.2);

        // when
        geofenceService.onEvent(locationOutsideGeofenceEvent);

        // then
        verify(businessEventPublisher).geofenceEntered( "geofence-any-id", userId, "GEOFENCE_TRANSITION_EXIT");
    }

    @Test
    void should_not_emmit_event_if_previous_user_location_was_inside_geofence_and_current_is_also_inside() {
        // given
        final UUID userId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Instant eventTime = Instant.parse("2020-01-01T00:00:00Z");
        when(geofenceRepository.findAllGeofences()).thenReturn(List.of(
                new Geofence("geofence-any-id", 10.0, 20.0, 1_000)
        ));
        when(userLocationRepository.getPreviousUserLocation(userId, eventTime)).thenReturn(Optional.of(
                new UserLocation(userId, eventTime.minusSeconds(1), 10.0001, 20.0002)
        ));

        UserLocationRecordedEvent locationInsideGeofenceEvent = new UserLocationRecordedEvent(userId, eventTime, 10.0002, 20.0001);

        // when
        geofenceService.onEvent(locationInsideGeofenceEvent);

        // then
        verify(businessEventPublisher, never()).geofenceEntered(any(), any(), any());
    }

    @Test
    void should_not_emmit_event_if_previous_user_location_was_outside_geofence_and_current_is_also_outside() {
        // given
        final UUID userId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Instant eventTime = Instant.parse("2020-01-01T00:00:00Z");
        when(geofenceRepository.findAllGeofences()).thenReturn(List.of(
                new Geofence("geofence-any-id", 10.0, 20.0, 1_000)
        ));
        when(userLocationRepository.getPreviousUserLocation(userId, eventTime)).thenReturn(Optional.of(
                new UserLocation(userId, eventTime.minusSeconds(1), 11.1, 22.2)
        ));

        UserLocationRecordedEvent locationOutsideGeofenceEvent = new UserLocationRecordedEvent(userId, eventTime, 11.2, 22.3);

        // when
        geofenceService.onEvent(locationOutsideGeofenceEvent);

        // then
        verify(businessEventPublisher, never()).geofenceEntered(any(), any(), any());
    }
}