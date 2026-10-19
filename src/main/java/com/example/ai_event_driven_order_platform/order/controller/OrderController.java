
package com.example.ai_event_driven_order_platform.order.controller;

import com.example.ai_event_driven_order_platform.order.dto.CreateOrderRequest;
import com.example.ai_event_driven_order_platform.order.entity.Order;
import com.example.ai_event_driven_order_platform.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
    return orderService.createOrder(request);
  }

  @GetMapping("/{id}")
  public Order getOrder(@PathVariable Long id) {
    return orderService.getOrder(id);
  }
}
