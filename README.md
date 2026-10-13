# Smart Internship Tracker — Backend

A robust, enterprise-ready RESTful backend API for the **Smart Internship Tracker** application, built using **Java 17** and **Spring Boot 3.2.5**. This service manages user authentication with JWT tokens, student and administrator roles, internship listings, and end-to-end application tracking workflows.

---

## 1. Project Overview

The Smart Internship Tracker backend provides secure, decoupled APIs for university students and placement cell administrators:
- **Students** can register, authenticate securely via JWT, explore active internship listings with pagination and search, apply to internships, and track/update their application statuses.
- **Administrators** have privileged access to manage internship listings (Create, Update, Delete), view all applicants across postings, and inspect registered users.
- **Dual-Database Support**: Boots out of the box with zero setup using an embedded **H2 in-memory database** for instant local development and testing, with a production-ready **MySQL profile** available.

---

## 2. Features

All features listed below are directly supported by the application code:

### 🔐 Authentication & Role-Based Access Control (RBAC)
- **Student Registration**: Public registration endpoint (`/api/auth/register`) strictly assigns `ROLE_STUDENT`.
- **JWT Login**: Validates credentials via BCrypt and issues HMAC-SHA256 signed JSON Web Tokens (`/api/auth/login`).
- **Profile Endpoint**: Authenticated `/api/auth/me` resolves the current user's profile from token claims.
- **Admin Seeding**: Automatically seeds an administrator account on initial startup (configured via environment variables).

### 💼 Internship Listings Management
- **Public Browsing & Search**: Search by title or company, sort by deadline, date, or stipend, and paginate results (`/api/internships`).
- **Detailed Specifications**: Retrieve single internship details by ID (`/api/internships/{id}`).
- **Admin CRUD**: Only users with `ROLE_ADMIN` can create, update, or delete internship listings.

### 📋 Application Tracking Workflow
- **Application Submission**: Students submit applications with resume URLs and custom notes (`/api/applications`).
- **Student Application Tracker**: Students view only their own submitted applications (`/api/applications/my`).
- **Status Lifecycle**: Track application progress across states: `APPLIED`, `UNDER_REVIEW`, `INTERVIEW`, `ACCEPTED`, `REJECTED`.
- **Application Withdrawal**: Students can withdraw their own applications (`DELETE /api/applications/{id}`).
- **Admin Applicant Oversight**: Administrators can view all student applications submitted for any specific internship listing (`/api/applications/internship/{id}`).

### 🛡️ Administrative Console
- **User Directory**: Administrators can list and filter all registered students and administrators (`/api/admin/users`).
- **User Profile Inspection**: View single user profile details (`/api/admin/users/{id}`).

### 🏥 System Health Monitoring
- Public, unauthenticated health check endpoint (`/api/health`) returning application status and uptime.

---

## 3. Technologies Used

