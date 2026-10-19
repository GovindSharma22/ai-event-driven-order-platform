
package com.example.ai_event_driven_order_platform.order.messaging;

import com.example.ai_event_driven_order_platform.order.event.OrderCreated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

  private static final String TOPIC = "order-created";

  private final KafkaTemplate<String, OrderCreated> kafkaTemplate;

  public OrderEventProducer(
    KafkaTemplate<String, OrderCreated> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(OrderCreated event) {
    kafkaTemplate.send(TOPIC, event.orderNumber(), event);
  }
}
