package com.arkho.fleet.controller;

import com.arkho.fleet.domain.RegistrationRequest;
import com.arkho.fleet.dto.request.CreateRegistrationRequest;
import com.arkho.fleet.dto.response.CreateRegistrationResponse;
import com.arkho.fleet.dto.response.RegistrationResponse;
import com.arkho.fleet.dto.response.UploadUrlResponse;
import com.arkho.fleet.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/solicitud")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<CreateRegistrationResponse> createRegistration(
            @Valid @RequestBody CreateRegistrationRequest request) {

        UUID id = registrationService.createRegistration(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateRegistrationResponse(id));
    }
    @GetMapping
    public ResponseEntity<Page<RegistrationResponse>> findList(Pageable pageable) {

        Page<RegistrationResponse> response =
                registrationService.findAll(pageable)
                        .map(s -> new RegistrationResponse(
                                s.getId(),
                                s.getPlate(),
                                s.getBrand(),
                                s.getModel(),
                                s.getYear(),
                                s.getOwnerName(),
                                s.getOwnerEmail(),
                                s.getStatus()
                        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponse> getById(@PathVariable UUID id) {

        RegistrationRequest registration = registrationService.getById(id);

        RegistrationResponse response = new RegistrationResponse(
                registration.getId(),
                registration.getPlate(),
                registration.getBrand(),
                registration.getModel(),
                registration.getYear(),
                registration.getOwnerName(),
                registration.getOwnerEmail(),
                registration.getStatus()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/documentos/upload-url")
    public ResponseEntity<UploadUrlResponse> generateUploadUrl(
            @PathVariable UUID id) {

        String url = registrationService.generateUploadUrl(id);

        return ResponseEntity.ok(new UploadUrlResponse(url));
    }

}
