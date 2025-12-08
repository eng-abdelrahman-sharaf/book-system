# Spring Boot & Git Conventions Cheat Sheet

## Spring Boot Conventions

### 1. Project Structure

    src/main/java/com/example/project
    ├── ProjectApplication.java     # Main Spring Boot application
    ├── config/                     # Configuration classes
    ├── controller/                 # REST controllers
    ├── service/                    # Business logic
    ├── repository/                 # Data access (JPA repositories)
    ├── model/                      # Entities / domain models
    └── dto/                        # Data Transfer Objects
    
    src/main/resources
    ├── application.properties      # Config file
    ├── static/                     # Static files (js, css, images)
    └── templates/                  # Thymeleaf templates

### 2. Class Naming

PascalCase

- **Controller:** `SomethingController`\
- **Service:** `SomethingService`\
- **Repository:** `SomethingRepository`\
- **Entity:** `Something` or `SomethingEntity`\
- **DTO:** `SomethingDto`

### 3. Bean Naming

camelCase based on class name:

``` java
@Service
public class UserService { } // Bean name = userService
```

### 4. Configuration Properties

Use `application.properties` or `application.yml`.\
Lowercase with dots:

    server.port=8080
    spring.datasource.url=jdbc:mysql://localhost:3306/db

### 5. REST API Conventions

- Lowercase, plural nouns\
- Examples:
  - `GET /users`\
  - `POST /users`\
  - `GET /users/{id}`\
  - `PUT /users/{id}`\
  - `DELETE /users/{id}`

Use proper HTTP status codes.

### 6. Exception Handling

Use `@ControllerAdvice` for global exception handling.

### 7. Dependency Injection

Prefer constructor injection:

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### 8. Logging

Use SLF4J + Lombok:

```java
@Slf4j
@Service
public class UserService {
    public void doSomething() {
        log.info("Doing something");
    }
}
```

------------------------------------------------------------------------

## Git Conventions

### 1. Branch Naming

- `main` / `master`: Production\
- `develop`: Integration\
- `feature/xyz`, `bugfix/issue-123`, `hotfix/urgent-fix`

### 2. Commit Message Conventions (Conventional Commits)

Format:

    <type>[optional scope]: <description>
    
    [optional body]
    
    [optional footer]

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:

    feat(user): add JWT authentication
    
    Added login endpoint and JWT token generation.

### 3. .gitignore

    # IDE
    .idea/
    *.iml
    
    # Maven / Gradle
    target/
    build/
    
    # OS
    .DS_Store
    Thumbs.db

### 4. Merging / Pull Requests

- Always branch off `main`
- Use pull requests for code review
- Squash commits if needed

### 5. Tagging / Releases

### Semantic Versioning (e.g 2.0.0)

Given a version number MAJOR.MINOR.PATCH, increment the:

1. MAJOR version when youy make incompatible API changes
2. MINOR version when you add functionality in a backward compatible manner.
3. PATCH version when you make backward compatible bug fixes.

Additional labels for pre-release and built metadata are available as extensions to the MAJOR.MINOR.PATCH format.

------------------------------------------------------------------------

### Tips

- Keep Spring Boot projects layered and clean\
- Commit messages should be concise\
- Follow REST and DI best practices\
- Use descriptive branch names
