package com.example.ai_event_driven_order_platform.order.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreated(
  Long orderId,
  String orderNumber,
  String customerId,
  String productCode,
  Integer quantity,
  BigDecimal amount,
  Instant occurredAt
) {
}
