package org.net.gpstracer.infrastructure.api;

import org.net.gpstracer.application.port.in.UserLocationRecorder;
import org.net.gpstracer.domain.UserLocation;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
public class UserLocationController {

    private final UserLocationRecorder userLocationRecorder;

    public UserLocationController(final UserLocationRecorder userLocationRecorder) {
        this.userLocationRecorder = userLocationRecorder;
    }

    @PostMapping("/user-location")
    public void recordUserLocation(@RequestBody final UserLocationDto userLocationDto) {
        userLocationRecorder.recordUserLocation(toDomain(userLocationDto));
    }

    private UserLocation toDomain(final UserLocationDto userLocationDto) {
        return new UserLocation(
                userLocationDto.userId(),
                Instant.now(),
                userLocationDto.latitude(),
                userLocationDto.longitude()
        );
    }

}
