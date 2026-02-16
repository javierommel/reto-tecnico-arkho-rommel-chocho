package com.arkho.fleet.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventListener {

    private final SqsPublisher sqsPublisher;

    public RegistrationEventListener(SqsPublisher sqsPublisher) {
        this.sqsPublisher = sqsPublisher;
    }

    @Async
    @EventListener
    public void handleRegistrationCreated(RegistrationCreatedEvent event) {
        sqsPublisher.send(event);
    }
}
