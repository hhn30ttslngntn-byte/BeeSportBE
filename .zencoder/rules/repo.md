---
description: Repository Information Overview
alwaysApply: true
---

# Sport_BE Information

## Summary
**Sport_BE** is a Java-based backend application built with **Spring Boot**. The project appears to be in its early development stages, providing a foundation for a sports-related service. It uses **Maven** for dependency management and build automation.

## Structure
- **.mvn/**: Contains the Maven Wrapper configuration for consistent builds.
- **src/main/java/**: Contains the application source code, including the main entry point and servlet initialization.
- **src/main/resources/**: Stores configuration files such as `application.properties`.
- **src/test/java/**: Contains the test suite for the application.

## Language & Runtime
**Language**: Java  
**Version**: 17  
**Build System**: Maven  
**Package Manager**: Maven (mvnw)

## Dependencies
**Main Dependencies**:
- **Spring Boot Starter RestClient**: For making RESTful calls.
- **Spring Boot Starter WebMVC**: For building web applications, including RESTful services.
- **Lombok**: A library to reduce boilerplate code (e.g., getters, setters).

**Development Dependencies**:
- **Spring Boot DevTools**: Provides fast application restarts and LiveReload.
- **Spring Boot Starter Tomcat**: Embedded servlet container (provided scope).
- **Spring Boot Starter Test**: Support for testing Spring Boot applications.

## Build & Installation
```bash
# Clean and install dependencies
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

## Main Files & Resources
- **Entry Point**: [./src/main/java/com/example/sport_be/SportBeApplication.java](./src/main/java/com/example/sport_be/SportBeApplication.java)
- **Servlet Initializer**: [./src/main/java/com/example/sport_be/ServletInitializer.java](./src/main/java/com/example/sport_be/ServletInitializer.java)
- **Configuration**: [./src/main/resources/application.properties](./src/main/resources/application.properties)

## Testing
**Framework**: JUnit / Spring Boot Test  
**Test Location**: [./src/test/java/com/example/sport_be/](./src/test/java/com/example/sport_be/)  
**Naming Convention**: `*Tests.java`  

**Run Command**:
```bash
./mvnw test
```
