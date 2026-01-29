# Spring-Boot-Clone Framework: Internal Architecture and Developer Guide

## 1. Introduction
This framework is a lightweight implementation of a Java web framework inspired by Spring Boot. It provides a structured MVC pattern, dependency injection through reflection, and an embedded servlet container (Tomcat) for standalone execution. The goal is to abstract the complexity of Java Servlet programming while providing high performance and ease of use.

## 2. Global Architecture
The framework follows the "Double Dispatch" or "Front Controller" pattern. At its heart lies the `FrontServlet`, which intercepts all incoming HTTP requests and routes them to the appropriate "Controller" method.

### Key Layers:
- **Routing Layer**: Handles URL mapping and pattern matching.
- **Binding Layer**: Converts HTTP request data (parameters, parts, session) into Java objects.
- **Service/Logic Layer**: The user-defined controllers.
- **View/Response Layer**: Handles JSP rendering or JSON serialization.
- **Bootstrap Layer**: The embedded Tomcat server and resource management.

---

## 3. Deep Dive into FrontServlet
The `FrontServlet` (`com.itu.framework.FrontServlet`) is the most critical component. It extends `HttpServlet` and overrides `init`, `doGet`, and `doPost`.

### 3.1 Initialization (`init` method)
When the server starts:
1. It reads `init-params` from `web.xml` (controller package, view prefix/suffix).
2. It triggers the `ComponentScan`.
3. It loads the security configuration from `WEB-INF/security-config.xml`.
4. It builds a global `urlMappings` map.

### 3.2 Request Processing (`processRequest`)
For every request:
1. **Path Computation**: It extracts the relative path (e.g., `/hello/greet`).
2. **Mapping Lookout**: It searches the `urlMappings` map for a match. It supports:
   - Direct matching (`/hello`)
   - Regex-based matching for path variables (`/user/{id}`).
3. **Authorization Check**: It checks if the matched method is allowed for the current session state.
4. **Argument Binding**: It uses reflection to instantiate arguments for the controller method.
5. **Invocation**: The controller method is executed.
6. **Result Handling**: 
   - If `@Json` is present: Serializes to JSON using Jackson.
   - If `ModelView` is returned: Forwards to a JSP.
   - If `String` is returned: Prints directly to the response.

---

## 4. Component Scanning (`ComponentScan`)
How does the framework know which classes are controllers?
The `ComponentScan` utility performs a deep scan of the specified package.

### 4.1 Supporting Multiple Protocols
To support both Development (IDE) and Production (Standalone JAR), the scanner handles:
- **`file:` protocol**: Direct file system access during development.
- **`jar:` protocol**: Recursive entry scanning inside the Fat JAR.

### 4.2 Annotation Discovery
It looks for classes marked with `@Controller`. For each such class, it introspects methods marked with `@GetMapping` or `@PostMapping`. It combines the class level base URL and method level URL to form the full endpoint.

---

## 5. Reflection and Parameter Binding
One of the most "Spring-like" features is the automatic binding of parameters.

### 5.1 Simple Types
The framework automatically converts strings to `int`, `long`, `double`, and `Date`.

### 5.2 Object Binding
If a controller method takes a custom object (e.g., `User user`), the framework:
1. Instantiates the object.
2. Iterates through its fields.
3. Looks for matching request parameters (e.g., `user.name` or just `name`).
4. Recursively populates nested objects.

### 5.3 Specialized Annotations
- **`@RequestParam("key")`**: Binds a specific named request parameter.
- **`@Session`**: Injects a proxy map that synchronizes changes back to the `HttpSession`.
- **`@Authorized` / `@Role`**: Declarative security at the method level.

---

## 6. The New Standalone Bootstrap Logic
With the update to an embedded server, we introduced `FrameworkRunner`.

### 6.1 Programmatic Tomcat
Instead of a separate `catalina` process, we use:
```java
Tomcat tomcat = new Tomcat();
tomcat.setPort(port);
tomcat.addWebapp("/", webappDir.getAbsolutePath());
```
This is executed within the application's process.

