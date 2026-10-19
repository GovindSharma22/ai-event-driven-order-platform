package com.example.ai_event_driven_order_platform.order.service;

import com.example.ai_event_driven_order_platform.order.dto.CreateOrderRequest;
import com.example.ai_event_driven_order_platform.order.entity.Order;
import com.example.ai_event_driven_order_platform.order.event.OrderCreated;
import com.example.ai_event_driven_order_platform.order.repository.OrderRepository;
import com.example.ai_event_driven_order_platform.outbox.entity.OutboxEvent;
import com.example.ai_event_driven_order_platform.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;

  public OrderService(
    OrderRepository orderRepository,
    OutboxEventRepository outboxEventRepository,
    ObjectMapper objectMapper) {
    this.orderRepository = orderRepository;
    this.outboxEventRepository = outboxEventRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public Order createOrder(CreateOrderRequest request) {
    String orderNumber = "ORD-" +
      UUID.randomUUID().toString()
        .substring(0, 8).toUpperCase();

    Order order = orderRepository.save(
      new Order(
        orderNumber,
        request.customerId(),
        request.productCode(),
        request.quantity(),
        request.amount()
      )
    );

    OrderCreated event = new OrderCreated(
      order.getId(),
      order.getOrderNumber(),
      order.getCustomerId(),
      order.getProductCode(),
      order.getQuantity(),
      order.getAmount(),
      Instant.now()
    );

    String payload;
    try {
      payload = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException exception) {
      throw new IllegalStateException(
        "Failed to serialize OrderCreated event", exception);
    }

    outboxEventRepository.save(
      new OutboxEvent(
        "Order",
        order.getId().toString(),
        "OrderCreated",
        payload
      )
    );

    return order;
  }

  @Transactional(readOnly = true)
  public Order getOrder(Long id) {
    return orderRepository.findById(id)
      .orElseThrow(() -> new OrderNotFoundException(id));
  }
}
