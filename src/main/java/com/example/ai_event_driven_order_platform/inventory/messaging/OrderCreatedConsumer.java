
package com.example.ai_event_driven_order_platform.inventory.messaging;

import com.example.ai_event_driven_order_platform.inventory.entity.InventoryReservation;
import com.example.ai_event_driven_order_platform.inventory.entity.InventoryStock;
import com.example.ai_event_driven_order_platform.inventory.repository.InventoryReservationRepository;
import com.example.ai_event_driven_order_platform.inventory.repository.InventoryStockRepository;
import com.example.ai_event_driven_order_platform.order.entity.Order;
import com.example.ai_event_driven_order_platform.order.entity.OrderStatus;
import com.example.ai_event_driven_order_platform.order.event.OrderCreated;
import com.example.ai_event_driven_order_platform.order.repository.OrderRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCreatedConsumer {

  private final OrderRepository orderRepository;
  private final InventoryReservationRepository reservationRepository;
  private final InventoryStockRepository stockRepository;

  public OrderCreatedConsumer(
    OrderRepository orderRepository,
    InventoryReservationRepository reservationRepository,
    InventoryStockRepository stockRepository) {
    this.orderRepository = orderRepository;
    this.reservationRepository = reservationRepository;
    this.stockRepository = stockRepository;
  }


  @KafkaListener(topics = "order-created", groupId = "inventory-service")
  @Transactional
  public void consume(OrderCreated event) {
    Order order = orderRepository.findByIdForUpdate(event.orderId())
      .orElseThrow(() ->
        new IllegalStateException(
          "Order not found: " + event.orderId()));

    if (reservationRepository.existsByOrderId(event.orderId())) {
      return;
    }

    int updatedRows = stockRepository.reserveIfAvailable(
      event.productCode(),
      event.quantity()
    );

    if (updatedRows == 0) {
      order.setStatus(OrderStatus.FAILED);
      orderRepository.save(order);
      return;
    }

    reservationRepository.save(
      new InventoryReservation(
        event.orderId(),
        event.orderNumber()
      )
    );

    order.setStatus(OrderStatus.INVENTORY_RESERVED);
    orderRepository.save(order);

    order.setStatus(OrderStatus.CONFIRMED);
    orderRepository.save(order);
  }

}
