# Campus Lost & Found

A college mini-project built with Spring Boot + React + MySQL.

## Features
- Register and login with JWT
- Create LOST / FOUND item reports
- Search and filter reports
- View item details
- Update/delete your own reports
- Mark an item as recovered
- Simple automatic matching based on category + location + keywords
- Admin dashboard APIs
- Clean React frontend

## Requirements
- Java 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8+

## Database
Create a MySQL database:

```sql
CREATE DATABASE campus_lost_found;
```

Then update `backend/src/main/resources/application.properties` with your MySQL username/password.

## Run backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on http://localhost:8080

## Run frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on http://localhost:5173

## Demo admin
The first registered user is not automatically admin. To make a user admin, run:

```sql
UPDATE users SET role='ADMIN' WHERE email='your-email@example.com';
```

## API
- POST `/api/auth/register`
- POST `/api/auth/login`
- GET `/api/items`
- GET `/api/items/{id}`
- POST `/api/items`
- PUT `/api/items/{id}`
- DELETE `/api/items/{id}`
- PATCH `/api/items/{id}/recover`
- GET `/api/items/{id}/matches`
- GET `/api/admin/stats`
