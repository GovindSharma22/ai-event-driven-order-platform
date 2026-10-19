# AI Event-Driven Order Platform

A Java 21 and Spring Boot application demonstrating event-driven order processing, inventory reservation, Kafka messaging, and the Transactional Outbox pattern.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA and Hibernate
- MySQL
- Apache Kafka
- Maven
- JUnit 5 and Mockito
- Docker

## Architecture

1. The client submits an order through the REST API.
2. The order and its `OrderCreated` outbox event are saved in one database transaction.
3. The scheduled outbox publisher publishes pending events to Kafka.
4. The inventory consumer processes events and reserves stock.
5. Orders become `CONFIRMED` when stock is available or `FAILED` when stock is insufficient.
6. Duplicate events are handled idempotently to prevent duplicate inventory reservations.

## Key Features

- REST API for creating and retrieving orders
- Request validation and centralized exception handling
- Transactional Outbox pattern for reliable event publication
- Kafka-based asynchronous processing
- Inventory reservation with atomic stock updates
- Duplicate-event protection
- Recovery of stale outbox processing claims
- Automated unit and MVC tests

## API Endpoints

### Create an order

`POST /api/orders`

Example request:

```json
{
  "customerId": "CUST-1001",
  "productCode": "PROD-100",
  "quantity": 2,
  "amount": 499.00
}
