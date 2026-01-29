# PR: Transition from WAR-based Deployment to Embedded Tomcat (Standalone JAR)

## Overview
This pull request introduces the capability for the framework to run as a standalone application using an embedded Tomcat server, mirroring the behavior of Spring Boot. Previously, the framework was limited to being bundled as a WAR file and deployed to an external servlet container.

## Key Changes and New Functionalities

### 1. Embedded Server Bootstrap
We have integrated `tomcat-embed-core` and `tomcat-embed-jasper` to allow the framework to start its own server instance.
- **`ServerApplication.java`**: Provides the low-level Tomcat initialization logic.
- **`FrameworkRunner.java`**: Coordinates the startup process, including temp directory management for web resources.

### 2. Standalone Application Entry Point
Introduction of a Spring-Boot-style entry point.
- **`@FrameworkApplication`**: A new annotation to mark the main class.
- **`FrameworkRunner.run()`**: A static method to bootstrap the application with minimal code (2 lines in the `main` method).

### 3. JAR-Compatible Component Scanning
The `ComponentScan` logic was overhauled to support scanning classes inside a JAR file using the `jar:` protocol, which is essential for `java -jar` execution.

### 4. Automatic Web Resource Extraction
Since Tomcat requires a physical directory for certain web operations (like JSP compilation), the framework now automatically extracts `WEB-INF` and other resources from the JAR to a temporary system directory at runtime.

### 5. Programmatic Lifecycle Management
The framework now handles its own shutdown hooks to ensure Tomcat is properly stopped when the JVM exits.

---

## Comparison: WAR vs. Embedded JAR

| Feature | Old (WAR-based) | New (Embedded JAR) |
| :--- | :--- | :--- |
| **Packaging** | `war` | `jar` |
| **Server Requirements** | External Tomcat installation required | No external server required (Self-contained) |
| **Deployment** | Manual copy of WAR to `webapps/` | Just run `java -jar application.jar` |
| **Configuration** | Heavy `web.xml` and server config | Minimal annotation + local `web.xml` |
| **Scanning** | Local filesystem scanning only | Supports JAR and Filesystem scanning |
| **Development Speed** | Slower (Re-deploy needed) | Faster (Direct execution) |

---

## Precise List of Modified/Added Files

### `framework_new` (Framework Core)
- **`pom.xml`**: Added Tomcat embedded dependencies. Removed `provided` scope to make them transitive.
- **`src/main/java/com/itu/framework/annotations/FrameworkApplication.java`** [NEW]: Application marker annotation.
- **`src/main/java/com/itu/framework/FrameworkRunner.java`** [NEW]: Orchestrator for the embedded bootstrapper.
- **`src/main/java/com/itu/framework/server/ServerApplication.java`** [NEW]: Tomcat instance configuration logic.
- **`src/main/java/com/itu/framework/helpers/ComponentScan.java`**: Implemented `jar:file` protocol support for class scanning.
- **`src/main/java/com/itu/framework/FrontServlet.java`**: Added startup debug logging to trace URL mapping during initialization.

### `test_project_new` (Example/Validation Project)
- **`pom.xml`**: Simplified to a single dependency on the framework. Added `maven-shade-plugin` for Fat-JAR creation.
- **`src/main/java/com/test/Application.java`**: Refactored to use `@FrameworkApplication` and `FrameworkRunner`.
- **`src/main/webapp/WEB-INF/web.xml`**: Configuration preserved but now extracted and used by the embedded server.

## Future Roadmap
- Implementation of an application-wide properties file (`application.properties`).
- Support for multiple servlet-mappings without manual `web.xml` intervention.
- Integration of a default error page handling mechanism.
