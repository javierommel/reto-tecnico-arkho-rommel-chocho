package com.arkho.fleet.dto.request;

import jakarta.validation.constraints.*;

public record CreateRegistrationRequest(

        @NotBlank(message = "Plate is required")
        @Size(max = 10, message = "Plate must not exceed 10 characters")
        String plate,

        @NotBlank(message = "Brand is required")
        @Size(max = 50)
        String brand,

        @NotBlank(message = "Model is required")
        @Size(max = 50)
        String model,

        @NotNull(message = "Year is required")
        @Min(value = 1900, message = "Year must be valid")
        Integer year,

        @NotBlank(message = "Owner name is required")
        @Size(max = 100)
        String ownerName,

        @NotBlank(message = "Owner email is required")
        @Email(message = "Invalid email format")
        @Size(max = 100)
        String ownerEmail
) {}
