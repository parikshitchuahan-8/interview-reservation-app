# Interview Reservation App

This repository is now organized with the Spring Boot application living in `backend/`.

## Structure

- `backend/` - interview reservation backend application
- `backend/src/main/java` - controllers, entities, repositories, services, and utilities
- `backend/src/main/resources` - application properties, Thymeleaf templates, and static assets
- `backend/src/test/java` - backend test suite

## Run locally

1. Change into the backend project:

```powershell
cd backend
```

2. Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

3. Open `http://localhost:8080`

## Notes

- The original app README has been copied to `backend/README.md`.
- This root README now describes the repository layout rather than the Spring Boot module internals.
