package com.arkho.fleet.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    @Override
    public String generatePresignedUploadUrl(String key) {

        // Simulación de URL firmada
        return "https://fake-s3.amazonaws.com/" + key +
                "?signature=" + UUID.randomUUID() +
                "&expires=" + Instant.now().plusSeconds(900);
    }
}
