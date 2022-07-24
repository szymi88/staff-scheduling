# Staff scheduling

Service provides endpoints to manage employees' work schedules.

## Running tests
    ./gradlew test

## Build

Prerequisites

    JDK17
    Gradle

Build project with Gradle

    ./gradlew build

Build docker image
    
    docker build -f src/main/docker/Dockerfile .

Run service & database with docker compose

    docker-compose -f src/main/docker/docker-compose.yml up -d