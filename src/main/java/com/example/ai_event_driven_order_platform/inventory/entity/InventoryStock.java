
package com.example.ai_event_driven_order_platform.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory_stock")
public class InventoryStock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String productCode;

  @Column(nullable = false)
  private int availableQuantity;

  @Version
  private Long version;

  protected InventoryStock() {
  }

  public InventoryStock(String productCode, int availableQuantity) {
    this.productCode = productCode;
    this.availableQuantity = availableQuantity;
  }

  public Long getId() {
    return id;
  }

  public String getProductCode() {
    return productCode;
  }

  public int getAvailableQuantity() {
    return availableQuantity;
  }

  public Long getVersion() {
    return version;
  }

  public void reserve(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException(
        "Quantity must be greater than zero"
      );
    }

    if (availableQuantity < quantity) {
      throw new IllegalStateException(
        "Insufficient stock for product: " + productCode
      );
    }

    availableQuantity -= quantity;
  }
}
