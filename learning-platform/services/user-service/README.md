# User Service — Complete Architecture & Working

## 1. Overview

The **User Service** is responsible for managing users in the Learning Platform.

It handles:

* User registration
* User retrieval
* User update
* User deletion
* Password encryption
* Role management
* JWT authentication
* Role-based authorization
* Input validation
* Duplicate email handling
* Global exception handling
* PostgreSQL persistence
* Docker-based database setup

The service is built using:

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT
* BCrypt
* PostgreSQL
* Docker
* Maven

---

# 2. High-Level Architecture

```text
                         CLIENT
                           |
                           | HTTP Request
                           | JSON + JWT
                           v
                  +-------------------+
                  |   User Service    |
                  |    Port: 8081     |
                  +-------------------+
                           |
                           v
                +----------------------+
                |   Spring Security    |
                |                      |
                |  JWT Authentication  |
                |  Role Authorization  |
                +----------------------+
                           |
                           v
                +----------------------+
                |      Controller      |
                |    UserController    |
                +----------------------+
                           |
                           v
                +----------------------+
                |       Service        |
                |      UserService     |
                +----------------------+
                           |
                           v
                +----------------------+
                |      Repository      |
                |    UserRepository    |
                +----------------------+
                           |
                           v
                +----------------------+
                |     PostgreSQL       |
                |       users          |
                +----------------------+
```

---

# 3. Request Flow

A normal request follows this path:

```text
Postman / Frontend
        |
        v
HTTP Request
        |
        v
JWT Authentication Filter
        |
        |---- Invalid/Missing JWT ---> 401
        |
        v
SecurityContext
        |
        |---- Insufficient Role ------> 403
        |
        v
Controller
        |
        v
Service
        |
        v
Repository
        |
        v
PostgreSQL
        |
        v
Response
```

---

# 4. Project Structure

A typical structure is:

```text
user-service/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── learningplatform/
│   │   │           └── user_service/
│   │   │
│   │   │           ├── UserServiceApplication.java
│   │   │           │
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java
│   │   │           │
│   │   │           ├── controller/
│   │   │           │   └── UserController.java
│   │   │           │
│   │   │           ├── dto/
│   │   │           │   └── CreateUserRequest.java
│   │   │           │
│   │   │           ├── entity/
│   │   │           │   ├── User.java
│   │   │           │   └── Role.java
│   │   │           │
│   │   │           ├── exception/
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── EmailAlreadyExistsException.java
│   │   │           │   └── RoleChangeNotAllowedException.java
│   │   │           │
│   │   │           ├── repository/
│   │   │           │   └── UserRepository.java
│   │   │           │
│   │   │           ├── security/
│   │   │           │   └── JwtAuthenticationFilter.java
│   │   │           │
│   │   │           └── service/
│   │   │               └── UserService.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# 5. Main Components

## 5.1 Controller

The controller exposes REST APIs.

```java
@RestController
@RequestMapping("/api/users")
public class UserController
```

The controller should mainly:

* Receive HTTP requests
* Validate request DTOs
* Call the service
* Return HTTP responses

It should not contain business logic.

Example:

```text
POST /api/users
        |
        v
UserController
        |
        v
UserService.createUser()
```

---

# 6. Entity Layer

The `User` entity represents the `users` table.

Important fields:

```text
id
firstName
lastName
email
password
role
createdAt
updatedAt
```

The database representation is approximately:

```text
users
------------------------------------------------
id           BIGINT
first_name   VARCHAR
last_name    VARCHAR
email        VARCHAR UNIQUE
password     VARCHAR
role         VARCHAR
created_at   TIMESTAMP
updated_at   TIMESTAMP
```

---

# 7. User Entity

The entity uses:

```java
@Entity
@Table(name = "users")
```

The ID is generated by PostgreSQL:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Email is unique:

```java
@Column(nullable = false, unique = true)
private String email;
```

Password is never returned in JSON:

```java
@JsonIgnore
@Column(nullable = false)
private String password;
```

This is important because the password should never be exposed through REST responses.

---

# 8. User Lifecycle

The entity uses JPA lifecycle methods.

## Before insert

```java
@PrePersist
protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();

    createdAt = now;
    updatedAt = now;

    if (role == null) {
        role = Role.STUDENT;
    }
}
```

This means:

```text
New User
   |
   +-- createdAt = current time
   |
   +-- updatedAt = current time
   |
   +-- role == null ?
          |
          YES
          |
          v
       STUDENT
```

Therefore, if the application does not set the role, the database user becomes:

```text
STUDENT
```

---

# 9. Role Enum

The role is normally represented by an enum:

```java
public enum Role {
    STUDENT,
    INSTRUCTOR,
    ADMIN
}
```

The database stores the enum as text because of:

```java
@Enumerated(EnumType.STRING)
```

Example:

```text
STUDENT
INSTRUCTOR
ADMIN
```

---

# 10. Important Role Rule

The application has three roles:

```text
STUDENT
INSTRUCTOR
ADMIN
```

Authorization is:

```text
STUDENT
   |
   +--> student endpoints