- **Language**: Java 17 (LTS)
- **Framework**: Spring Boot 3.2.5
  - **Spring Web**: RESTful API design & JSON serialization (Jackson)
  - **Spring Data JPA & Hibernate**: ORM, repositories, entity lifecycle
  - **Spring Security 6**: Stateless JWT authentication filter, method-level authorization (`@PreAuthorize`)
  - **Spring Validation (Hibernate Validator)**: Request payload Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Min`)
- **Databases**:
  - **H2 Database (Default)**: In-memory relational database for zero-setup local execution
  - **MySQL Connector/J**: Production-grade relational database persistence
- **Security & Tokens**: `io.jsonwebtoken` (JJWT 0.12.5) for HMAC-SHA256 JWT tokens
- **Build Tool**: Apache Maven 3.8+
- **Testing**: JUnit 5, Mockito, Spring Security Test, MockMvc (33 automated unit & integration tests)

---

## 4. Project Folder Structure

```
backend/
├── .env.example                               # Environment variables template
├── .gitignore                                 # Git ignore rules for Maven, IDE, and secrets
├── pom.xml                                    # Maven dependencies & build configuration
├── README.md                                  # Project documentation
└── src/
    ├── main/
    │   ├── java/com/smartinternship/backend/
    │   │   ├── SmartInternshipApplication.java # Spring Boot entry point
    │   │   ├── config/                        # SecurityConfig (JWT filter, CORS, rules)
    │   │   ├── controller/                    # REST API Controllers
    │   │   │   ├── AdminUserController.java   # /api/admin/users endpoints
    │   │   │   ├── ApplicationController.java # /api/applications endpoints
    │   │   │   ├── AuthController.java        # /api/auth endpoints
    │   │   │   ├── HealthController.java      # /api/health endpoint
    │   │   │   └── InternshipController.java  # /api/internships endpoints
    │   │   ├── dto/                           # Request & Response Data Transfer Objects
    │   │   ├── exception/                     # Global exception handler & custom exceptions
    │   │   ├── model/                         # JPA Entities (User, Internship, Application, etc.)
    │   │   ├── repository/                    # Spring Data JPA Repositories
    │   │   ├── security/                      # JWT Provider, Auth Filter, UserDetailsService
    │   │   └── service/                       # Business logic services & interfaces
    │   └── resources/
    │       ├── application.properties         # Main configuration (default: H2 in-memory)
    │       ├── application-h2.properties      # Standalone H2 profile
    │       └── application-mysql.properties   # MySQL database profile
    └── test/
        ├── java/com/smartinternship/backend/
        │   ├── controller/                    # Controller & security integration tests
        │   ├── service/                       # Service layer unit tests (Mockito)
        │   ├── HealthControllerTests.java     # Health endpoint test
        │   └── SmartInternshipApplicationTests.java # Context load test
        └── resources/
            └── application.properties         # Isolated in-memory test database properties
```

---

## 5. Prerequisites

Before running the backend, make sure you have the following installed on Windows:

1. **Java Development Kit (JDK 17 or higher)**
   - Verify in Command Prompt:
     ```cmd
     java -version
     ```
2. **Apache Maven (3.8 or higher)**
   - Verify in Command Prompt:
     ```cmd
     mvn -version
     ```
3. *(Optional)* **MySQL Server 8.0+** (only required if running with the `mysql` profile).

---

## 6. How to Configure Environment Variables

The backend uses environment variables for sensitive settings. A template file [`.env.example`](.env.example) is included in the project root.

### Available Variables

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `PORT` | Server HTTP port | `8080` |
| `JWT_SECRET` | Secret key for JWT signing (minimum 32 characters) | *Required for production* |
| `JWT_EXPIRATION_MS`| JWT validity in milliseconds | `86400000` (24 hours) |
| `ADMIN_EMAIL` | Initial seeded administrator email | `admin@example.com` |
| `ADMIN_PASSWORD` | Initial seeded administrator password | *Your secure password* |
| `ADMIN_NAME` | Initial administrator display name | `Placement Administrator` |
| `DB_URL` | JDBC Database URL (MySQL) | `jdbc:mysql://localhost:3306/internship_tracker` |
| `DB_USERNAME` | MySQL database user | `root` |
| `DB_PASSWORD` | MySQL database password | *Your database password* |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origin URLs | `http://localhost:5173,http://127.0.0.1:5173` |

### Setting Environment Variables on Windows

#### Option A: In Windows Command Prompt (`cmd.exe`)
```cmd
set JWT_SECRET=MySuperSecretKeyForSmartInternshipTracker2026!
set ADMIN_EMAIL=admin@example.com
set ADMIN_PASSWORD=Admin@Secure2026
```

#### Option B: In Windows PowerShell
```powershell
$env:JWT_SECRET="MySuperSecretKeyForSmartInternshipTracker2026!"
$env:ADMIN_EMAIL="admin@example.com"
$env:ADMIN_PASSWORD="Admin@Secure2026"
```

---

## 7. How to Run the Backend Using Maven

Open Command Prompt or PowerShell, navigate to the `backend` folder:

```cmd
cd C:\Users\Windows\Desktop\TT-PROJECT\Smart-Internship-Tracker\backend
```

### Mode 1: Default Embedded Database (H2 — Zero Setup Required)
Ideal for local testing and demonstration. No database installation needed.

```cmd
mvn spring-boot:run
```

