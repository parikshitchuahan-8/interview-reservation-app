# CareerSprint

CareerSprint is a placement-focused full-stack application built with Spring Boot, Thymeleaf, HTMX, JPA, and PostgreSQL. Instead of cloning a generic booking app, it solves a real student workflow: tracking job applications, interview rounds, preparation focus, and high-priority opportunities from one dashboard.

## Why this project stands out

- Solves a practical college placement problem instead of repeating a common demo.
- Includes pipeline analytics, readiness scoring, coach-style recommendations, upcoming interview tracking, and recruiter follow-up reminders.
- Includes pipeline analytics, readiness scoring, mock interview readiness signals, coach-style recommendations, upcoming interview tracking, and recruiter follow-up reminders.
- Supports quick stage updates directly from the dashboard with HTMX.
- Exports application history to CSV for reporting or sharing.
- Tracks resume variants across applications to support targeted job-search strategy.
- Includes application editing, progress-history analytics, and reminder automation.
- Uses password hashing, session-based login flow, and user-owned record protection.

## Core features

- Registration and login
- Placement dashboard with summary metrics
- Stage filters across the application pipeline
- Add, remove, and update applications
- Edit existing applications with full form-based updates
- Readiness score per company based on pipeline activity
- Focus recommendation for the strongest next opportunity
- Coach tips generated from current pipeline state
- Recruiter follow-up radar for due outreach
- Resume version tracking across companies
- Mock interview score and confidence tracking
- Recruiter outreach draft generation
- Monthly momentum and recent activity history on the dashboard
- Manual and scheduled reminder sending when SMTP is configured
- CSV export of tracked applications

## Tech stack

- Java 21
- Spring Boot 3
- Spring MVC
- Spring Data JPA
- Thymeleaf
- HTMX
- PostgreSQL
- Lombok

## Suggested interview pitch

Say that you wanted to build something closer to student reality than another task manager or booking clone. CareerSprint helps users manage a placement pipeline by combining CRUD, dashboard analytics, lightweight recommendation logic, and faster UI updates through HTMX. The project demonstrates backend modeling, server-rendered UI, persistence, and product thinking in a single system.

## Optional reminder automation setup

Set these if you want scheduled reminder emails to send automatically:

- `careersprint.reminders.enabled=true`
- `MAIL_HOST`
- `MAIL_PORT`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_SMTP_AUTH`
- `MAIL_SMTP_STARTTLS`

Without SMTP configuration, reminder actions still work as in-app workflow prompts and recruiter message drafts.

## Run locally

1. Create a PostgreSQL database named `placement_tracker`, or override the datasource with environment variables.
2. Set environment variables if needed:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
3. Run the app:

```powershell
.\mvnw.cmd spring-boot:run
```

4. Open `http://localhost:8080`

## Future upgrade ideas

- Add file upload for resume snapshots
- Add interview feedback journal entries per round
- Add recruiter contact analytics by company type
- Add calendar sync for interviews and follow-up tasks
