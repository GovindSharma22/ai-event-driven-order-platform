
package com.example.ai_event_driven_order_platform.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateOrderRequest(

  @NotBlank
  @Size(max = 100)
  String customerId,

  @NotBlank
  @Size(max = 50)
  String productCode,

  @NotNull
  @Min(1)
  Integer quantity,

  @NotNull
  @DecimalMin("0.01")
  BigDecimal amount
) {
}