INSTRUCTOR
   |
   +--> student endpoints
   |
   +--> instructor endpoints

ADMIN
   |
   +--> student endpoints
   |
   +--> instructor endpoints
   |
   +--> admin endpoints
```

Therefore:

| Role       | Student API | Instructor API | Admin API |
| ---------- | ----------- | -------------- | --------- |
| STUDENT    | Yes         | No             | No        |
| INSTRUCTOR | Yes         | Yes            | No        |
| ADMIN      | Yes         | Yes            | Yes       |

---

# 11. DTO Layer

User creation uses a DTO instead of directly exposing the entity.

Example:

```java
public class CreateUserRequest
```

The DTO contains:

```text
firstName
lastName
email
password
```

Validation is applied using Jakarta Validation.

Example:

```java
@NotBlank(message = "First name is required")
private String firstName;
```

Email:

```java
@NotBlank(message = "Email is required")
@Email(message = "Invalid email format")
private String email;
```

Password:

```java
@NotBlank(message = "Password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String password;
```

---

# 12. Why DTO Is Used

Without a DTO:

```text
HTTP Request
     |
     v
User Entity
```

With a DTO:

```text
HTTP Request
     |
     v
CreateUserRequest
     |
     v
User Entity
```

This is better because the external API does not directly control every field in the database entity.

For example:

```text
id
createdAt
updatedAt
```

should not be supplied by a normal user during registration.

---

# 13. User Registration

The endpoint is:

```http
POST /api/users
```

Example request:

```json
{
    "firstName": "Test",
    "lastName": "Instructor",
    "email": "instructor2@example.com",
    "password": "Instructor@123",
    "role": "INSTRUCTOR"
}
```

The controller receives the request.

Then it creates a `User`:

```java
User user = new User();

user.setFirstName(request.getFirstName());
user.setLastName(request.getLastName());
user.setEmail(request.getEmail());
user.setPassword(request.getPassword());
```

The service handles the business logic.

---

# 14. Password Encryption

The password must NEVER be stored as plain text.

The service uses:

```java
passwordEncoder.encode(user.getPassword())
```

The encoder is:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

So:

```text
Instructor@123
        |
        v
BCrypt
        |
        v
$2a$10$.............
```

The database stores the BCrypt hash.

It does NOT store:

```text
Instructor@123
```

---

# 15. Password Verification

During login, the password is not compared directly.

Conceptually:

```text
User enters:

Instructor@123

       |
       v

PasswordEncoder.matches()

       |
       v

Stored BCrypt hash
```

BCrypt verifies whether the password matches the stored hash.

---

# 16. Duplicate Email Handling

Before creating a user:

```java
if (userRepository.existsByEmail(user.getEmail())) {
    throw new EmailAlreadyExistsException(
        "Email already registered"
    );
}
```

Therefore:

```text
POST /api/users
        |
        v
Does email exist?
     /       \
   YES       NO
   |          |
   v          v
409          Create
Conflict     User
```

The custom exception is:

```java
public class EmailAlreadyExistsException
        extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
```

---

# 17. HTTP 409 Conflict

If the email already exists, the correct response is:

```http
409 Conflict
```

Example:

```json
{
    "error": "Email already registered"
}
```

This is different from authentication errors.

```text
400 = Invalid request
401 = Not authenticated
403 = Authenticated but not authorized
404 = Resource not found
409 = Conflict
500 = Server error
```

---

# 18. Global Exception Handler

The application uses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler
```

This allows exceptions to be handled centrally.

For example:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
```

handles validation failures.

Example request:

```json
{
    "firstName": "",
    "lastName": "",
    "email": "wrong-email",
    "password": "123"
}
```

Possible response:

```json
{
    "firstName": "First name is required",
    "lastName": "Last name is required",
    "email": "Invalid email format",
    "password": "Password must be at least 6 characters"
}
```

---

# 19. Exception Handling Should Also Include Business Exceptions

For example:

```java
@ExceptionHandler(EmailAlreadyExistsException.class)
public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(
        EmailAlreadyExistsException ex) {

    Map<String, String> error = new HashMap<>();

    error.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
}
```

For role-change violations, use a dedicated exception rather than a generic `RuntimeException`.

Example:

```java
@ExceptionHandler(RoleChangeNotAllowedException.class)
public ResponseEntity<Map<String, String>> handleRoleChange(
        RoleChangeNotAllowedException ex) {

    Map<String, String> error = new HashMap<>();

    error.put("error", ex.getMessage());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(error);
}
```

This produces a proper:

```http
403 Forbidden
```

instead of a generic:

```http
500 Internal Server Error
```

---

# 20. Repository Layer

The repository communicates with PostgreSQL through Spring Data JPA.

Example:

```java
public interface UserRepository
        extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
```

This provides operations such as:

```text
save()
findById()
findAll()
deleteById()
existsById()
```

and custom queries:

```text
existsByEmail()
findByEmail()
```

---

# 21. Get All Users

Endpoint:

```http
GET /api/users
```

Controller:

```java
@GetMapping
public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(
        userService.getAllUsers()
    );
}
```

Flow:

```text
GET /api/users
      |
      v
