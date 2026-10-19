package com.example.ai_event_driven_order_platform.order.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(Long id) {
    super("Order not found with id: " + id);
  }
}
