
package com.example.ai_event_driven_order_platform.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "inventory_reservations")
public class InventoryReservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "order_number", nullable = false)
  private String orderNumber;

  @Column(name = "reserved_at", nullable = false)
  private Instant reservedAt;

  protected InventoryReservation() {
  }

  public InventoryReservation(Long orderId, String orderNumber) {
    this.orderId = orderId;
    this.orderNumber = orderNumber;
    this.reservedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public Instant getReservedAt() {
    return reservedAt;
  }
}