### 6.2 Managing the Transience of JARs
Since `web.xml` and JSPs cannot be served directly from within a JAR by a standard Tomcat context, the framework performs an "Explosion" at startup:
1. It creates a temporary directory in `java.io.tmpdir`.
2. It copies everything under `WEB-INF/` from the JAR to this directory.
3. It tells Tomcat to use this temp directory as the "DocBase".

---

## 7. Security Mechanism
The framework provides a pluggable security config.

### 7.1 `security-config.xml`
Defined in your webapp, this file tells the framework:
- Which session key holds the user's login status.
- Which session key holds the user's role.

### 7.2 Annotation Enforcement
- **`isMethodAuthorized`**: This internal method in `FrontServlet` checks the session before allowing any method invocation. If a method requires a "ADMIN" role and the session doesn't have it, a `403 Forbidden` is returned immediately.

---

## 8. View Engine and Response Handling
The framework remains un-opinionated about the response, but provides high-level wrappers.

### 8.1 `ModelView`
The `ModelView` object allows developers to:
- Define the JSP name.
- Add "Items" (data) to the model.
The framework automatically Transfers these items into `HttpServletRequest` attributes before forwarding, making them accessible via EL in JSPs (e.g., `${item}`).

### 8.2 JSON Support
By annotating a method with `@Json`, you bypass the view engine. The framework uses the Jackson library to serialize the object returned by the controller directly into the response stream with the `application/json` content type.

---

## 9. File Upload Handling
The framework abstracts the complex `Part` API of servlets.
- It detects `multipart/form-data`.
- It reads files into `byte[]`.
- It can inject a `Map<String, byte[]>` into your controller, where keys are filenames.

---

## 10. Summary of Annotations

| Annotation | Description | Location |
| :--- | :--- | :--- |
| `@Controller` | Marks a class as a web controller. | Class |
| `@GetMapping` | Defines a GET endpoint URL. | Method |
| `@PostMapping` | Defines a POST endpoint URL. | Method |
| `@RequestParam` | Sets the source parameter name for binding. | Parameter |
| `@Session` | Injects the HTTP session map. | Parameter |
| `@Json` | Signals JSON serialization of the return value. | Method |
| `@Authorized` | Restricts access to authenticated users. | Method |
| `@Role` | Restricts access to specific user roles. | Method |
| `@Anonym` | Explicitly allows public access. | Method |
| `@FrameworkApplication` | Mark the main class for standalone JAR. | Class |

---

## 11. Customizing the Startup
You can pass arguments to your application to override defaults.
Usage: `java -jar app.jar --port=9090`

The `FrameworkRunner` parses these arguments and passes them to the internal `Tomcat` instance.

## 12. Troubleshooting
- **404 Not Found**: Check if your Controller is in the package specified in `web.xml`. Ensure `@GetMapping` or `@PostMapping` starts with `/`.
- **ClassCastException**: Ensure your `@Session` parameter is of type `Map<String, Object>`.
- **Binding Failure**: Ensure your model classes have a default no-args constructor and standard getters/setters.

---

## 13. Advanced: The Mapping Registry
The `Mapping` class (`com.itu.framework.helpers.Mapping`) stores:
- `className`: The full name of the controller class.
- `methodName`: The name of the method to invoke.
- `httpMethod`: "GET" or "POST".

When multiple URLs match (e.g., regex vs literal), the framework prioritize certain patterns. The internal `findMappingForPath` handles this logic recursively.

---

## 14. Conclusion
This framework provides a robust foundation for building modern Java web applications without the overhead of massive industry frameworks, while maintaining the same ergonomic developer experience. By combining reflection, annotations, and embedded servers, it proves that simplicity and power can coexist in the Java ecosystem.

---
*Document Version: 2.1.0*
*Last Updated: 2026-01-29*
*Author: Framework Core Team*
