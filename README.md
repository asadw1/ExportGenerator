# ExportGenerator

ExportGenerator is a Spring Boot application designed to generate Excel exports with various columns of dummy data. The application provides an API endpoint to trigger the export process and download the generated Excel file.

## Features

- Generate Excel exports with customizable columns
- Supports up to 100,000 rows of data
- Includes various types of columns (email addresses, locations, misc values)
- API endpoint to trigger the export and download the Excel file
- Well-structured project with unit tests for controllers and services
- **Security**: Uses Bearer Authentication with JWT tokens for secure access to API endpoints

## Technologies Used

- Java
- Spring Boot
- Apache POI (for Excel operations)
- Jakarta EE
- Maven
- JUnit and Mockito (for testing)

## Setup and Configuration

### Prerequisites

- Java 11 or higher
- Maven
- An IDE of your choice (e.g., IntelliJ IDEA, Eclipse)
- A web browser to access the Swagger UI (optional)

### Clone the Repository

```bash
git clone https://github.com/yourusername/ExportGenerator.git
cd ExportGenerator
```

### Build and Run the Application

To build and run the application, use the following Maven commands:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Swagger UI

Optionally, you can use Swagger UI to explore and test the API endpoints. The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Project Structure

- `src/main/java/com/exportgenerator/demo` - Main application code
  - `controller` - API controllers
  - `services` - Service classes
  - `serviceinterfaces` - Service interfaces
- `src/test/java/com/exportgenerator/demo` - Unit tests
  - `controller` - Tests for controllers
  - `services` - Tests for services
- `src/main/resources` - Configuration files and resources
  - `application.properties` - Spring Boot configuration
  - `pokemon_names.json` - Dummy data for Pokémon names

