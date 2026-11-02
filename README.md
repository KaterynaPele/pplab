# Car Service Management System 

## Requirements
- **Java**: 17 or higher
- **Build Tool**: Apache Maven 3.8+

## How to Build and Run

### 1. Compile the Project
```bash
mvn clean compile
```

### 2. Run all Unit Tests
```bash
.\mvnw test
```

### 3. Run the Demonstration App (CarServiceDemo)
```bash
mvn exec:java -Dexec.mainClass="carservice.CarServiceDemo"
```