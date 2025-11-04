# Blood Donation Management System

This is a web-based Blood Donation Management System built with Spring Boot.

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Running Locally

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install`
4. Run `mvn spring-boot:run`
5. Open http://localhost:8080 in your browser

## Deploying to Render

1. Create a new Web Service on Render
2. Connect your GitHub repository
3. Use the following build settings:
   - Build Command: `mvn clean install`
   - Start Command: `java -jar target/blood-donation-app-0.0.1-SNAPSHOT.jar`
4. Add the following environment variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `JAVA_VERSION=17`

## Features

- Register blood donors
- Search donors by name, blood group, location, or phone number
- View blood group statistics
- Responsive web interface
- Real-time updates