Controller
      |
      v
UserService
      |
      v
UserRepository.findAll()
      |
      v
PostgreSQL
```

Because the password has:

```java
@JsonIgnore
```

the response does not contain the password.

---

# 22. Get User By ID

Endpoint:

```http
GET /api/users/{id}
```

Example:

```http
GET /api/users/7
```

Service:

```java
public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
}
```

If user exists:

```http
200 OK
```

If not:

```http
404 Not Found
```

---

# 23. Get User By Email

Endpoint:

```http
GET /api/users/email/{email}
```

Example:

```http
GET /api/users/email/instructor2@example.com
```

The repository uses:

```java
findByEmail(email)
```

---

# 24. Update User

Endpoint:

```http
PUT /api/users/{id}
```

Example:

```http
PUT /api/users/7
```

The service first finds the existing user:

```java
User existingUser =
    userRepository.findById(id)
        .orElseThrow(...);
```

Then normal fields are updated:

```java
existingUser.setFirstName(
    updatedUser.getFirstName()
);

existingUser.setLastName(
    updatedUser.getLastName()
);

existingUser.setEmail(
    updatedUser.getEmail()
);
```

---

# 25. Password Update

Password is updated only if a new password was supplied.

```java
if (updatedUser.getPassword() != null
        && !updatedUser.getPassword().isBlank()) {

    existingUser.setPassword(
        passwordEncoder.encode(
            updatedUser.getPassword()
        )
    );
}
```

This is important.

If the password is omitted:

```text
Existing password
       |
       v
UNCHANGED
```

If a new password is supplied:

```text
New plain password
       |
       v
BCrypt
       |
       v
New password hash
       |
       v
