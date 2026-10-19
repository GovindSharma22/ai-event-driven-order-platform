
package com.example.ai_event_driven_order_platform.common;

import com.example.ai_event_driven_order_platform.order.service.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleOrderNotFound(
    OrderNotFoundException exception) {

    Map<String, Object> body = Map.of(
      "timestamp", Instant.now().toString(),
      "status", HttpStatus.NOT_FOUND.value(),
      "error", "Not Found",
      "message", exception.getMessage()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationErrors(
    MethodArgumentNotValidException exception) {

    Map<String, String> fieldErrors = new HashMap<>();

    exception.getBindingResult().getFieldErrors().forEach(error ->
      fieldErrors.putIfAbsent(
        error.getField(),
        error.getDefaultMessage()
      )
    );

    Map<String, Object> body = Map.of(
      "timestamp", Instant.now().toString(),
      "status", HttpStatus.BAD_REQUEST.value(),
      "error", "Bad Request",
      "message", "Request validation failed",
      "fieldErrors", fieldErrors
    );

    return ResponseEntity.badRequest().body(body);
  }
}
