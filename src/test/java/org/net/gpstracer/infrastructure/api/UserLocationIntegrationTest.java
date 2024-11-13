package org.net.gpstracer.infrastructure.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.net.gpstracer.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserLocationIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SqsClient sqsClient;

    @Value("${aws.sqs.queue-name}")
    private String queueName;

    @Test
    void should_record_user_location_when_there_are_no_geofences_registered() throws Exception {

        final var recordUserLocationRequest = new UserLocationRequestBuilder()
                .withLatitude(12.34)
                .withLongitude(56.78)
                .build();

        performRequest("/user-location", recordUserLocationRequest);
    }

    @Test
    void should_trigger_event_when_gps_point_is_inside_geofence() throws Exception {

        final var registerGeofenceRequest = """
                {
                    "id" : "office-geofence",
                    "latitude" : 50.07672471302663,
                    "longitude" : 19.94672587408553,
                    "radiusInMeters" : 100
                }
                """.trim();

        performRequest("/geofence", registerGeofenceRequest);

        final var userOutsideGeofenceRequest = new UserLocationRequestBuilder()
                .withLatitude(51.07702766750613)
                .withLongitude(19.947519807887176)
                .build();

        performRequest("/user-location", userOutsideGeofenceRequest);

        final var userInsideGeofenceRequest = new UserLocationRequestBuilder()
                .withLatitude(50.07702766750613)
                .withLongitude(19.947519807887176)
                .build();

        performRequest("/user-location", userInsideGeofenceRequest);

        List<String> messages = receiveMessages();
        Assertions.assertThat(messages).containsExactly("""
                {
                    "geofenceId": "office-geofence",
                    "userId": "38400000-8cf0-11bd-b23e-10b96e4ef00d",
                    "eventType": "GEOFENCE_TRANSITION_ENTER"
                }
                """.strip());
    }

    @Test
    void should_not_trigger_event_when_gps_point_is_outside_geofence() throws Exception {

        final var registerGeofenceRequest = """
                {
                    "id" : "office-geofence",
                    "latitude" : 50.07672471302663,
                    "longitude" : 19.94672587408553,
                    "radiusInMeters" : 100
                }
                """.trim();

        performRequest("/geofence", registerGeofenceRequest);

        final var userInsideGeofenceRequest = new UserLocationRequestBuilder()
                .withLatitude(50.07702766750613)
                .withLongitude(19.947519807887176)
                .build();

        performRequest("/user-location", userInsideGeofenceRequest);

        final var userOutsideGeofenceRequest = new UserLocationRequestBuilder()
                .withLatitude(50.08566439128211)
                .withLongitude(19.95376970931042)
                .build();

        performRequest("/user-location", userOutsideGeofenceRequest);

        List<String> messages = receiveMessages();
        Assertions.assertThat(messages.get(0)).isEqualTo("""
                {
                    "geofenceId": "office-geofence",
                    "userId": "38400000-8cf0-11bd-b23e-10b96e4ef00d",
                    "eventType": "GEOFENCE_TRANSITION_EXIT"
                }
                """.strip());
    }

    private void performRequest(String urlTemplate, String registerGeofenceRequest) throws Exception {
        mockMvc.perform(post(urlTemplate).contentType(MediaType.APPLICATION_JSON)
                        .content(registerGeofenceRequest))
                .andExpect(status().isOk());
    }

    private List<String> receiveMessages() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueName)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(1)
                .build();

        return sqsClient.receiveMessage(receiveMessageRequest).messages()
                .stream()
                .map(Message::body).toList();
    }

}