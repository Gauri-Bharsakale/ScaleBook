# 📚 ScaleBook – Distributed Resource Reservation System

ScaleBook is a full-stack resource reservation platform built using Spring Boot and React. It allows authenticated users to browse available resources and provides the foundation for a scalable booking system using modern backend architecture and containerization.

The project demonstrates authentication, role-based access, REST APIs, Dockerized deployment, and integration with PostgreSQL, Redis, and RabbitMQ.

---

## 🚀 Features

### Authentication
- User Registration
- User Login
- JWT Token-based Authentication
- BCrypt Password Encryption
- Role-based User Management (USER, ADMIN)

### Resource Management
- View all available resources
- View resource details
- Create new resources
- Update existing resources
- Soft delete resources (using `isActive`)

### Infrastructure
- PostgreSQL Database
- Redis Integration
- RabbitMQ Integration
- Docker & Docker Compose
- Layered Spring Boot Architecture

---

# 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- JWT Authentication
- Maven

### Frontend
- React
- TypeScript
- Axios
- React Router

### Database
- PostgreSQL

### Messaging & Caching
- RabbitMQ
- Redis

### DevOps
- Docker
- Docker Compose

---

# 📂 Project Structure

```
scalebook
│
├── scalebook-backend
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── security
│   ├── config
│   └── dto
│
├── scalebook-frontend
│
├── docker-compose.yml
│
└── README.md
```

---

# 🏗 System Architecture

```
                 React Frontend
                       │
                 HTTP Requests
                       │
                       ▼
              Spring Boot Backend
        (Controller → Service → Repository)
                       │
      ┌────────────────┼────────────────┐
      │                │                │
      ▼                ▼                ▼
 PostgreSQL         Redis          RabbitMQ
(User, Roles,     (Caching)      (Async Messaging)
 Resources)
```

---

# 🔐 Authentication Flow

```
User
 │
 ▼
Register/Login
 │
 ▼
Spring Security
 │
 ▼
JWT Token Generated
 │
 ▼
Client stores Token
 │
 ▼
Authorization:
Bearer <JWT Token>
 │
 ▼
Protected APIs
```

---

# 📊 Database Tables

- users
- roles
- user_roles
- resources

---

# 📌 REST APIs

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | `/api/auth/register` |
| POST | `/api/auth/login` |

---

## Resources

| Method | Endpoint |
|---------|----------|
| GET | `/api/resources` |
| GET | `/api/resources/{id}` |
| POST | `/api/resources` |
| PUT | `/api/resources/{id}` |
| DELETE | `/api/resources/{id}` |

---

# 🐳 Running the Project

## Prerequisites

- Java 17+
- Docker Desktop
- Docker Compose

---

## Clone Repository

```bash
git clone https://github.com/<your-username>/scalebook.git
cd scalebook
```

---

## Start Application

```bash
docker compose up -d --build
```

---

## Stop Application

```bash
docker compose down
```

---

## Open Application

Frontend

```
http://localhost
```

Backend API

```
http://localhost:8080
```

RabbitMQ Dashboard

```
http://localhost:15672
```

---

# 🧪 Testing

### Register User

```
POST /api/auth/register
```

### Login

```
POST /api/auth/login
```

Copy the JWT token returned by the login endpoint.

---

### Access Protected APIs

In Postman

```
Authorization
```

Select

```
Bearer Token
```

Paste the JWT token.

Example

```
GET /api/resources
```

---

# 🔒 Security Features

- JWT Authentication
- BCrypt Password Hashing
- Stateless Authentication
- Spring Security
- Role-based Authorization
- Passwords are never stored in plain text

---

# 📈 Scalability Considerations

The project has been designed using a layered architecture, making it easy to extend with additional features such as:

- Booking Management
- Distributed Resource Locking
- Email Notifications
- Resource Availability Tracking
- Caching Frequently Accessed Resources
- Cloud Deployment
- CI/CD Pipelines

---

# 🔮 Future Enhancements

- Redis Distributed Locking to prevent double booking
- Email Notifications using RabbitMQ consumers
- Booking History
- Admin Dashboard
- AWS EC2 Deployment
- Nginx Reverse Proxy
- HTTPS with SSL
- Load Testing
- Monitoring using Prometheus and Grafana

---

# 👩‍💻 Author

**Gauri Bharsakale**

Final Year Computer Science Engineering Student

```
