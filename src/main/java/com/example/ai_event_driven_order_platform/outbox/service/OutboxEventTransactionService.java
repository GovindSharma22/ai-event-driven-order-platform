package com.example.ai_event_driven_order_platform.outbox.service;

import com.example.ai_event_driven_order_platform.outbox.entity.OutboxEvent;
import com.example.ai_event_driven_order_platform.outbox.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OutboxEventTransactionService {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventTransactionService(
            OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public Optional<ClaimedOutboxEvent> claimEvent(Long eventId) {
        return outboxEventRepository.findPendingByIdForUpdate(eventId)
                .map(event -> {
                    event.markProcessing();
                    event.incrementAttempts();

                    return new ClaimedOutboxEvent(
                            event.getId(),
                            event.getPayload());
                });
    }

    @Transactional
    public void markPublished(Long eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox event not found: " + eventId));

        event.markPublished();
        event.releaseForRetry();
    }

    @Transactional
    public void releaseForRetry(Long eventId) {
        outboxEventRepository.findById(eventId)
                .ifPresent(OutboxEvent::releaseForRetry);
    }

    public record ClaimedOutboxEvent(Long id, String payload) {
    }
}
