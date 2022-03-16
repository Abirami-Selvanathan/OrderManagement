## Order management
- Order management for Reading is good
## Table of contents
- [Setup](#setup)
- [Integration test](#integration)
- [Docker setup](#docker)
## Setup
- Run `CREATE DATABASE order_management;`
- Replace the `username` and `password` with your postgres credentials in `src/main/resources/application.properties`
## Integration test
- Run `CREATE DATABASE order_management_integration;`
- Replace the `username` and `password` with your postgres credentials in `src/test/resources/application.properties`
- ## Docker setup
- Replace <DATABASE_NAME>, <DATABASE_USERNAME> and <DATABASE_PASSWORD> in docker-compose.yml file
- Run `./gradlew clean build`
- Run `docker compose up --build`