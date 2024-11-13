package org.net.gpstracer.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    private static final DockerImageName LOCALSTACK_IMAGE_NAME = DockerImageName.parse("localstack/localstack:latest");


    @Bean
    public LocalStackContainer localStackContainer() {
        return new LocalStackContainer(LOCALSTACK_IMAGE_NAME)
                .withServices(DYNAMODB, SQS);
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(final LocalStackContainer localStackContainer) {

        AwsBasicCredentials creds = AwsBasicCredentials.builder()
                .accessKeyId(localStackContainer.getAccessKey())
                .secretAccessKey(localStackContainer.getSecretKey())
                .build();
        return StaticCredentialsProvider.create(creds);
    }

    @Bean
    public DynamoDbClient dynamoDbClient(
            final AwsCredentialsProvider credentialsProvider,
            final LocalStackContainer localstack
    ) {
        return DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(localstack.getEndpointOverride(DYNAMODB))
                .region(Region.of(localstack.getRegion()))
                .build();
    }

    @Bean
    public SqsClient sqsClient(
            final AwsCredentialsProvider credentialsProvider,
            final LocalStackContainer localstack
    ) {
        return SqsClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(localstack.getEndpointOverride(SQS))
                .region(Region.of(localstack.getRegion()))
                .build();
    }

}
