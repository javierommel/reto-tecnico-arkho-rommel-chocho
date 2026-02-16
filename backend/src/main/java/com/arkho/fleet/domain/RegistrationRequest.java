package com.arkho.fleet.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

@Entity
@Table(
        name = "registration_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_registration_plate", columnNames = "plate")
        },
        indexes = {
                @Index(name = "idx_registration_plate", columnList = "plate")
        }
)
public class RegistrationRequest {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 10)
    private String plate;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 100)
    private String ownerName;

    @Column(nullable = false, length = 100)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RegistrationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    protected RegistrationRequest() {
        // For JPA
    }

    public RegistrationRequest(String plate,
                               String brand,
                               String model,
                               Integer year,
                               String ownerName,
                               String ownerEmail) {

        this.id = UUID.randomUUID();
        this.plate = normalizePlate(plate);
        this.brand = normalizeText(brand);
        this.model = normalizeText(model);
        this.year = year;
        this.ownerName = normalizeText(ownerName);
        this.ownerEmail = normalizeText(ownerEmail);
        this.status = RegistrationStatus.PENDING;
        this.createdAt = LocalDateTime.now();

        validateInvariants();
    }

    private String normalizePlate(String plate) {
        if (plate == null) return null;
        return plate.replaceAll("\\s+", "")
                .toUpperCase()
                .trim();
    }

    private String normalizeText(String text) {
        if (text == null) return null;
        return text.trim();
    }

    private void validateInvariants() {
        if (plate == null || plate.isBlank()) {
            throw new IllegalArgumentException("Plate cannot be empty");
        }

        int currentYear = Year.now().getValue();
        if (year != null && year > currentYear) {
            throw new IllegalArgumentException("Vehicle year cannot be in the future");
        }
    }

    public UUID getId() { return id; }
    public String getPlate() { return plate; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerEmail() { return ownerEmail; }
    public RegistrationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
