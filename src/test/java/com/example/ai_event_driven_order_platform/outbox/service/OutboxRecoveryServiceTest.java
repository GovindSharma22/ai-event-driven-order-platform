package com.example.ai_event_driven_order_platform.outbox.service;

import com.example.ai_event_driven_order_platform.outbox.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRecoveryServiceTest {

    @Mock
    private OutboxEventRepository repository;

    @Test
    void recoverStaleEventsShouldUseFiveMinuteCutoff() {
        OutboxRecoveryService service = new OutboxRecoveryService(repository);
        Instant before = Instant.now();

        when(repository.releaseStaleProcessingEvents(any(Instant.class)))
                .thenReturn(2);

        service.recoverStaleEvents();

        Instant after = Instant.now();

        verify(repository).releaseStaleProcessingEvents(argThat(cutoff ->
                !cutoff.isBefore(before.minus(5, ChronoUnit.MINUTES))
                        && !cutoff.isAfter(after.minus(5, ChronoUnit.MINUTES))
        ));
    }

    @Test
    void recoverStaleEventsShouldHandleNoStaleEvents() {
        OutboxRecoveryService service = new OutboxRecoveryService(repository);

        when(repository.releaseStaleProcessingEvents(any(Instant.class)))
                .thenReturn(0);

        service.recoverStaleEvents();

        verify(repository, times(1))
                .releaseStaleProcessingEvents(any(Instant.class));
    }
}
