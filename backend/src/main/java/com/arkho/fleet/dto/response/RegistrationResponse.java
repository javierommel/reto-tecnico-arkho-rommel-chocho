package com.arkho.fleet.dto.response;

import com.arkho.fleet.domain.RegistrationStatus;

import java.util.UUID;

public record RegistrationResponse(
        UUID id,
        String plate,
        String brand,
        String model,
        Integer year,
        String ownerName,
        String ownerEmail,
        RegistrationStatus registrationStatus
) {}
