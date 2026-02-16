package com.arkho.fleet.service;

import com.arkho.fleet.domain.RegistrationRequest;
import com.arkho.fleet.dto.request.CreateRegistrationRequest;
import com.arkho.fleet.event.RegistrationCreatedEvent;
import com.arkho.fleet.event.RegistrationEventPublisher;
import com.arkho.fleet.exception.BusinessException;
import com.arkho.fleet.repository.RegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Year;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository repository;

    @Mock
    private RegistrationEventPublisher eventPublisher;

    @InjectMocks
    private RegistrationService service;

    @Test
    void shouldCreateRegistrationSuccessfully() {

        CreateRegistrationRequest request =
                new CreateRegistrationRequest(
                        "ABC123",
                        "Toyota",
                        "Corolla",
                        2020,
                        "John Doe",
                        "john@mail.com"
                );

        RegistrationRequest savedEntity =
                new RegistrationRequest(
                        "ABC123",
                        "Toyota",
                        "Corolla",
                        2020,
                        "John Doe",
                        "john@mail.com"
                );


        when(repository.save(any(RegistrationRequest.class)))
                .thenReturn(savedEntity);

        UUID result = service.createRegistration(request);

        assertNotNull(result);
        assertEquals(savedEntity.getId(), result);

        verify(repository).save(any(RegistrationRequest.class));

        ArgumentCaptor<RegistrationCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(RegistrationCreatedEvent.class);

        verify(eventPublisher).publishAfterCommit(eventCaptor.capture());

        RegistrationCreatedEvent publishedEvent = eventCaptor.getValue();

        assertEquals(savedEntity.getId(), publishedEvent.id());
        assertEquals("ABC123", publishedEvent.plate());
    }

    @Test
    void shouldThrowExceptionWhenPlateAlreadyExists() {

        CreateRegistrationRequest request =
                new CreateRegistrationRequest(
                        "ABC123",
                        "Toyota",
                        "Corolla",
                        2020,
                        "John Doe",
                        "john@mail.com"
                );

        when(repository.save(any(RegistrationRequest.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.createRegistration(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));

        verify(eventPublisher, never())
                .publishAfterCommit(any());
    }

    @Test
    void shouldThrowExceptionWhenYearIsFuture() {

        int futureYear = Year.now().getValue() + 1;

        CreateRegistrationRequest request =
                new CreateRegistrationRequest(
                        "ABC123",
                        "Toyota",
                        "Corolla",
                        futureYear,
                        "John Doe",
                        "john@mail.com"
                );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.createRegistration(request)
        );

        assertEquals("Vehicle year cannot be in the future", exception.getMessage());

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishAfterCommit(any());
    }

    @Test
    void shouldThrowExceptionWhenPlateFormatIsInvalid() {

        CreateRegistrationRequest request =
                new CreateRegistrationRequest(
                        "INVALID!!!",
                        "Toyota",
                        "Corolla",
                        2020,
                        "John Doe",
                        "john@mail.com"
                );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.createRegistration(request)
        );

        assertEquals("Formato de patente inválido", exception.getMessage());

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishAfterCommit(any());
    }
}
