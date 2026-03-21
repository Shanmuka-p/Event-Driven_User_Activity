package com.example.consumer.service;

import com.example.consumer.entity.UserActivity;
import com.example.consumer.repository.UserActivityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventConsumerTest {

    @Mock
    private UserActivityRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EventConsumer consumer;

    @Test
    public void testReceiveMessage_SavesToDatabase() throws Exception {
        // 1. Create a fake RabbitMQ message payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", 123);
        payload.put("event_type", "login");
        payload.put("timestamp", "2026-03-20T21:00:00Z");

        // 2. Trigger the consumer
        consumer.receiveMessage(payload);

        // 3. Verify that the consumer successfully mapped the JSON and called repository.save()
        ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
        verify(repository).save(captor.capture());

        // 4. Assertions
        UserActivity savedActivity = captor.getValue();
        assertEquals(123L, savedActivity.getUserId());
        assertEquals("login", savedActivity.getEventType());
        assertEquals(OffsetDateTime.parse("2026-03-20T21:00:00Z"), savedActivity.getTimestamp());
    }
}