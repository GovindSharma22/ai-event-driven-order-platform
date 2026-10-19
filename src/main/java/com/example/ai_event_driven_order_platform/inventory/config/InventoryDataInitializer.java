
package com.example.ai_event_driven_order_platform.inventory.config;

import com.example.ai_event_driven_order_platform.inventory.entity.InventoryStock;
import com.example.ai_event_driven_order_platform.inventory.repository.InventoryStockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryDataInitializer {

  @Bean
  CommandLineRunner initializeInventory(
    InventoryStockRepository repository) {

    return args -> {
      if (repository.findByProductCode("PROD-100").isEmpty()) {
        repository.save(
          new InventoryStock("PROD-100", 10)
        );
      }
    };
  }
}
