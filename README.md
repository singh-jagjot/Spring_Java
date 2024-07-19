# Notes Application

A Spring Boot project for saving notes and sharing notes with other users. The application uses JWT for authentication and includes rate limiting with Bucket4j.

## Features

- Save personal notes
- Share notes with other users
- JWT-based authentication
- Rate limiting using Bucket4j
- Full-text search using PostgreSQL's `tsvector`

## Technology Choices

### Backend Framework: Spring Boot

Spring Boot is chosen for its maturity and robustness as a backend framework. It offers a wide range of features for building enterprise-level applications and integrates seamlessly with various tools and libraries.

### Database: PostgreSQL

PostgreSQL is selected for its production-readiness and solid support for full-text search using `tsvector`.

### Rate Limiting: Bucket4j

Bucket4j is used for implementing rate limiting to control the rate of requests to the application. It provides a simple and efficient way to apply rate limits.

## Prerequisites

- JDK 21
- Maven
- PostgreSQL

## How to Run

1. **Clone the repository**:
   ```sh
   git clone <repository-url>

2. **Navigate to the project's root directory**:
   ```sh
   cd Spring_Java/Speer_Project
3. **Install JDK 21 and Maven** (if not already installed).
4. **Run test cases**:
   ```sh
   mvn clean test
5. **Build the project**:
   ```sh
   mvn clean package
6. **Edit properties (optional)**:
   Before running the application, you can edit the properties in `src/main/resources/application-dev.yml` to suit your needs.
7. **Run the application**:
   ```sh
   java -jar target/project-0.0.1-SNAPSHOT.jar
8. **Access Swagger UI**:
   Open your browser and navigate to `http://localhost:8080/swagger-ui/index.html` to use Swagger UI. Alternatively, you can use Postman to send requests to `http://localhost:8080`.

## Usage

- **Authentication**:
  - **Sign Up**: `POST /api/auth/signup`
  - **Log In**: `POST /api/auth/login`

- **Notes**:
  - All requests to `/api/notes/**` require an `Authorization` header with a Bearer token.
  - You can get the token by signing up and logging in as described above.

## Note
- Make sure to replace `your_token` with the actual token obtained from the login response.
- The default port is `8080`, and it can be changed by editing the `application-dev.yml` file.

