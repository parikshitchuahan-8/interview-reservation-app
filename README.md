# Interview Reservation App

Interview Reservation App is a Spring Boot application for managing interview scheduling workflows, user registration, reservation handling, dashboard insights, and reminder automation.

## Highlights

- User registration and login flows
- Reservation management for interview scheduling
- Dashboard and reporting support
- Reminder automation service
- Spring Boot web application with Thymeleaf views
- PostgreSQL-backed persistence through Spring Data JPA

## Tech Stack

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- Maven

## Project Structure

```text
interview-reservation-app/
|-- src/main/java/com/example/system/
|   |-- controller/
|   |-- entity/
|   |-- repository/
|   |-- service/
|   `-- util/
|-- src/main/resources/
|-- src/test/java/
|-- pom.xml
`-- Dockerfile
```

## Run Locally

```powershell
.\mvnw.cmd spring-boot:run
```

Then open `http://localhost:8080`.

## Key Modules

- `ReservationController` for reservation workflows
- `DashboardController` for high-level application views
- `UserRegistrationController` for onboarding users
- `ReminderAutomationService` for reminder-related logic

## Notes

- Keep local logs, IDE files, and generated output files out of version control.
- Review datasource and mail configuration before running the application locally.
