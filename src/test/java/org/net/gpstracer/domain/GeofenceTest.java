package org.net.gpstracer.domain;

import org.junit.jupiter.api.Test;
import org.net.gpstracer.BaseUnitTest;

import static org.assertj.core.api.Assertions.assertThat;

class GeofenceTest extends BaseUnitTest {

    @Test
    void should_return_true_if_location_is_inside_geofence() {
        // given
        Geofence geofence = new Geofence("any-id", 50.0, 20.0, 100);
        Location location = new Location(50.00001, 20.00001);

        // when
        boolean result = geofence.isInside(location);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void should_return_false_if_location_is_outside_geofence() {
        // given
        Geofence geofence = new Geofence("any-id", 50.0, 20.0, 100);
        Location location = new Location(51.1, 21.1);

        // when
        boolean result = geofence.isInside(location);

        // then
        assertThat(result).isFalse();
    }

}