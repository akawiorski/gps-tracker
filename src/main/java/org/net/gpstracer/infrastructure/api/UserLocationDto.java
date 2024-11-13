package org.net.gpstracer.infrastructure.api;

import java.util.UUID;

public record UserLocationDto(
        Double latitude,
        Double longitude,
        UUID userId
) {
}