Once started:
- API Base URL: `http://localhost:8080/api`
- Health Check: `http://localhost:8080/api/health`
- H2 Web Console (when enabled): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:internship_tracker`
  - Username: `sa`
  - Password: *(leave blank)*

### Mode 2: MySQL Database Profile
Ensure your MySQL server is running and a database named `internship_tracker` exists:

```sql
CREATE DATABASE internship_tracker;
```

Provide your MySQL credentials and run:

```cmd
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Mode 3: Build Executable JAR File
```cmd
mvn clean package -DskipTests
java -jar target/backend-1.0.0.jar
```

---

## 8. How to Run Automated Tests

The project includes unit and integration tests covering security, controllers, and services.

Run all tests:
```cmd
mvn test
```

Expected output:
```
[INFO] Results:
[INFO] Tests run: 33, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 9. Important API Endpoints

All endpoints are prefixed with `/api`. Protected routes require the following HTTP header:
`Authorization: Bearer <your_jwt_token>`

### Health
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | Public | System status and service info |

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Request Body | Description |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | `RegisterRequest` | Register new student account |
| `POST` | `/api/auth/login` | Public | `LoginRequest` | Authenticate and obtain JWT |
| `GET` | `/api/auth/me` | Authenticated | *None* | Get authenticated user profile |

### Internships (`/api/internships`)
| Method | Endpoint | Access | Query / Body | Description |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/internships` | Public | `page`, `size`, `sortBy`, `sortDir`, `search` | Paginated list of internships |
| `GET` | `/api/internships/{id}` | Public | *None* | Get single internship by ID |
| `POST` | `/api/internships` | Admin only | `InternshipRequest` | Create a new internship posting |
| `PUT` | `/api/internships/{id}` | Admin only | `InternshipRequest` | Update an internship posting |
| `DELETE` | `/api/internships/{id}` | Admin only | *None* | Delete an internship posting |

### Applications (`/api/applications`)
| Method | Endpoint | Access | Request Body | Description |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/applications` | Student only | `ApplicationRequest` | Submit application for an internship |
| `GET` | `/api/applications/my` | Student only | *None* | Retrieve student's own applications |
| `GET` | `/api/applications/{id}` | Student (owner) / Admin | *None* | Get application details |
| `PATCH` | `/api/applications/{id}/status` | Student / Admin | `ApplicationStatusUpdateRequest` | Update status & feedback remarks |
| `DELETE` | `/api/applications/{id}` | Student (owner) / Admin | *None* | Withdraw application |
| `GET` | `/api/applications/internship/{id}` | Admin only | *None* | Get all applicants for an internship |

### Administration (`/api/admin`)
| Method | Endpoint | Access | Query Params | Description |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/users` | Admin only | `?role=STUDENT` *(optional)* | List registered users |
| `GET` | `/api/admin/users/{id}` | Admin only | *None* | View user profile details |

---

## 10. How to Connect the React Frontend

The React.js frontend (built with Vite) communicates with this backend over REST.

1. **Verify CORS Settings**:
   In [application.properties](src/main/resources/application.properties), `app.cors.allowed-origins` includes:
   `http://localhost:5173,http://127.0.0.1:5173`
2. **Configure Frontend Base URL**:
   In your frontend directory, configure your `.env` file:
   ```properties
   VITE_API_BASE_URL=http://localhost:8080/api
   ```
3. **Launch Order**:
   - Start the backend on port `8080`: `mvn spring-boot:run`
   - Start the Vite frontend in its separate directory: `npm run dev`
   - The frontend status indicator in the navbar will display **🟢 Live API**.

---

## 11. Security Notes & Best Practices

- **Never Commit Secrets**: Do not commit actual passwords, private JWT keys, or local `.env` files to GitHub or any public version control system.
- **Git Protection**: The provided [`.gitignore`](.gitignore) is pre-configured to ignore `.env`, `.env.*`, `application-local.properties`, build artifacts (`target/`), and IDE configurations.
- **JWT Key Strength**: Use a cryptographically secure key of at least 256 bits (32+ characters) for `JWT_SECRET`.
- **Database Passwords**: When switching to MySQL for staging or production, always supply `DB_USERNAME` and `DB_PASSWORD` via environment variables.
