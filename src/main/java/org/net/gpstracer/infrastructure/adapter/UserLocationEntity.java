package org.net.gpstracer.infrastructure.adapter;

import org.net.gpstracer.domain.UserLocation;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.UUID;

@DynamoDbBean
public class UserLocationEntity {

    private UUID userId;
    private Instant eventTime;
    private Double latitude;
    private Double longitude;

    public UserLocationEntity(){}
    public UserLocationEntity(UUID userId, Instant eventTime, Double latitude, Double longitude) {
        this.userId = userId;
        this.eventTime = eventTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static UserLocationEntity from(UserLocation userLocation) {
        return new UserLocationEntity(
                userLocation.userId(),
                userLocation.eventTime(),
                userLocation.latitude(),
                userLocation.longitude());
    }

    @DynamoDbPartitionKey
    public UUID getUserId() {
        return userId;
    }

    @DynamoDbSortKey
    public Instant getEventTime() {
        return eventTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public UserLocation toUserLocation() {
        return new UserLocation(userId, eventTime, latitude, longitude);
    }
}
