package com.example.consumer.service;

import com.example.consumer.entity.UserActivity;
import com.example.consumer.repository.UserActivityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class EventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private final UserActivityRepository repository;
    private final ObjectMapper objectMapper;

    public EventConsumer(UserActivityRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "user_activity_events")
    public void receiveMessage(Map<String, Object> eventPayload) {
        try {
            logger.info("Received event: {}", eventPayload);

            UserActivity activity = new UserActivity();

            // Map the JSON payload to the entity
            activity.setUserId(((Number) eventPayload.get("user_id")).longValue());
            activity.setEventType((String) eventPayload.get("event_type"));

            // Handle timestamp parsing
            String timestampStr = (String) eventPayload.get("timestamp");
            activity.setTimestamp(java.time.OffsetDateTime.parse(timestampStr));

            // Convert metadata map back to a JSON string for the database
            if (eventPayload.containsKey("metadata")) {
                String metadataJson = objectMapper.writeValueAsString(eventPayload.get("metadata"));
                activity.setMetadata(metadataJson);
            }

            repository.save(activity);
            logger.info("Successfully saved event for user_id: {}", activity.getUserId());

        } catch (Exception e) {
            // Robust Error Handling: Log the error, but don't throw it.
            // If thrown, RabbitMQ might endlessly loop the broken message.
            logger.error("Failed to process message: {}. Error: {}", eventPayload, e.getMessage());
        }
    }
}