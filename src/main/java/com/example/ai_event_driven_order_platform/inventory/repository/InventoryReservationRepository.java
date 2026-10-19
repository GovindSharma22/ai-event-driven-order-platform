package com.example.ai_event_driven_order_platform.inventory.repository;

import com.example.ai_event_driven_order_platform.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository
  extends JpaRepository<InventoryReservation, Long> {

  boolean existsByOrderId(Long orderId);
}
