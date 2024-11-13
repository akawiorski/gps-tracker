package org.net.gpstracer.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class SqsConfiguration {

    @Value("${aws.sqs.endpoint}")
    private String sqsEndpoint;

    @Value("${aws.secret.region}")
    private String region;

    @Bean
    @Profile("!test")
    public SqsClient sqsClient(
            final AwsCredentialsProvider credentialsProvider
    ) {
        return SqsClient.builder()
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(sqsEndpoint))
                .region(Region.of(region))
                .build();
    }
}
