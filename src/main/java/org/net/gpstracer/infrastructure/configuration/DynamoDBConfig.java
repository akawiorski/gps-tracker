package org.net.gpstracer.infrastructure.configuration;

import org.net.gpstracer.infrastructure.adapter.GeofenceEntity;
import org.net.gpstracer.infrastructure.adapter.UserLocationEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.dynamodb.endpoint}")
    private String dynamoDbEndpoint;

    @Value("${aws.secret.region}")
    private String region;

    @Value("${aws.secret.access-key}")
    private String accessKey;

    @Value("${aws.secret.secret-key}")
    private String secretKey;

    @Bean
    @Profile("!test")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
    }


    @Bean
    @Profile("!test")
    public DynamoDbClient dynamoDbClient(final AwsCredentialsProvider awsCredentialsProvider) {

        return  DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(final DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<UserLocationEntity> userLocationTable(final DynamoDbEnhancedClient dynamoDbEnhancedClient) {

        TableSchema<UserLocationEntity> documentSchema = TableSchema.fromBean(UserLocationEntity.class);
        var table = dynamoDbEnhancedClient.table("UserLocation", documentSchema);
        if(!tableExists(table)) {
            table.createTable();
        }
        return table;
    }

    @Bean
    public DynamoDbTable<GeofenceEntity> geofenceTable(final DynamoDbEnhancedClient dynamoDbEnhancedClient) {

        TableSchema<GeofenceEntity> documentSchema = TableSchema.fromBean(GeofenceEntity.class);
        var table = dynamoDbEnhancedClient.table("Geofence", documentSchema);
        if(!tableExists(table)) {
            table.createTable();
        }
        return table;
    }

    private boolean tableExists(DynamoDbTable<?> table) {
        try {
            TableDescription tableDescription = table.describeTable().table();
            return tableDescription.tableStatus() != TableStatus.DELETING;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

}