Database
```

---

# 26. Role Change Security

A normal user should not be allowed to change another user's role.

The application checks:

```java
if (updatedUser.getRole() != null
        && updatedUser.getRole() != existingUser.getRole()) {
```

Then it checks the current authenticated user's authority.

```java
boolean isAdmin =
    authentication.getAuthorities()
        .stream()
        .anyMatch(authority ->
            authority.getAuthority()
                .equals("ROLE_ADMIN")
        );
```

If the current user is not an admin:

```text
Only ADMIN can change user roles
```

The request should result in:

```http
403 Forbidden
```

---

# 27. Why ROLE_ADMIN Is Used

Spring Security uses the convention:

```text
hasRole("ADMIN")
```

which corresponds to:

```text
ROLE_ADMIN
```

Similarly:

```java
hasRole("INSTRUCTOR")
```

means:

```text
ROLE_INSTRUCTOR
```

And:

```java
hasRole("STUDENT")
```

means:

```text
ROLE_STUDENT
```

Therefore, the JWT filter should ultimately create authorities such as:

```text
ROLE_STUDENT
ROLE_INSTRUCTOR
ROLE_ADMIN
```

---

# 28. JWT Authentication

When a user logs in, the authentication system generates a JWT.

Conceptually:

```text
Email + Password
       |
       v
Authentication
       |
       v
JWT
       |
       v
Client/Postman
```

The client sends the token on protected requests:

```http
Authorization: Bearer <JWT>
```

---

# 29. JWT Request Flow

Example:

```http
GET /api/users/admin-test
Authorization: Bearer eyJhbGciOi...
```

The request enters:

```text
JwtAuthenticationFilter
```

The filter:

1. Reads the Authorization header.
2. Extracts the Bearer token.
3. Validates the JWT.
4. Gets the email/subject.
5. Finds the user.
6. Gets the user's role.
7. Creates an Authentication object.
8. Places it into SecurityContext.
9. Spring Security checks authorization.
10. Controller executes.

---

# 30. SecurityContext

The filter creates something conceptually like:

```text
UsernamePasswordAuthenticationToken

Principal:
    instructor@example.com

Authorities:
    ROLE_INSTRUCTOR

Authenticated:
    true
```

The important part is:

```text
Authenticated = true
```

and:

```text
Authorities = ROLE_INSTRUCTOR
```

---

# 31. Security Configuration

The security rules are approximately:

```java
.requestMatchers("/api/auth/login")
    .permitAll()

.requestMatchers("/api/users/student-test")
    .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

.requestMatchers("/api/users/instructor-test")
    .hasAnyRole("INSTRUCTOR", "ADMIN")

.requestMatchers("/api/users/admin-test")
    .hasRole("ADMIN")

.requestMatchers("/api/users")
    .permitAll()

.requestMatchers("/api/users/**")
    .authenticated()

.anyRequest()
    .authenticated()
```

---

# 32. Meaning of 401

HTTP `401 Unauthorized` means:

```text
The request is not successfully authenticated.
```

Typical causes:

```text
No Authorization header
Invalid JWT
Expired JWT
JWT signature invalid
JWT parsing failed
Authentication was never placed in SecurityContext
```

Example:

```http
GET /api/users/admin-test
```

without:

```http
Authorization: Bearer <token>
```

results in:

```http
401 Unauthorized
```

---

# 33. Meaning of 403

HTTP `403 Forbidden` is different.

It means:

```text
The user is authenticated,
but does not have sufficient permission.
```

Example:

```text
Current user:
ROLE_INSTRUCTOR

Endpoint:
GET /api/users/admin-test

Required:
ROLE_ADMIN
```

Result:

```http
403 Forbidden
```

This distinction was important during testing.

---

# 34. Authentication vs Authorization

Always remember:

```text
Authentication
      =
"Who are you?"
```

```text
Authorization
      =
"What are you allowed to do?"
```

Example:

```text
JWT valid
    |
    v
Authentication successful
    |
    v
ROLE_INSTRUCTOR
    |
    v
Admin endpoint?
    |
    v
NO
    |
    v
403
```

---

# 35. Security Filter Chain

The security architecture is:

```text
HTTP Request
     |
     v
JwtAuthenticationFilter
     |
     v
SecurityContext
     |
     v
AuthorizationFilter
     |
     +---- allowed ----> Controller
     |
     +---- denied ------> 401 / 403
```

The JWT filter must execute before the normal username/password authentication filter:

```java
.addFilterBefore(
    jwtAuthenticationFilter,
    UsernamePasswordAuthenticationFilter.class
)
```

---

# 36. Admin Test Endpoint

Endpoint:

```http
GET /api/users/admin-test
```

Required:

```text
ROLE_ADMIN
```

An ADMIN JWT:

```text
ADMIN JWT
   |
   v
ROLE_ADMIN
   |
   v
200 OK
```

An INSTRUCTOR JWT:

```text
INSTRUCTOR JWT
   |
   v
ROLE_INSTRUCTOR
   |
   v
403 Forbidden
```

---

# 37. Instructor Test Endpoint

Endpoint:

```http
GET /api/users/instructor-test
```

Allowed:

```text
ROLE_INSTRUCTOR
ROLE_ADMIN
```

Not allowed:

```text
ROLE_STUDENT
```

---

# 38. Student Test Endpoint

Endpoint:

```http
GET /api/users/student-test
```

Allowed:

```text
ROLE_STUDENT
ROLE_INSTRUCTOR
ROLE_ADMIN
```

---

# 39. Delete User

Endpoint:

```http
DELETE /api/users/{id}
```

Example:

```http
DELETE /api/users/9
```

The service first checks:

```java
if (!userRepository.existsById(id)) {
    throw new RuntimeException("User not found");
}
```

Then:

```java
userRepository.deleteById(id);
```

Successful response:

```http
204 No Content
```

---

# 40. Database

The application uses PostgreSQL.

Example database:

```text
learning_platform
```

Example table:

```text
users
```

Check users:

```sql
SELECT id, email, role
FROM users;
```

Example:

```text
 id | email                    | role
----+--------------------------+-----------
  3 | sujanv2@example.com      | STUDENT
  4 | testjwt@example.com      | STUDENT
  6 | password-test@example.com| STUDENT
  8 | admin@example.com        | ADMIN
  9 | instructor2@example.com  | INSTRUCTOR
```

---

# 41. PostgreSQL Docker Setup

If PostgreSQL is running through Docker, first check containers:

```bash
docker ps
```

Check all containers:

```bash
docker ps -a
```

---

# 42. Start Docker Compose

From the directory containing `docker-compose.yml`:

```bash
docker compose up -d
```

Check:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs
```

Follow logs:

```bash
docker compose logs -f
```

For only PostgreSQL:

```bash
docker compose logs -f postgres
```

If your PostgreSQL service has a different name, replace `postgres` with the actual service name from `docker-compose.yml`.

---

# 43. Stop Docker Compose

Stop containers:

```bash
docker compose down
```

Stop and remove containers:

```bash
docker compose down
```

If you intentionally want to remove the database volume too:

```bash
docker compose down -v
```

WARNING:

```text
docker compose down -v
```

can delete your PostgreSQL data stored in the Docker volume.

Do not use it casually.

---

# 44. Restart Docker Environment

A common development sequence:

```bash
docker compose down
docker compose up -d
docker compose ps
```

Then start Spring Boot.

---

# 45. Check PostgreSQL Container

Find the container:

```bash
docker ps
```

Then connect using `psql` if the container has it installed:

```bash
docker exec -it <postgres-container-name> psql -U <username> -d <database>
```

For example:

```bash
docker exec -it postgres psql -U postgres -d learning_platform
```

Use the actual container name, username, and database from your `docker-compose.yml`.

---

# 46. Useful PostgreSQL Commands

Once inside `psql`:

Show databases:

```sql
\l
```

Show tables:

```sql
\dt
```

Describe users:

```sql
\d users
```

Query users:

```sql
SELECT * FROM users;
```

Query only important fields:

```sql
SELECT id, email, role
FROM users;
```

Exit:

```sql
\q
```

---

# 47. Run Spring Boot Application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```cmd
mvnw.cmd spring-boot:run
```

If Maven is installed globally:

```bash
mvn spring-boot:run
```

---

# 48. Build the Application

```bash
./mvnw clean package
```

Windows:

```cmd
mvnw.cmd clean package
```

Skip tests if necessary:

```bash
./mvnw clean package -DskipTests
```

The generated JAR will normally be under:

```text
target/
```

---

# 49. Run the JAR

Example:

```bash
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

Use the actual JAR filename generated by Maven.

---

# 50. Typical Development Startup

The normal development sequence is:

```bash
# 1. Start PostgreSQL
docker compose up -d

# 2. Check containers
docker compose ps

# 3. Start Spring Boot
./mvnw spring-boot:run
```

Then the service should be available at:

```text
http://127.0.0.1:8081
```

---

# 51. Postman Testing Order

A good testing sequence is:

```text
1. Create Student
2. Login as Student
3. Test Student endpoint
4. Test Instructor endpoint
5. Test Admin endpoint
6. Create/prepare Instructor
7. Login as Instructor
8. Test Instructor endpoint
9. Test Admin endpoint
10. Create/prepare Admin
11. Login as Admin
12. Test Admin endpoint
13. Test PUT
14. Test role change
15. Test DELETE
16. Test validation
17. Test duplicate email
```

---

# 52. Create Student

```http
POST http://127.0.0.1:8081/api/users
```

Body:

```json
{
    "firstName": "Test",
    "lastName": "Student",
    "email": "student2@example.com",
    "password": "Student@123"
}
```

Because no role is supplied:

```text
role = null
```

Then `@PrePersist` executes:

```text
role == null
    |
    v
STUDENT
```

The database stores:

```text
STUDENT
```

---

# 53. Create Instructor

If your registration API is intentionally allowed to receive a role, the request can contain:

```json
{
    "firstName": "Test",
    "lastName": "Instructor",
    "email": "instructor2@example.com",
    "password": "Instructor@123",
    "role": "INSTRUCTOR"
}
```

However, make sure your `CreateUserRequest` actually contains the role field and that the controller copies it:

```java
user.setRole(request.getRole());
```

Otherwise the role will remain:

```text
null
```

and `@PrePersist` will convert it to:

```text
STUDENT
```

This was the reason a request containing:

```json
"role": "INSTRUCTOR"
```

could previously result in:

```text
ROLE BEFORE SAVE: null
ROLE AFTER SAVE: STUDENT
```

---

# 54. Correct Role Mapping During Creation

If registration is supposed to accept the role, the DTO needs:

```java
private Role role;
```

with:

```java
public Role getRole() {
    return role;
}

public void setRole(Role role) {
    this.role = role;
}
```

And the controller needs:

```java
user.setRole(request.getRole());
```

Then:

```text
JSON role
    |
    v
CreateUserRequest.role
    |
    v
User.role
    |
    v
@PrePersist
    |
    v
Database
```

---

# 55. Important Security Design

For a real production application, be careful about allowing anyone to send:

```json
"role": "ADMIN"
```

during public registration.

Otherwise someone could register:

```json
{
    "email": "attacker@example.com",
    "role": "ADMIN"
}
```

and become an administrator.

A safer design is:

```text
Public registration
        |
        v
Always STUDENT
```

Then only an existing ADMIN can promote a user:

```text
ADMIN
  |
  v
PUT /api/users/{id}
  |
  v
role = INSTRUCTOR / ADMIN
```

This is the recommended security model.

---

# 56. Login

Login is generally handled by the authentication endpoint:

```http
POST /api/auth/login
```

Example:

```json
{
    "email": "admin@example.com",
    "password": "Admin@123"
}
```

Successful login returns a JWT.

Conceptually:

```text
email + password
       |
       v
User lookup
       |
       v
BCrypt password verification
       |
       v
Role lookup
       |
       v
JWT generation
       |
       v
JWT returned to client
```

---

# 57. JWT Contents

A JWT commonly contains claims such as:

```json
{
    "sub": "admin@example.com",
    "role": "ADMIN",
    "iat": 1786184161,
    "exp": 1786187761
}
```

The important values are:

```text
sub  -> user identity
role -> user role
iat  -> issued-at time
exp  -> expiration time
```

The JWT is then sent with requests:

```http
Authorization: Bearer <JWT>
```

---

# 58. Postman Authorization

In Postman:

```text
Authorization
    |
    v
Type: Bearer Token
    |
    v
Paste JWT
```

Or manually in Headers:

```text
Authorization: Bearer eyJhbGciOi...
```

Do not include:

```text
Bearer Bearer eyJ...
```

Only:

```text
Bearer eyJ...
```

---

# 59. Testing ADMIN

First login as admin.

Then copy the JWT.

Request:

```http
GET http://127.0.0.1:8081/api/users/admin-test
```

Authorization:

```text
Bearer <ADMIN_JWT>
```

Expected:

```http
200 OK
```

Response:

```text
Admin access granted
```

---

# 60. Testing INSTRUCTOR

Login as instructor.

Copy the instructor JWT.

Request:

```http
GET http://127.0.0.1:8081/api/users/instructor-test
```

Expected:

```http
200 OK
```

Then:

```http
GET http://127.0.0.1:8081/api/users/admin-test
```

Expected:

```http
403 Forbidden
```

because:

```text
ROLE_INSTRUCTOR != ROLE_ADMIN
```

---

# 61. Testing STUDENT

Login as student.

Request:

```http
GET http://127.0.0.1:8081/api/users/student-test
```

Expected:

```http
200 OK
```

Then:

```http
GET http://127.0.0.1:8081/api/users/instructor-test
```

Expected:

```http
403 Forbidden
```

And:

```http
GET http://127.0.0.1:8081/api/users/admin-test
```

Expected:

```http
403 Forbidden
```

---

# 62. Testing PUT

Example:

```http
PUT http://127.0.0.1:8081/api/users/7
```

Body:

```json
{
    "firstName": "Test Updated",
    "lastName": "Instructor Updated",
    "email": "instructor2@example.com"
}
```

If password is omitted:

```text
Password remains unchanged.
```

If password is supplied:

```json
{
    "firstName": "Test Updated",
    "lastName": "Instructor Updated",
    "email": "instructor2@example.com",
    "password": "NewPassword@123"
}
```

Then the password is BCrypt encoded.

---

# 63. Testing Role Change

Suppose user `7` currently has:

```text
INSTRUCTOR
```

An ADMIN can send:

```http
PUT /api/users/7
```

with:

```json
{
    "firstName": "Test",
    "lastName": "Instructor",
    "email": "instructor2@example.com",
    "role": "ADMIN"
}
```

The service checks:

```text
Requested role != Existing role
```

Then:

```text
Who is making the request?
        |
        v
SecurityContext
        |
        v
ROLE_ADMIN?
      /   \
    YES    NO
     |      |
     v      v
 Allow     403
```

---

# 64. Instructor Attempting Role Change

If an instructor tries:

```json
{
    "role": "ADMIN"
}
```

the service detects:

```text
Current authority:
ROLE_INSTRUCTOR
```

Therefore:

```text
isAdmin = false
```

Result:

```http
403 Forbidden
```

This is correct behavior.

---

# 65. PUT 404 Troubleshooting

If:

```http
PUT /api/users/7
```

returns:

```http
404 Not Found
```

check:

```text
1. Does user ID 7 exist?
2. Is the URL correct?
3. Is @PutMapping("/{id}") present?
4. Is the application running on port 8081?
5. Is the correct service receiving the request?
```

Database check:

```sql
SELECT id, email, role
FROM users
WHERE id = 7;
```

---

# 66. PUT 401 Troubleshooting

If PUT returns:

```http
401 Unauthorized
```

check:

```text
Authorization header
JWT validity
JWT expiration
JWT filter
SecurityContext
```

Postman must contain:

```http
Authorization: Bearer <VALID_JWT>
```

---

# 67. PUT 403 Troubleshooting

If PUT reaches the service and produces:

```text
Only ADMIN can change user roles
```

then the request is authenticated but the current user is not ADMIN.

Check logs:

```text
CURRENT USER: ...
CURRENT ROLE: [ROLE_INSTRUCTOR]
REQUESTED ROLE: ADMIN
EXISTING ROLE: INSTRUCTOR
IS ADMIN: false
```

For a role change, use an ADMIN JWT.

---

# 68. 401 vs 403 vs 404 vs 409

Remember this table:

| Status | Meaning           | Example                             |
| ------ | ----------------- | ----------------------------------- |
| 200    | Success           | Admin endpoint allowed              |
| 201    | Created           | User registration                   |
| 204    | No Content        | Successful delete                   |
| 400    | Bad Request       | Validation failure                  |
| 401    | Not authenticated | Missing/invalid JWT                 |
| 403    | Not authorized    | Instructor accessing admin endpoint |
| 404    | Not found         | User ID does not exist              |
| 409    | Conflict          | Email already registered            |
| 500    | Server error      | Unhandled exception                 |

---

# 69. Duplicate Email Test

Create:

```json
{
    "firstName": "Test",
    "lastName": "User",
    "email": "admin@example.com",
    "password": "Password@123"
}
```

If the email already exists:

```java
userRepository.existsByEmail(...)
```

returns:

```text
true
```

Then:

```java
throw new EmailAlreadyExistsException(
    "Email already registered"
);
```

Expected:

```http
409 Conflict
```

---

# 70. Validation Test

Send:

```json
{
    "firstName": "",
    "lastName": "",
    "email": "invalid",
    "password": "123"
}
```

Expected:

```http
400 Bad Request
```

because of:

```text
@NotBlank
@Email
@Size
```

---

# 71. Password Security

The application follows:

```text
Client
  |
  | Plain password over HTTPS
  v
Application
  |
  | BCrypt
  v
Database
  |
  | Hashed password
  v
Stored
```

Never store:

```text
password = "Instructor@123"
```

Store:

```text
password = BCrypt hash
```

And never return it in JSON:

```java
@JsonIgnore
```

---

# 72. Complete User Creation Flow

```text
POST /api/users
        |
        v
CreateUserRequest
        |
        v
Bean Validation
        |
        +---- invalid ---> 400
        |
        v
UserController
        |
        v
UserService
        |
        v
existsByEmail()
        |
        +---- exists ----> 409
        |
        v
BCrypt password encoding
        |
        v
UserRepository.save()
        |
        v
@PrePersist
        |
        +---- role null?
        |       |
        |       YES
        |       |
        |       v
        |    STUDENT
        |
        v
PostgreSQL
        |
        v
201 Created
```

---

# 73. Complete Protected Request Flow

```text
GET /api/users/admin-test
Authorization: Bearer JWT
             |
             v
JwtAuthenticationFilter
             |
             v
Extract JWT
             |
             v
Validate JWT
             |
       +-----+-----+
       |           |
     invalid      valid
       |           |
       v           v
      401      Extract user
                   |
                   v
             Find user
                   |
                   v
             Get role
                   |
                   v
           ROLE_ADMIN
                   |
                   v
          SecurityContext
                   |
                   v
          AuthorizationFilter
                   |
             +-----+-----+
             |           |
           allowed      denied
             |           |
             v           v
        Controller      403
             |
             v
          200 OK
```

---

# 74. Complete PUT Flow

```text
PUT /api/users/7
        |
        v
JWT Filter
        |
        v
Authenticated?
   /          \
 NO            YES
 |              |
401             v
          Security Rules
                |
                v
            Controller
                |
                v
          UserService
                |
                v
        Find existing user
                |
          +-----+-----+
          |           |
       missing       found
          |           |
          v           v
         404     Update fields
                      |
                      v
              Role changed?
                 /       \
               NO         YES
               |           |
               |           v
               |       Is ADMIN?
               |         /   \
               |       YES    NO
               |        |      |
               |        v      v
               |      allow   403
               |        |
               +--------+
                    |
                    v
             Password supplied?
                    |
               +----+----+
               |         |
              NO        YES
               |         |
               |         v
               |       BCrypt
               |         |
               +----+----+
                    |
                    v
              Repository
                    |
                    v
                 Database
                    |
                    v
                 200 OK
```

---

# 75. Recommended Improvements

The current implementation works, but the next improvements should be:

## 1. Do not allow public users to choose ADMIN

Prefer:

```text
POST /api/users
        |
        v
Always STUDENT
```

Then:

```text
ADMIN
  |
  v
promote user
```

---

## 2. Use DTO for PUT

Instead of accepting:

```java
@RequestBody User user
```

create:

```text
UpdateUserRequest
```

This prevents clients from directly controlling entity fields.

---

## 3. Use dedicated exceptions

Instead of:

```java
throw new RuntimeException(...)
```

use:

```text
UserNotFoundException
EmailAlreadyExistsException
RoleChangeNotAllowedException
```

Then map them in:

```text
GlobalExceptionHandler
```

---

## 4. Return DTOs instead of entities

Instead of:

```text
Controller -> User entity -> JSON
```

prefer:

```text
Controller
    |
    v
UserResponse DTO
    |
    v
JSON
```

This gives better control over what the API exposes.

---

# 76. Recommended Final Architecture

```text
                         CLIENT
                           |
                           v
                    +-------------+
                    |   REST API  |
                    +-------------+
                           |
                           v
                 +-------------------+
                 | JWT Filter        |
                 |                   |
                 | Authentication    |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | Spring Security   |
                 | Authorization     |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | Controller        |
                 |                   |
                 | Request/Response  |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | DTO               |
                 | Validation        |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | Service           |
                 |                   |
                 | Business Logic    |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | Repository        |
                 |                   |
                 | JPA               |
                 +-------------------+
                           |
                           v
                 +-------------------+
                 | PostgreSQL        |
                 +-------------------+
```

---

# 77. Docker + Application Architecture

When PostgreSQL runs in Docker:

```text
                    HOST MACHINE
                         |
              +----------+----------+
              |                     |
              v                     v
       Spring Boot             Docker
       User Service              |
       Port 8081                 v
                         +---------------+
                         | PostgreSQL    |
                         | Container     |
                         | Port 5432     |
                         +---------------+
                                |
                                v
                          learning_platform
                                |
                                v
                              users
```

---

# 78. Daily Development Commands

## Start database

```bash
docker compose up -d
```

## Check database

```bash
docker compose ps
```

## View logs

```bash
docker compose logs -f
```

## Start Spring Boot

```bash
./mvnw spring-boot:run
```

## Build

```bash
./mvnw clean package
```

## Stop database

```bash
docker compose down
```

---

# 79. Complete Fresh Start

When starting the project from scratch:

```bash
# Start PostgreSQL
docker compose up -d

# Verify containers
docker compose ps

# Start application
./mvnw spring-boot:run
```

Then test:

```text
POST   /api/users
POST   /api/auth/login

GET    /api/users
GET    /api/users/{id}
GET    /api/users/email/{email}

GET    /api/users/student-test
GET    /api/users/instructor-test
GET    /api/users/admin-test

PUT    /api/users/{id}

DELETE /api/users/{id}
```

---

# 80. Database Verification

After creating a user:

```sql
SELECT id, email, role
FROM users
ORDER BY id;
```

To verify the password is hashed:

```sql
SELECT id, email, password
FROM users;
```

The password should look like a BCrypt hash rather than the original password.

Do not expose this query through an API.

---

# 81. Final Mental Model

The entire User Service can be remembered using this:

```text
REQUEST
   |
   v
JWT FILTER
   |
   +---- no/invalid JWT ----> 401
   |
   v
SECURITY CONTEXT
   |
   v
ROLE CHECK
   |
   +---- insufficient role --> 403
   |
   v
CONTROLLER
   |
   v
DTO + VALIDATION
   |
   +---- invalid -----------> 400
   |
   v
SERVICE
   |
   +---- duplicate email ---> 409
   |
   +---- user missing ------> 404
   |
   +---- role violation ----> 403
   |
   v
BCrypt / BUSINESS LOGIC
   |
   v
REPOSITORY
   |
   v
POSTGRESQL
   |
   v
RESPONSE
```

---

# 82. Most Important Rules

### Rule 1 — Password

```text
NEVER store plain passwords.
ALWAYS BCrypt encode them.
```

### Rule 2 — Password response

```text
NEVER return passwords.
Use @JsonIgnore or response DTOs.
```

### Rule 3 — Authentication

```text
JWT answers:
"Who is this user?"
```

### Rule 4 — Authorization

```text
ROLE answers:
"What can this user do?"
```

### Rule 5 — 401

```text
Authentication problem.
```

### Rule 6 — 403

```text
Authorization problem.
```

### Rule 7 — 404

```text
Requested resource does not exist.
```

### Rule 8 — 409

```text
Request conflicts with existing data.
Example: duplicate email.
```

### Rule 9 — Role changes

```text
Only ADMIN should be allowed
to change another user's role.
```

### Rule 10 — Registration

```text
Public registration should normally
create STUDENT users.

ADMIN promotion should be protected.
```

---

# 83. Final End-to-End Example

Suppose:

```text
admin@example.com
ROLE_ADMIN
```

logs in.

The system generates:

```text
ADMIN JWT
```

Postman sends:

```http
PUT /api/users/9
Authorization: Bearer <ADMIN_JWT>
```

with:

```json
{
    "firstName": "Instructor",
    "lastName": "Updated",
    "email": "instructor2@example.com",
    "role": "INSTRUCTOR"
}
```

The complete flow is:

```text
Postman
   |
   v
JWT Filter
   |
   v
JWT valid
   |
   v
admin@example.com
   |
   v
ROLE_ADMIN
   |
   v
SecurityContext
   |
   v
PUT Controller
   |
   v
UserService
   |
   v
Find user ID 9
   |
   v
Role changed?
   |
   YES
   |
   v
Is current user ADMIN?
   |
   YES
   |
   v
Change role
   |
   v
Save user
   |
   v
PostgreSQL
   |
   v
200 OK
```

This is the complete architecture of the User Service.

---

# 84. Quick Reference

```text
Application
-----------
Spring Boot
Port: 8081

Database
--------
PostgreSQL
Database: learning_platform
Table: users
Port: 5432

Security
--------
JWT
Spring Security
BCrypt

Roles
-----
STUDENT
INSTRUCTOR
ADMIN

Endpoints
---------
POST   /api/users
GET    /api/users
GET    /api/users/{id}
GET    /api/users/email/{email}
PUT    /api/users/{id}
DELETE /api/users/{id}

Security Tests
--------------
GET /api/users/student-test
GET /api/users/instructor-test
GET /api/users/admin-test

Status Codes
------------
200 OK
201 CREATED
204 NO CONTENT
400 BAD REQUEST
401 UNAUTHORIZED
403 FORBIDDEN
404 NOT FOUND
409 CONFLICT
500 INTERNAL SERVER ERROR

Docker
------
docker compose up -d
docker compose ps
docker compose logs -f
docker compose down

Maven
-----
./mvnw spring-boot:run
./mvnw clean package
```

---

# 85. One-Line Architecture Summary

```text
Client → JWT Filter → Spring Security → Controller → DTO/Validation → Service → Repository/JPA → PostgreSQL
```

And for authorization:

```text
JWT → User → Role → SecurityContext → Permission Check → 200 / 401 / 403
```
