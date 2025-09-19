
# Hamro Gunaso – Empowering Voices, Driving Change

Hamro Gunaso is an innovative, full-featured platform designed to streamline the process of submitting, tracking, and resolving public complaints. Leveraging modern Java and Spring Boot technologies, it delivers a robust, secure, and scalable solution for both citizens and administrators.

Our mission is to bridge the gap between the public and authorities, ensuring every voice is heard and every issue is addressed transparently. Hamro Gunaso is more than just a backend service—it's a catalyst for social impact, accountability, and digital transformation in governance.

---

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)


## Key Features
- **Seamless Complaint Submission:** Citizens can easily file complaints with supporting documents and media.
- **Real-Time Tracking:** Transparent status updates and notifications for every complaint.
- **KYC & User Verification:** Secure onboarding with document uploads and identity checks.
- **Role-Based Access:** Distinct dashboards and permissions for users, admins, and authorities.
- **Data Security:** Robust authentication, validation, and exception handling.
- **RESTful API:** Clean, well-documented endpoints for easy integration and extension.


## Project Structure
```
├── src/main/java/com/project/hamroGunaso/
│   ├── HamroGunasoApplication.java
│   ├── config/           # Configuration & security
│   ├── controller/       # RESTful API controllers
│   ├── entities/         # JPA entities & models
│   ├── ENUM/             # Enum types
│   ├── exception/        # Custom error handling
│   ├── repository/       # Data access layer
│   ├── requestDTO/       # API request DTOs
│   ├── responseDTO/      # API response DTOs
│   └── services/         # Business logic & services
├── src/test/java/com/project/hamroGunaso/ # Unit & integration tests
├── uploads/              # User-uploaded files (KYC, complaints, etc.)
├── pom.xml               # Maven build file
└── README.md             # Project documentation
```


## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL (or compatible RDBMS)

### Quick Start
1. **Clone the repository:**
   ```sh
   git clone <repository-url>
   cd Team-FusionStack-Backend
   ```
2. **Configure your database:**
   - Edit `src/main/resources/application.properties` with your DB credentials.
3. **Build the project:**
   ```sh
   mvn clean install
   ```
4. **Run the backend server:**
   ```sh
   mvn spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.


## Configuration
- All configuration files are in `src/main/resources/`.
- Use environment variables or secrets management for sensitive data.


## API Documentation
- All endpoints are defined in the `controller` package.
- For API exploration and testing, use [Swagger](https://swagger.io/) or [Postman](https://www.postman.com/).
- (Optional) Integrate Swagger UI for interactive, auto-generated API docs.


## Testing
- Unit and integration tests are in `src/test/java/com/project/hamroGunaso/`.
- To run all tests:
   ```sh
   mvn test
   ```


## Contributing
We welcome contributions from everyone! To contribute:
1. Fork this repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to your branch (`git push origin feature/your-feature`)
5. Open a Pull Request with a clear description


## License
This project is licensed under the MIT License.
