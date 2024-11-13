package org.net.gpstracer.infrastructure.adapter;

import org.net.gpstracer.domain.Geofence;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class GeofenceEntity {

    private String id;
    private Double latitude;
    private Double longitude;
    private Integer radius;

    public GeofenceEntity(){}
    public GeofenceEntity(final String id,
                          final Double latitude,
                          final Double longitude,
                          final Integer radius) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public static GeofenceEntity from(final Geofence geofence) {
        return new GeofenceEntity(
                geofence.id(),
                geofence.latitude(),
                geofence.longitude(),
                geofence.radiusInMeters()
        );
    }


    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

}
