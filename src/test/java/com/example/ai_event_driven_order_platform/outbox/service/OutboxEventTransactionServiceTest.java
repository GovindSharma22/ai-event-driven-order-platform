package com.example.ai_event_driven_order_platform.outbox.service;

import com.example.ai_event_driven_order_platform.outbox.entity.OutboxEvent;
import com.example.ai_event_driven_order_platform.outbox.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventTransactionServiceTest {

    @Mock
    private OutboxEventRepository repository;

    private OutboxEventTransactionService service;

    @BeforeEach
    void setUp() {
        service = new OutboxEventTransactionService(repository);
    }

    @Test
    void claimEventShouldMarkEventAsProcessing() {
        OutboxEvent event = new OutboxEvent(
                "Order", "101", "OrderCreated", "{\"orderId\":101}");

        when(repository.findPendingByIdForUpdate(1L))
                .thenReturn(Optional.of(event));

        var result = service.claimEvent(1L);

        assertTrue(result.isPresent());
        assertEquals("{\"orderId\":101}", result.get().payload());
        assertTrue(event.isProcessing());
        assertNotNull(event.getProcessingAt());
        assertEquals(1, event.getAttempts());
    }

    @Test
    void claimEventShouldReturnEmptyWhenEventCannotBeClaimed() {
        when(repository.findPendingByIdForUpdate(2L))
                .thenReturn(Optional.empty());

        var result = service.claimEvent(2L);

        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void markPublishedShouldSetPublishedTimestampAndReleaseEvent()
            throws Exception {
        OutboxEvent event = new OutboxEvent(
                "Order", "103", "OrderCreated", "{\"orderId\":103}");

        var processingMethod = OutboxEvent.class
                .getMethod("markProcessing");
        processingMethod.invoke(event);

        when(repository.findById(3L)).thenReturn(Optional.of(event));

        service.markPublished(3L);

        assertNotNull(event.getPublishedAt());
        assertFalse(event.isProcessing());
        assertNull(event.getProcessingAt());
    }

    @Test
    void releaseForRetryShouldClearProcessingState() {
        OutboxEvent event = new OutboxEvent(
                "Order", "104", "OrderCreated", "{\"orderId\":104}");
        event.markProcessing();

        when(repository.findById(4L)).thenReturn(Optional.of(event));

        service.releaseForRetry(4L);

        assertFalse(event.isProcessing());
        assertNull(event.getProcessingAt());
        assertNull(event.getPublishedAt());
    }
}
