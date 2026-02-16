package com.arkho.fleet.service;

import com.arkho.fleet.domain.RegistrationRequest;
import com.arkho.fleet.dto.request.CreateRegistrationRequest;
import com.arkho.fleet.event.RegistrationCreatedEvent;
import com.arkho.fleet.event.RegistrationEventPublisher;
import com.arkho.fleet.exception.BusinessException;
import com.arkho.fleet.exception.ResourceNotFoundException;
import com.arkho.fleet.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository repository;
    private final RegistrationEventPublisher eventPublisher;
    private final StorageService storageService;

    private static final Pattern PATENTE_PATTERN =
            Pattern.compile("^[A-Z0-9-]{6,10}$");

    public UUID createRegistration(CreateRegistrationRequest request) {

        String plate = request.plate().trim().toUpperCase();
        String brand = request.brand().trim();
        String model = request.model().trim();
        String ownerName = request.ownerName().trim();
        String ownerEmail = request.ownerEmail().trim();
        int year = request.year();

        if (!PATENTE_PATTERN.matcher(plate).matches()) {
            throw new BusinessException("Formato de patente inválido");
        }

        if (year > Year.now().getValue()) {
            throw new BusinessException("Vehicle year cannot be in the future");
        }

        if (repository.existsByPlate(plate)) {
            throw new BusinessException("Plate already exists");
        }

        try {

            RegistrationRequest registration = new RegistrationRequest(
                    plate,
                    brand,
                    model,
                    year,
                    ownerName,
                    ownerEmail
            );

            RegistrationRequest saved = repository.save(registration);

            eventPublisher.publishAfterCommit(
                    new RegistrationCreatedEvent(
                            saved.getId(),
                            saved.getPlate(),
                            saved.getCreatedAt()
                    )
            );

            return saved.getId();

        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Plate already exists");
        }
    }

    @Transactional(readOnly = true)
    public RegistrationRequest getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Registration not found"));
    }

    @Transactional(readOnly = true)
    public Page<RegistrationRequest> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public String generateUploadUrl(UUID id) {

        RegistrationRequest registrationFound = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Registration not found"));

        String key = "registrations/" + registrationFound.getId() + "/document.pdf";

        return storageService.generatePresignedUploadUrl(key);
    }
}
