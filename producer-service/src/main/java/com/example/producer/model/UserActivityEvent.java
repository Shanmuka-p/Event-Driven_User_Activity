package com.example.producer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class UserActivityEvent {
    @NotNull(message = "user_id cannot be null")
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank(message = "event_type cannot be blank")
    @JsonProperty("event_type")
    private String eventType;

    @NotNull(message = "timestamp cannot be null")
    private OffsetDateTime timestamp;

    private Map<String, Object> metadata;
}