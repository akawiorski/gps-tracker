package org.net.gpstracer.infrastructure.adapter;

import org.net.gpstracer.application.port.out.UserLocationRepository;
import org.net.gpstracer.domain.UserLocation;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DynamoUserLocationRepository implements UserLocationRepository {

    private final DynamoDbTable<UserLocationEntity> userLocationTable;

    public DynamoUserLocationRepository(final DynamoDbTable<UserLocationEntity> userLocationTable) {
        this.userLocationTable = userLocationTable;
    }

    @Override
    public void recordUserLocation(UserLocation userLocation) {
        var item = UserLocationEntity.from(userLocation);
        userLocationTable.putItem(item);
    }

    @Override
    public Optional<UserLocation> getPreviousUserLocation(UUID userId, Instant eventTime) {


        QueryConditional locationEarlierThan = QueryConditional.sortLessThan(
                Key.builder()
                        .sortValue(eventTime.toString())
                        .partitionValue(userId.toString())
                        .build()
        );

        return userLocationTable.query(locationEarlierThan)
                .items()
                .stream()
                .map(UserLocationEntity::toUserLocation)
                .findFirst();
    }
}
