package org.net.gpstracer.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;

@Component
public class SqsQueueInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SqsQueueInitializer.class);

    @Value("${aws.sqs.queue-name}")
    private String queueName;
    private final SqsClient sqsClient;

    public SqsQueueInitializer(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            GetQueueUrlResponse queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
            log.info("Queue " + queueName + " already exists.");
        } catch (Exception e) {
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            sqsClient.createQueue(request);
            log.info("Queue " + queueName + " created successfully.");
        }
    }
}
