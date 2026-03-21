package com.example.producer.controller;

import com.example.producer.model.UserActivityEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // We mock RabbitTemplate so it doesn't try to connect to a real broker during the test
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void validEvent_Returns202Accepted() throws Exception {
        UserActivityEvent validEvent = new UserActivityEvent();
        validEvent.setUserId(123L);
        validEvent.setEventType("page_view");
        validEvent.setTimestamp(OffsetDateTime.now());
        validEvent.setMetadata(Map.of("page_url", "/home"));

        mockMvc.perform(post("/api/v1/events/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEvent)))
                .andExpect(status().isAccepted()); // Asserts the 202 response
    }

    @Test
    public void invalidEvent_MissingUserId_Returns400BadRequest() throws Exception {
        UserActivityEvent invalidEvent = new UserActivityEvent();
        // Intentionally leaving userId null
        invalidEvent.setEventType("page_view");
        invalidEvent.setTimestamp(OffsetDateTime.now());

        mockMvc.perform(post("/api/v1/events/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isBadRequest()); // Asserts the 400 response
    }
}