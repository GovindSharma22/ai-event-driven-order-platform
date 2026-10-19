
package com.example.ai_event_driven_order_platform.inventory.messaging;

import com.example.ai_event_driven_order_platform.inventory.repository.InventoryReservationRepository;
import com.example.ai_event_driven_order_platform.inventory.repository.InventoryStockRepository;
import com.example.ai_event_driven_order_platform.order.repository.OrderRepository;
import com.example.ai_event_driven_order_platform.order.event.OrderCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.*;
import com.example.ai_event_driven_order_platform.order.entity.Order;
import com.example.ai_event_driven_order_platform.order.entity.OrderStatus;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private InventoryReservationRepository reservationRepository;

  @Mock
  private InventoryStockRepository stockRepository;

  private OrderCreatedConsumer consumer;

  @BeforeEach
  void setUp() {
    consumer = new OrderCreatedConsumer(
      orderRepository,
      reservationRepository,
      stockRepository
    );
  }



  @Test
  void duplicateEventShouldNotReserveStockAgain() {
    OrderCreated event = new OrderCreated(
      10L,
      "ORD-TEST-10",
      "CUST-TEST",
      "PROD-100",
      2,
      new BigDecimal("200.00"),
      Instant.now()
    );

    Order order = new Order(
      "ORD-TEST-10",
      "CUST-TEST",
      "PROD-100",
      2,
      new BigDecimal("200.00")
    );

    when(orderRepository.findByIdForUpdate(10L))
      .thenReturn(Optional.of(order));

    when(reservationRepository.existsByOrderId(10L))
      .thenReturn(true);

    consumer.consume(event);

    assertEquals(OrderStatus.CREATED, order.getStatus());

    verify(orderRepository).findByIdForUpdate(10L);
    verify(reservationRepository).existsByOrderId(10L);
    verifyNoInteractions(stockRepository);
    verify(orderRepository, never()).save(any());
    verify(reservationRepository, never()).save(any());
  }


  @Test
  void insufficientStockShouldMarkOrderFailed() {
    OrderCreated event = new OrderCreated(
      11L,
      "ORD-TEST-11",
      "CUST-TEST",
      "PROD-100",
      100,
      new BigDecimal("10000.00"),
      Instant.now()
    );

    Order order = new Order(
      "ORD-TEST-11",
      "CUST-TEST",
      "PROD-100",
      100,
      new BigDecimal("10000.00")
    );

    when(reservationRepository.existsByOrderId(11L))
      .thenReturn(false);

    when(orderRepository.findByIdForUpdate(11L))
      .thenReturn(Optional.of(order));

    when(stockRepository.reserveIfAvailable("PROD-100", 100))
      .thenReturn(0);

    consumer.consume(event);

    assertEquals(OrderStatus.FAILED, order.getStatus());

    verify(orderRepository).save(order);
    verify(reservationRepository, never()).save(any());
  }

  @Test
  void sufficientStockShouldConfirmOrderAndCreateReservation() {
    OrderCreated event = new OrderCreated(
      12L,
      "ORD-TEST-12",
      "CUST-TEST",
      "PROD-100",
      2,
      new BigDecimal("200.00"),
      Instant.now()
    );

    Order order = new Order(
      "ORD-TEST-12",
      "CUST-TEST",
      "PROD-100",
      2,
      new BigDecimal("200.00")
    );

    when(reservationRepository.existsByOrderId(12L))
      .thenReturn(false);

    when(orderRepository.findByIdForUpdate(12L))
      .thenReturn(Optional.of(order));

    when(stockRepository.reserveIfAvailable("PROD-100", 2))
      .thenReturn(1);

    consumer.consume(event);

    assertEquals(OrderStatus.CONFIRMED, order.getStatus());

    verify(reservationRepository).save(any());
    verify(orderRepository, atLeastOnce()).save(order);
  }
}
