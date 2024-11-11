# Bank Solution Application

## Project Description
 Simple REST API for managing accounts. 
 
## Features
- **Account Management**: Create, update, and query user accounts.
- **Money Transfers**: Transfer money between accounts with validation.
- **Test Coverage**: Code coverage is tracked and reported using JaCoCo.
- **Dockerized**: The application can be built and run using Docker for easy deployment.

## Tools and Libraries Used

### **Backend Technologies**
- **Java 17**: The primary programming language for the backend.
- **Spring Boot**: Framework for building Java-based web applications and microservices.
- **Maven**: Build automation tool for managing dependencies and building the project.
- **Spring Data JPA**: ORM framework for database interaction.
- **H2 Database**: Lightweight, in-memory database for testing and development purposes.

### **Testing and Code Quality**
- **JUnit 5**: Testing framework for writing unit and integration tests.
- **JaCoCo**: Code coverage library to ensure quality and coverage of tests.
- **Mockito**: Framework for creating mock objects in tests.

### **Containerization**
- **Docker**: Platform for packaging the application into containers.
- **Docker Compose**: Tool for defining and running multi-container Docker applications.

### **Web Server (test reports)**
- **http-server**: Simple, zero-config Node.js HTTP server that serves static files.

## Instructions for Installation and Usage

### **Prerequisites**
- Docker and Docker Compose installed on your machine.
- Java 17 installed (if building locally without Docker).
- Maven installed for local builds.

### **Run with docker**
Start by cloning the repository to your local machine.
Enter the project dir after project cloned.

```bash
git clone https://github.com/SpaceUrgent/banking-service.git
cd banking-service
```

Build and run services, including web server for test reports.

```bash
docker-compose up --build
```
The applications REST API will be available at: http://localhost:8080

The JaCoCo test report can be accessed at: http://localhost:8081/jacoco

### **Run locally**
Start by cloning the repository to your local machine.
Enter the project dir after project cloned.

```bash
git clone https://github.com/SpaceUrgent/banking-service.git
cd banking-service
```
Build project using Maven

```bash
mvn clean package
```

Run the Application

```bash
java -jar target/app.jar
```

The applications REST API will be available at: http://localhost:8080

The JaCoCo test report is served via and can be accessed at: http://localhost:63342/banking-service/target/site/jacoco/index.html


## API Specification


| **Endpoint** | **Method** | **Description** | **Request Parameters** | **Response** | **Response Body**        |
|--------------|------------|-----------------|------------------------|--------------|--------------------------|
| `/api/accounts` | `POST` | Create a new account | `balance` (optional): The initial balance for the account (default: `0.00`) | `201 Created` | `Account details`        |
| `/api/accounts` | `GET` | Retrieve all accounts | None | `200 OK` | `List of accounts`  |
| `/api/accounts/{accountNumber}` | `GET` | Retrieve account details by account number | `accountNumber`: The account number to fetch details for | `200 OK` | `Account details`        |
| `/api/accounts/{accountNumber}/deposit` | `POST` | Deposit funds into an account | `accountNumber`: The account number<br>`amount`: The deposit amount | `200 OK` | `Account details` (Updated) |
| `/api/accounts/{accountNumber}/withdraw` | `POST` | Withdraw funds from an account | `accountNumber`: The account number<br>`amount`: The withdrawal amount | `200 OK` | `Account details` (Updated) |
| `/api/accounts/{sourceAccountNumber}/transfer` | `POST` | Transfer funds between two accounts | `sourceAccountNumber`: The account number to transfer from<br>`targetAccountNumber`: The account number to transfer to<br>`amount`: The transfer amount | `200 OK` | `Account вetail` (Updated) |

---
- *Account details*
```json
{
  "accountNumber": "26000000031012",
  "currency" : "UAH",
  "balance": 1000.00
}
```

- *Accounts list*
```json
{
  "data": [
    {
      "accountNumber": "26000000031012", 
      "currency" : "UAH"
    },
    {
      "accountNumber": "26000000031002",
      "currency" : "UAH"
    }
  ]
}
```


### **Error Handling**

| **Error Code** | **Error Description** | **Possible Cause** | **Response Body** |
|----------------|-----------------------|--------------------|-------------------|
| `400 Bad Request` | Invalid or missing parameters | Missing `amount`, invalid `accountNumber`, or amount exceeds balance | `{"status":400, "message":"Amount exceeds balance", "path":"/api/accounts/123456789/withdraw"}` |
| `404 Not Found` | Account not found | Account with the provided account number does not exist | `{"status":404, "message":"Account with number '123456789' not found", "path":"/api/accounts/123456789"}` |

---

