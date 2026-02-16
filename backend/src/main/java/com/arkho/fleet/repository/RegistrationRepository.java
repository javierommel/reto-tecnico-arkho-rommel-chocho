package com.arkho.fleet.repository;

import com.arkho.fleet.domain.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<RegistrationRequest, UUID> {

    boolean existsByPlate(String plate);

    Optional<RegistrationRequest> findByPlate(String plate);
}
