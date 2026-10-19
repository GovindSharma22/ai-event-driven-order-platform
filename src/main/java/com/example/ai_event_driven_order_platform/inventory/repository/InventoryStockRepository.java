
package com.example.ai_event_driven_order_platform.inventory.repository;

import com.example.ai_event_driven_order_platform.inventory.entity.InventoryStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface InventoryStockRepository
  extends JpaRepository<InventoryStock, Long> {

  Optional<InventoryStock> findByProductCode(String productCode);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
        SELECT s
        FROM InventoryStock s
        WHERE s.productCode = :productCode
        """)
  Optional<InventoryStock> findByProductCodeForUpdate(
    @Param("productCode") String productCode);
  @Modifying
  @Query("""
    UPDATE InventoryStock s
    SET s.availableQuantity = s.availableQuantity - :quantity,
        s.version = s.version + 1
    WHERE s.productCode = :productCode
      AND :quantity > 0
      AND s.availableQuantity >= :quantity
    """)
  int reserveIfAvailable(
    @Param("productCode") String productCode,
    @Param("quantity") int quantity
  );
}
