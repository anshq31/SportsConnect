# SportsConnect — Backend

A production-deployed Spring Boot backend (100% Java) for the SportsConnect platform — enabling users to create, join, and manage sports gigs, participate in real-time team chats, leave reviews, and build their player profile.

**Live API:** `https://sportsconnect-c2po.onrender.com`

---

## 🚀 Features

- **Authentication & JWT Security** — Register, login, refresh tokens, BCrypt password hashing, secured endpoints
- **Gig Management** — Create, browse, join, complete, and delete sports gigs with full participant flow
- **Real-time Team Chat** — Group chat per gig powered by WebSocket/STOMP, persisted to DB
- **User Profiles & Skills** — Rich profiles with customizable experience, skills, and aggregated ratings
- **Review System** — Gig masters can review participants post-completion; ratings persist independently of gig lifecycle
- **Automated Lifecycle** — Scheduled cron jobs auto-complete expired gigs and clean up old completed gigs
- **RESTful API** — All business logic exposed via JSON, secured with JWT Bearer tokens

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (JJWT) |
| Realtime | Spring WebSocket + STOMP |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL (Render managed) |
| Deployment | Docker on Render |
| Utilities | Lombok, Jackson |

---

## 🌐 Live Deployment

| Item | Detail |
|---|---|
| Platform | [Render](https://render.com) |
| Base URL | `https://sportsconnect-c2po.onrender.com` |
| Database | Render PostgreSQL (Singapore region) |
| Container | Docker (eclipse-temurin:17) |

> **Note:** The API is hosted on Render's free tier. The first request after a period of inactivity may take 30–60 seconds while the service wakes up. Subsequent requests are fast.

---

## 🧩 API Reference

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT |
| POST | `/api/auth/refresh` | Refresh access token |

### Gigs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/gigs` | Create a gig |
| GET | `/api/gigs/active` | Browse joinable gigs (supports `?sport=` and `?location=` filters) |
| GET | `/api/gigs/created` | Gigs you created |
| GET | `/api/gigs/joined` | Gigs you joined |
| GET | `/api/gigs/{id}` | Get gig detail |
| POST | `/api/gigs/{id}/request-join` | Request to join a gig |
| PUT | `/api/gigs/{id}/complete` | Mark gig as complete |
| DELETE | `/api/gigs/{id}` | Delete your gig |

### Participation
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/gigs/my-gig/requests` | View join requests for your gig |
| POST | `/api/gigs/my-gig/requests/{id}/accept` | Accept a join request |
| POST | `/api/gigs/my-gig/requests/{id}/reject` | Reject a join request |

### User Profile
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users/me` | My profile |
| PUT | `/api/users/me` | Update experience and skills |
| GET | `/api/users/{userId}` | View any user's public profile |

### Reviews
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/reviews` | Submit a review for a gig participant |
| GET | `/api/reviews/user/{id}` | Get all reviews for a user (paginated) |

### Chat
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/chat/{gigId}/history` | Paginated chat history |
| WS | `wss://sportsconnect-c2po.onrender.com/ws` | STOMP WebSocket endpoint |

---

## ⚙️ Running Locally

### Prerequisites
- Java 17+
- Maven
- PostgreSQL (local) or use the Render DB URL directly

### Setup

1. **Clone the repo:**
    ```shell
    git clone https://github.com/anshq31/SportsConnect.git
    cd SportsConnect
    ```

2. **Create `src/main/resources/application-dev.properties`** (gitignored — not committed):
    ```properties
    server.port=8080
    spring.datasource.url=jdbc:postgresql://localhost:5432/sportsconnect
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.jpa.show-sql=true
    jwt.secret=your_jwt_secret
    ```

3. **Run:**
    ```shell
    mvn clean install
    mvn spring-boot:run
    ```
    Server starts on `http://localhost:8080`

---

## 🐳 Docker

```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run locally:
```shell
docker build -t sportsconnect .
docker run -p 8080:10000 sportsconnect
```

---

## 🔒 Security

- JWT Bearer token required on all endpoints except `/api/auth/**` and `/ws/**`
- Passwords hashed with BCrypt — never stored in plaintext
- STOMP WebSocket authenticated via JWT in the `CONNECT` frame header
- Production secrets managed via Render environment variables — never committed to source

---

## ⏱️ Scheduled Jobs

Two cron jobs run automatically on the deployed server:

| Job | Schedule | Action |
|---|---|---|
| `autoCompleteGigs` | Daily at midnight | Marks ACTIVE/FULL gigs past their dateTime as COMPLETED |
| `autoDeleteCompletedGigs` | Daily at 2am | Hard deletes gigs that have been COMPLETED for 2+ days |

This two-phase approach preserves completed gig data (reviews, history) for a retention window before permanent deletion.

---

## 📖 Project Structure

```
src/
 └── main/
     ├── java/com/ansh/sportsconnect/
     │    ├── config         # Security, WebSocket, JPA configs
     │    ├── controller     # REST + WebSocket controllers
     │    ├── dto            # Request/response objects
     │    ├── model          # JPA entities
     │    ├── repository     # Spring Data JPA repositories
     │    ├── security       # JWT filter, entry point, service
     │    ├── service        # Business logic
     │    └── seeder         # Skill data seeder
     └── resources/
          ├── application.properties          # Shared config (committed)
          ├── application-dev.properties      # Local secrets (gitignored)
          └── application-prod.properties     # Prod env vars (gitignored)
```

---

## 🧪 Testing

- Unit tests in `src/test/` with JUnit and Mockito
- API testing recommended via Postman against `https://sportsconnect-c2po.onrender.com`

---

## 🙏 Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot)
- [JJWT](https://github.com/jwtk/jjwt)
- [Lombok](https://projectlombok.org)
- [Render](https://render.com) for hosting
