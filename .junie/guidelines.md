Project: linkai-api (Spring Boot 3.5, Java 24)

This document captures project-specific build, test, and development practices to help future contributors. It assumes familiarity with Maven, Spring Boot, JUnit 5, and MockMvc.

1) Build and Configuration
- JDK: Java 24 (pom.xml -> <java.version>24</java.version>). Use a JDK distribution that provides toolchain support for Java 24.
- Build tool: Maven (wrapper included: mvnw / mvnw.cmd).
- Key dependencies: Spring Boot (web, security, data-jpa, validation), Lombok, java-jwt, PostgreSQL driver, H2 (runtime + test), spring-security-test.
- Profiles / Properties:
  - Default application.properties uses PostgreSQL via Docker Compose service name:
    - spring.datasource.url=jdbc:postgresql://postgres:5432/linkai_db
    - spring.datasource.username=linkai / password=secret
    - spring.jpa.hibernate.ddl-auto=update
    - api.security.token.secret=my-secret-key
  - Test profile (ActiveProfiles("test")) is configured via src/test/resources/application-test.properties:
    - H2 in-memory DB; ddl-auto=create-drop
- UrlIdGeneratorService requires property url.id.secret-key at runtime. For tests that bootstrap the full context and hit this bean, define it in the active profile (e.g., add to application-test.properties) or provide a TestConfiguration bean override. Current unit tests don’t require it, but integration tests that wire the whole context might.
- Docker: compose.yaml is present to orchestrate Postgres. Run docker compose up -d (or Docker Desktop) before starting the app with the default profile.

Build commands
- Clean build with tests: .\mvnw.cmd -q -DskipTests=false clean verify
- Build without tests: .\mvnw.cmd -q -DskipTests clean package
- Run app (default profile): .\mvnw.cmd spring-boot:run

Running with different DBs
- Postgres (default): Ensure Docker compose is up, or point spring.datasource.url to a reachable Postgres instance.
- H2 (local dev): Add a dev profile (application-dev.properties) mirroring application-test.properties if you prefer in-memory DB during development.

2) Testing
- Stack: JUnit 5, Spring Boot Test, MockMvc, spring-security-test, H2 in-memory DB for @SpringBootTest with @ActiveProfiles("test").
- How to run all tests: .\mvnw.cmd -q -DskipTests=false test
- How to run a single test by FQN using the JetBrains runner in this environment: use the run_test tool with a path or FQN. Examples the team validated:
  - run_test src\test\java\br\com\matheus161\linkai_api\services\AuthServiceTest.java
  - run_test src\test\java\br\com\matheus161\linkai_api\GuidelinesSmokeTest.java (used below, then removed)

Integration test base
- BaseIntegrationTest sets up MockMvc and ObjectMapper with @SpringBootTest, @AutoConfigureMockMvc, @Transactional, @ActiveProfiles("test").
- Utilities provided:
  - registerAndGetToken(name, email, password): posts to /auth/register and returns JWT from response.
  - performAuthenticatedPost(url, token, body): helper for authenticated POST with JSON body.
  - getUserByEmail(email): fetches a persisted User from UserRepository.
- When adding new integration tests:
  - Extend BaseIntegrationTest to get fully wired context and helpers.
  - Prefer unique test data to avoid collisions; H2 schema is reset per test class due to @Transactional + create-drop.
  - If interacting with security components, leverage spring-security-test for authentication/authorization scenarios.

Adding unit tests
- Pure unit tests (e.g., for services) can avoid context startup. Use Mockito and JUnit 5; spring-boot-starter-test already includes necessary dependencies.
- Example approach used in existing tests: AuthServiceTest stubs repositories and encoders; no DB needed.

Property caveats for tests
- If a new test wires UrlIdGeneratorService, ensure property url.id.secret-key is present in application-test.properties (e.g., url.id.secret-key=test-secret). Without it, context creation will fail.

3) Demo: creating and running a simple test
- A minimal smoke test (GuidelinesSmokeTest) was created and executed to validate the flow:
  - File path: src/test/java/br/com/matheus161/linkai_api/GuidelinesSmokeTest.java
  - Content:
    - @Test void simpleAssertion() { assertEquals(2, 1 + 1); }
  - Result: Passed locally using the run_test tool.
- After verification, this file has been removed to keep the repository clean, as per the task requirement. If you need a similar template later, you can re-create it quickly following the snippet above.

4) Additional development notes
- DTOs live under br.com.matheus161.linkai_api.dto; controllers under controllers; services under services with I* interfaces; repositories under repositories; domain entities under domain.*.
- Link entity uses a 7-char hash as @Id, with redirectId unique. Be careful with persistence when generating short IDs (collisions must be handled at service level if not guaranteed unique by generator or DB constraint).
- Security: TokenService uses java-jwt. app property api.security.token.secret must be set consistently across environments.
- Testing profile uses H2 dialect and create-drop; SQL logging can be enabled if deeper debugging is needed via spring.jpa.show-sql=true in application-test.properties.
- When writing new @SpringBootTest classes, always annotate with @ActiveProfiles("test") to use H2 and avoid touching Postgres.
- Prefer constructor injection in new beans. Follow existing package naming and conventions.

5) Troubleshooting
- Context fails with missing property url.id.secret-key: add to the active profile properties or supply a TestConfiguration bean to construct UrlIdGeneratorService with a literal key.
- Port or DB connection errors at runtime: if running with default application.properties, ensure Docker Compose Postgres is healthy, or override datasource properties for local dev.
- Lombok issues in IDE: enable annotation processing; Maven build already configures annotation processor.
- Java version mismatch: ensure JAVA_HOME points to JDK 24; Maven enforcer isn’t configured, so build may proceed with another JDK but might cause runtime/compiler inconsistencies.

6) Useful Maven invocations
- Run integration tests only (by naming pattern): .\mvnw.cmd -Dtest=*IntegrationTest test
- Run a single method: .\mvnw.cmd -Dtest=AuthServiceTest#should\ create\ user\ successfully\ when\ everything\ is\ ok test
- Skip tests for faster packaging: .\mvnw.cmd -DskipTests package

7) Optional: running with Docker Compose
- Compose file defines Postgres service reachable at host name postgres. Start it before spring-boot:run when using default properties:
  - docker compose up -d
  - .\mvnw.cmd spring-boot:run

End of guidelines.