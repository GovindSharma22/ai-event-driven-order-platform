
package com.example.ai_event_driven_order_platform.order.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String orderNumber;

  @Column(nullable = false)
  private String customerId;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;
  @Column(nullable = false, length = 50)
  private String productCode;

  @Column(nullable = false)
  private Integer quantity;

  protected Order() {}


  public Order(
    String orderNumber,
    String customerId,
    String productCode,
    Integer quantity,
    BigDecimal amount) {

    this.orderNumber = orderNumber;
    this.customerId = customerId;
    this.productCode = productCode;
    this.quantity = quantity;
    this.amount = amount;
    this.status = OrderStatus.CREATED;
    this.createdAt = LocalDateTime.now();
  }


  public Long getId() { return id; }
  public String getOrderNumber() { return orderNumber; }
  public String getCustomerId() { return customerId; }
  public BigDecimal getAmount() { return amount; }
  public OrderStatus getStatus() { return status; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public String getProductCode() {
    return productCode;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
}
