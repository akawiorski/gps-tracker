package org.net.gpstracer.domain;

public record Geofence(
        String id,
        Double latitude,
        Double longitude,
        Integer radiusInMeters
) {
    private static final double EARTH_RADIUS_M = 6371.0 * 1000; // Earth's radiusInMeters in meters

    public Geofence {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        if (radiusInMeters == null) throw new IllegalArgumentException("radiusInMeters must not be null");
        if (latitude == null) throw new IllegalArgumentException("latitude must not be null");
        if (longitude == null) throw new IllegalArgumentException("longitude must not be null");
    }

    public boolean isInside(final Location location) {

        double lat1Rad = Math.toRadians(location.latitude());
        double lon1Rad = Math.toRadians(location.longitude());
        double lat2Rad = Math.toRadians(latitude);
        double lon2Rad = Math.toRadians(longitude);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceInMeters = EARTH_RADIUS_M * c;
        return distanceInMeters < radiusInMeters.doubleValue();
    }
}
