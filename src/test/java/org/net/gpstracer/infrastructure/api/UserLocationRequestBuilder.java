package org.net.gpstracer.infrastructure.api;

public class UserLocationRequestBuilder {
    private String userId = "38400000-8cf0-11bd-b23e-10b96e4ef00d";
    private double latitude;
    private double longitude;

    public UserLocationRequestBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserLocationRequestBuilder withLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public UserLocationRequestBuilder withLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String build() {
        return """
                        {
                            "userId" : "%s",
                            "latitude" : %s,
                            "longitude" : %s
                        }
                    """.strip().formatted(userId, latitude, longitude);
    }

}
