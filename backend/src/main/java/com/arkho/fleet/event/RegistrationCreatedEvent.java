package com.arkho.fleet.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistrationCreatedEvent(
        UUID id,
        String plate,
        LocalDateTime createdAt
) {}
