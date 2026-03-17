# SportsConnect (Backend)

[![Build](https://img.shields.io/github/workflow/status/anshq31/SportsConnect/Java%20CI)](https://github.com/anshq31/SportsConnect/actions)
[View Commits](https://github.com/anshq31/SportsConnect/commits/main)

A robust Spring Boot backend (100% Java) for the SportsConnect platform — enabling users to create, join, and manage sports gigs, participate in real-time chats, leave reviews, and more.

---

## 🚀 Features

- **Authentication & JWT Security**: User registration, login, refresh tokens, secure endpoints, password hashing.
- **Gigs Management**: Create, join, complete, and delete sports gigs. Full CRUD with participant flow.
- **Team Chat (Realtime)**: Group chat per gig powered by WebSocket/STOMP.
- **User Profiles & Skills**: Rich user profiles with customizable experience and skills.
- **Review System**: Users can rate and review other participants post-gig; profile ratings aggregate historical feedback.
- **RESTful API**: All business logic exposed via JSON, secured using JWT authentication.

---

## 🛠️ Tech Stack

- **Spring Boot 3.x**
- **Spring Security (JWT)**
- **Spring WebSocket & STOMP** for realtime chat
- **Spring Data JPA & Hibernate** (MySQL by default)
- **Lombok** for DTOs and entities
- **JUnit & Mockito** for testing

---

## 🧩 API Structure

### Authentication
- `POST /api/auth/register` — Register user
- `POST /api/auth/login` — Login and get JWT
- `POST /api/auth/refresh` — Refresh JWT (safe, doesn’t invalidate other sessions)

### Gigs
- `POST   /api/gigs`                    — Create a gig
- `GET    /api/gigs/active`             — List all joinable gigs (with filters)
- `GET    /api/gigs/created`            — Gigs you created
- `GET    /api/gigs/joined`             — Gigs you joined
- `POST   /api/gigs/{id}/request-join`  — Request to join a gig
- `PUT    /api/gigs/{id}/complete`      — Mark gig as complete
- `DELETE /api/gigs/{id}`               — Delete gig

### Participation
- `GET    /api/gigs/my-gig/requests`                — See join requests for your gig
- `POST   /api/gigs/my-gig/requests/{id}/accept`    — Accept request
- `POST   /api/gigs/my-gig/requests/{id}/reject`    — Reject request

### User Profile
- `GET    /api/users/me`        — My profile (experience, skills, reviews, rating)
- `PUT    /api/users/me`        — Update experience & skills
- `GET    /api/users/{userId}`  — Public profile

### Review System
- `POST   /api/reviews`           — Add review for a user, one per gig per user
- `GET    /api/reviews/user/{id}` — Get all reviews for a user (paging supported)

### Chat
- `GET    /api/chat/{gigId}/history`   — RESTful chat history (paged)
- `WS     /ws`                         — WebSocket STOMP endpoint (room = gigId)

---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL (local or remote)
- (Optional) Redis for future caching

### Setup

1. **Clone and configure DB:**
    ```shell
    git clone https://github.com/anshq31/SportsConnect.git
    cd SportsConnect
    # edit src/main/resources/application.properties for your MySQL username/password/database
    ```

2. **Run database migrations**  
    (Schema auto-creates. You may add Flyway/Liquibase in the future for versioned migrations.)

3. **Build and run:**
    ```shell
    mvn clean install
    mvn spring-boot:run
    ```
    Server runs on port 8080 by default.

---

## 🧑‍💻 Development

- **Code style**: 100% Java, Clean DTO/entity separation, `com.ansh.sportsconnect` package (see [refactor commit](https://github.com/anshq31/SportsConnect/commit/23123894fcc9db7d62d4b84c05dccb84f14a9321))
- **Recent improvements**:
    - Refactored to use package name `com.ansh.sportsconnect`
    - Removed jargon and commented code for clarity
    - Sports types in Gigs are now dynamic (not hardcoded)
    - Gig → Chat links now use gigId instead of groupId
    - Review system full CRUD (add/view), profile ratings
    - 401 on invalid refresh tokens, improved session handling

---

## 🔒 Security

- JWT-based authentication on all endpoints (except `/api/auth/**` and `/ws/**`)
- Passwords: BCrypt hashing, never stored in plaintext
- All user data endpoints require a valid Bearer token
- CORS: Origin patterns configurable in security config
- Rate limiting can be enabled via Spring or proxy/middleware as needed

---

## 📖 Project Structure

```
src/
 └── main/
     ├── java/com/ansh/sportsconnect/
     │    ├── config            # Security/websocket/JPA configs
     │    ├── controller        # All REST endpoints (Auth, Gigs, User, Reviews, Chat, etc)
     │    ├── dto               # Request/response objects
     │    ├── model             # Entities
     │    ├── repository        # Spring Data JPA repositories
     │    ├── security          # JWT & Security filters
     │    ├── service           # Business logic & helpers
     │    └── seeder            # Data seeding (skills etc)
     └── resources/
          └── application.properties
```

---

## 🧪 Testing

- Unit and integration tests for all core services: see `src/test/`
- Mocking with Mockito
- End-to-end test recommendations: Postman collections + Selenium for chat

---

## 🖥️ Deployment

- Recommended: Run on Linux (Ubuntu 22+) with Dockerized MySQL
- `application-prod.properties` for production secrets (not committed)
- Spring profiles supported for `dev`, `test`, `prod`

---

## 🙏 Acknowledgements

- Starter seed for skills
- Open-source libraries: Spring Boot, Lombok, JJWT, Mockito, STOMP, MySQL
- Inspiration from online sports booking platforms

---
