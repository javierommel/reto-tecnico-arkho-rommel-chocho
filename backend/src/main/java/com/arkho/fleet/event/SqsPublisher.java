package com.arkho.fleet.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SqsPublisher {


    private final ObjectMapper objectMapper;

    public SqsPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void send(Object event) {

        try {
            String message = objectMapper.writeValueAsString(event);

            log.info("SQS_SIMULATION: {}", message);

        } catch (Exception e) {
            log.error("Error serializing event", e);
        }
    }
}