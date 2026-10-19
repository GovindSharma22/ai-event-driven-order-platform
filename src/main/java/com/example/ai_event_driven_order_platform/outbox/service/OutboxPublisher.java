package com.example.ai_event_driven_order_platform.outbox.service;

import com.example.ai_event_driven_order_platform.order.event.OrderCreated;
import com.example.ai_event_driven_order_platform.outbox.entity.OutboxEvent;
import com.example.ai_event_driven_order_platform.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "order-created";

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventTransactionService transactionService;
    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            OutboxEventTransactionService transactionService,
            KafkaTemplate<String, OrderCreated> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.transactionService = transactionService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents =
                outboxEventRepository
                        .findTop100ByPublishedAtIsNullAndProcessingFalseOrderByIdAsc();

        for (OutboxEvent pending : pendingEvents) {
            var claimed = transactionService.claimEvent(pending.getId());

            if (claimed.isEmpty()) {
                continue;
            }

            var eventSnapshot = claimed.get();

            try {
                OrderCreated event = objectMapper.readValue(
                        eventSnapshot.payload(), OrderCreated.class);

                kafkaTemplate.send(
                                TOPIC,
                                event.orderNumber(),
                                event)
                        .get();

                transactionService.markPublished(eventSnapshot.id());

            } catch (Exception exception) {
                transactionService.releaseForRetry(eventSnapshot.id());

                System.err.println(
                        "Failed to publish outbox event "
                                + eventSnapshot.id()
                                + ": " + exception.getMessage());
            }
        }
    }
}
