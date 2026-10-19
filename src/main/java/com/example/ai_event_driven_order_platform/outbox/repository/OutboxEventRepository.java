package com.example.ai_event_driven_order_platform.outbox.repository;

import com.example.ai_event_driven_order_platform.outbox.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent>
    findTop100ByPublishedAtIsNullAndProcessingFalseOrderByIdAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT e
            FROM OutboxEvent e
            WHERE e.id = :id
              AND e.publishedAt IS NULL
              AND e.processing = false
            """)
    Optional<OutboxEvent> findPendingByIdForUpdate(
            @Param("id") Long id);

    @Modifying
    @Query("""
            UPDATE OutboxEvent e
            SET e.processing = false,
                e.processingAt = null
            WHERE e.publishedAt IS NULL
              AND e.processing = true
              AND e.processingAt < :cutoff
            """)
    int releaseStaleProcessingEvents(@Param("cutoff") Instant cutoff);
}
