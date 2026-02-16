package com.arkho.fleet.service;

public interface StorageService {
    String generatePresignedUploadUrl(String key);
}