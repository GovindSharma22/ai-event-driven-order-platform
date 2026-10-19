package com.example.ai_event_driven_order_platform.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "JSON")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "processing", nullable = false)
    private boolean processing = false;

    @Column(name = "processing_at")
    private Instant processingAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.attempts = 0;
    }

    public Long getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isProcessing() {
        return processing;
    }

    public Instant getProcessingAt() {
        return processingAt;
    }

    public void markPublished() {
        this.publishedAt = Instant.now();
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void markProcessing() {
        this.processing = true;
        this.processingAt = Instant.now();
    }

    public void releaseForRetry() {
        this.processing = false;
        this.processingAt = null;
    }
}
