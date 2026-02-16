package com.arkho.fleet.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class RegistrationEventPublisher {

    private final ApplicationEventPublisher publisher;

    public RegistrationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishAfterCommit(Object event) {

        if (TransactionSynchronizationManager.isActualTransactionActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            publisher.publishEvent(event);
                        }
                    }
            );

        } else {
            publisher.publishEvent(event);
        }
    }
}
