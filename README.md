# Staff scheduling

Service provides endpoints to manage employees' work schedules.

## Rest API documentation
    
    http://localhost:8080/v3/api-docs
    
REST API can be tested using Swagger:

    http://localhost:8080/swagger-ui.html

## Running tests

    ./gradlew test

## Build & Run

Prerequisites

    JDK17
    Gradle

Build project with Gradle

    ./gradlew build

Build docker image
    
    docker build -f src/main/docker/Dockerfile .

Run service & database with docker compose

    docker-compose -f src/main/docker/docker-compose.yml up -d