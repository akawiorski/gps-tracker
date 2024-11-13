package org.net.gpstracer.infrastructure.adapter;

import org.net.gpstracer.application.port.out.BusinessEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;

@Component
public class SqsBusinessEventPublisher implements BusinessEventPublisher {

    private Logger log = LoggerFactory.getLogger(SqsBusinessEventPublisher.class);

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-name}")
    private String queueName;

    public SqsBusinessEventPublisher(final SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public void geofenceEntered(final String id,
                                final UUID userId,
                                final String eventType) {
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .messageBody("""
                        {
                            "geofenceId": "%s",
                            "userId": "%s",
                            "eventType": "%s"
                        }
                        """.formatted(id, userId, eventType).strip())
                .queueUrl(queueName)
                .build();
        SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);
        log.info("Message published with ID: {}", response.messageId());
    }
}
