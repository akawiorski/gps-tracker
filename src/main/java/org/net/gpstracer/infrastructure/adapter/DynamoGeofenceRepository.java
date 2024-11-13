package org.net.gpstracer.infrastructure.adapter;

import org.net.gpstracer.application.port.out.GeofenceRepository;
import org.net.gpstracer.domain.Geofence;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.List;

@Repository
public class DynamoGeofenceRepository implements GeofenceRepository {

    private final DynamoDbTable<GeofenceEntity> geofenceTable;


    public DynamoGeofenceRepository(DynamoDbTable<GeofenceEntity> geofenceTable) {
        this.geofenceTable = geofenceTable;
    }

    @Override
    public void registerGeofence(Geofence geofence) {
        var item = GeofenceEntity.from(geofence);
        geofenceTable.putItem(item);
    }

    @Override
    public List<Geofence> findAllGeofences() {
        return geofenceTable.scan().items().stream()
                .map(this::toGeofence)
                .toList();
    }

    private Geofence toGeofence(GeofenceEntity geofenceEntity) {
        return new Geofence(
                geofenceEntity.getId(),
                geofenceEntity.getLatitude(),
                geofenceEntity.getLongitude(),
                geofenceEntity.getRadius()
        );
    }
}
