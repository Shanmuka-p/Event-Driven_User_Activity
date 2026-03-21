# Event-Driven User Activity Tracking System

## Overview

This project is a robust backend service designed to track user activities (e.g., login, logout, page view) using an event-driven architecture. The system consists of two main services:

- **Producer Service**: A RESTful API that receives user activity events and publishes them to a RabbitMQ message queue.
- **Consumer Service**: A standalone service that consumes events from the message queue, processes them, and stores them in a MySQL database.

The primary goal is to build a decoupled, scalable, and resilient system for handling real-time data streams, which is a common requirement in modern microservices architectures.

## Architecture

The system follows a classic event-driven, publish-subscribe pattern:

1.  A client sends a `POST` request with user activity data to the **Producer Service**.
2.  The **Producer Service** validates the incoming data and, if valid, publishes it as a message to a **RabbitMQ** queue.
3.  The **Consumer Service**, which is subscribed to the queue, receives the message.
4.  The **Consumer Service** processes the event and persists the data into a **MySQL** database.

This asynchronous communication model decouples the services, allowing them to scale independently and making the overall system more resilient to failures.

![Architecture Diagram](https://i.imgur.com/example.png) <!-- Placeholder for an architecture diagram -->

## Features

- **Event-Driven Architecture**: Utilizes RabbitMQ for asynchronous communication between services.
- **RESTful API**: A clean and robust API for tracking user activity events.
- **Asynchronous Processing**: The producer does not wait for the consumer to process the event, resulting in low latency for the client.
- **Data Persistence**: Stores user activity data in a MySQL database.
- **Containerized**: Fully containerized using Docker and orchestrated with Docker Compose for easy setup and deployment.
- **Health Checks**: Both services expose a `/health` endpoint to monitor their status.
- **Scalability**: The decoupled nature of the architecture allows for independent scaling of the producer and consumer services.
- **Resilience**: The system is designed to be resilient to transient failures.

## Technology Stack

- **Backend**: Java with Spring Boot
- **Database**: MySQL
- **Message Broker**: RabbitMQ
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) installed on your local machine.
- A Git client to clone the repository.
- An API client like [Postman](https://www.postman.com/) or `curl` to test the API.

## Getting Started

Follow these steps to get the project up and running on your local machine.

### 1. Clone the Repository

```bash
git clone https://github.com/Shanmuka-p/Event-Driven_User_Activity.git
cd Event-Driven_User_Activity
```

### 2. Configure Environment Variables

Create a `.env` file in the root of the project by copying the example file:

```bash
cp .env.example .env
```

The `.env` file contains all the necessary environment variables for the services to run, such as database credentials and RabbitMQ connection details. You can modify these values if needed, but the defaults are configured to work with the `docker-compose.yml` setup.

### 3. Build and Run the Application

Once Docker is running, you can build and start all the services using Docker Compose:

```bash
docker-compose up --build -d
```

This command will:

- Build the Docker images for the `producer-service` and `consumer-service`.
- Start the containers for all services (`producer-service`, `consumer-service`, `rabbitmq`, and `mysql`) in detached mode (`-d`).
- Create the `user_activities` table in the database using the `db/init.sql` script.

You can check the status of the running containers with:

```bash
docker-compose ps
```

You can also view the logs for a specific service:

```bash
docker-compose logs -f <service_name>  # e.g., producer-service
```

## API Endpoint

### Track User Activity

- **Endpoint**: `POST /api/v1/events/track`
- **Description**: Receives a user activity event and publishes it to the message queue.
- **Request Body**:

      ```bash
        curl -i -X POST http://localhost:9000/api/v1/events/track \

        -H "Content-Type: application/json" \
        -d '{
            "user_id": 999,
            "event_type": "checkout_button_click",
            "timestamp": "2026-03-20T21:00:00Z",
            "metadata": {"cart_value": 150}
          }'
     ```

- **Success Response**:
  - **Code**: `202 Accepted`
- **Error Response**:
  - **Code**: `400 Bad Request`
  - **Content**: An error message indicating what was wrong with the request.

  ```json
  {
    "error": "Invalid payload",
    "details": "user_id is a required field"
  }
  ```

## Database Schema

The `user_activities` table in the `user_activity_db` database has the following schema:

```sql
CREATE TABLE IF NOT EXISTS user_activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    metadata JSON
);
```

## Running Tests

To run the automated tests for each service, you can use `docker-compose exec`:

### Producer Service Tests

```bash
docker-compose exec producer-service mvn test
```

### Consumer Service Tests

```bash
docker-compose exec consumer-service mvn test
```
