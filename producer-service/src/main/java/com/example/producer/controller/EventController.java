package com.example.producer.controller;

import com.example.producer.config.RabbitMQConfig;
import com.example.producer.model.UserActivityEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final RabbitTemplate rabbitTemplate;

    public EventController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/events/track")
    public ResponseEntity<Void> trackEvent(@Valid @RequestBody UserActivityEvent event) {
        // Send the JSON payload to the RabbitMQ queue
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, event);
        
        // Return 202 Accepted immediately
        return ResponseEntity.accepted().build(); 
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        // Required health check endpoint for Docker Compose
        return ResponseEntity.ok("OK");
    }
}