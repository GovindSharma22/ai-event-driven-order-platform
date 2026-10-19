package com.example.ai_event_driven_order_platform.outbox.service;

import com.example.ai_event_driven_order_platform.outbox.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class OutboxRecoveryService {

    private static final long STALE_AFTER_MINUTES = 5;

    private final OutboxEventRepository outboxEventRepository;

    public OutboxRecoveryService(
            OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void recoverStaleEvents() {
        Instant cutoff = Instant.now()
                .minus(STALE_AFTER_MINUTES, ChronoUnit.MINUTES);

        int recovered = outboxEventRepository
                .releaseStaleProcessingEvents(cutoff);

        if (recovered > 0) {
            System.out.println(
                    "Recovered " + recovered + " stale outbox event(s).");
        }
    }
}
