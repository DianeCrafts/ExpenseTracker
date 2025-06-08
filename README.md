# Expense Tracker

A simple full-stack application to manage expenses, built with Spring Boot and Vue.js.

## Prerequisites
- Java 11 or higher
- Maven
- Node.js and npm

## Project Structure
- `backend/`: Spring Boot REST API
- `frontend/`: Vue.js frontend


## Instructions
1. Project Setup:
   - Download the project from the provided [GitHub link or ZIP file].
   - Backend:
      - Navigate to the backend/ directory.
      - Run mvn spring-boot:run to start the server (runs on http://localhost:8080).
   - Frontend:
      - Navigate to the frontend/ directory.
      - Run npm install to install dependencies.
      - Run npm run serve to start the app (typically on http://localhost:8084).
   - Verify the application is running by accessing it in your browser.
   - You can login using the credentials admin and admin.
   - Database can be accessed by going to this link http://localhost:8080/h2-console/login.jsp
   - Information required to login in the database above can be accessed from application.properties file

## Features
   - Add, edit, and delete expenses.
   - Add categories.
   - Filter expenses by description, amount range, date range, and category.
   - Pagination with configurable rows per page.
   - Embedded H2 database for easy setup and testing.
   - Including unit test and integration test

## Requirements
Before running the project, make sure the following are installed:

### 1. Java (JDK 11+)

- Required for running the Spring Boot backend.
- Verify installation:
  ```bash
  java -version

### Node.js 
- Required for running the Vue.js frontend.
- Install from the official site: https://nodejs.org

###  Font Awesome (Free Version)
- Used for frontend icons (e.g., edit, delete, archive).
```bash
npm install @fortawesome/fontawesome-free
