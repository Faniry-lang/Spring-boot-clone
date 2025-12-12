**Spring-Boot clone**

**Project**
- **Name**: `Spring-Boot clone` — a lightweight Java web framework inspired by Spring Boot's developer experience. Built for learning and prototyping.

**Overview**
- **Framework module**: `framework_new` — core framework code (`FrontServlet`, annotations, helpers, `ModelView`).
- **Example app**: `test_project_new` — demo web application (WAR) that uses the framework to validate features.

**Features**
- **Annotation-based controllers**: `@Controller`, `@UrlMapping`, `@GET`, `@POST`.
- **Request binding**: automatic binding for primitive parameters and complex objects (including nested objects).
- **Collection binding**: supports `List` and `Set` from repeated parameters or indexed notation (`items[0].field`).
- **Date parsing**: parses `Date` values from common formats (`yyyy-MM-dd`, `yyyy-MM-dd'T'HH:mm:ss`, `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`).
- **View resolution**: `ModelView` with configurable prefix/suffix (set in `web.xml`) to forward to JSP pages.
- **Component scanning**: automatic discovery of controllers via `ComponentScan`.

**Quick Start**
- Build framework:
```
mvn -f framework_new/pom.xml -DskipTests package
```
- Build demo app (WAR):
```
mvn -f test_project_new/pom.xml -DskipTests package
```
- Deploy the generated WAR (`test_project_new/target/test_project.war`) to your servlet container (Tomcat, Jetty, etc.).

**Tomcat deployment (step-by-step)**
1. Install Apache Tomcat (9+ recommended) and start it locally.
2. Build the demo WAR as shown above. The WAR file is created at `test_project_new/target/test_project.war`.
3. Copy the WAR into Tomcat's `webapps/` directory, or use Tomcat Manager to upload it.
   - Example (Linux/macOS):
     ```bash
     cp test_project_new/target/test_project.war /path/to/tomcat/webapps/
     ```
4. Start Tomcat (if not already running):
   ```bash
   /path/to/tomcat/bin/startup.sh
   ```
5. Tomcat will explode the WAR and deploy it automatically. Watch logs in `logs/catalina.out`.
6. Access the demo pages in your browser. If the WAR is deployed under context `test_project`, open:
   - `http://localhost:8080/test_project/paiement/form` — example payment form.
7. Submit the form to exercise POST binding; the framework will forward to configured JSP pages (see `web.xml` for `view-prefix`/`view-suffix`).

**Controller examples**
Below are example controllers and usage patterns to illustrate how to write controllers with this framework.

- Simple controller returning a `ModelView`:

```java
package com.test.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.UrlMapping;
import com.itu.framework.view.ModelView;

@Controller("/hello")
public class HelloController {

    @UrlMapping("/greeting")
    public ModelView sayHello() {
        ModelView mv = new ModelView("hello");
        mv.addObject("name", "Faniry");
        return mv;
    }
}
```

- Controller with path variable and simple return string:

```java
@UrlMapping("/{name}")
public String sayHelloToName(String name) {
    return "Hello " + name + "!";
}
```

- Controller using `@RequestParam`:

```java
@UrlMapping("/search")
public String search(@RequestParam("q") String query) {
    return "Searching for: " + query;
}
```

- Controller handling POST and binding a complex object (object and nested object binding):

```java
@POST
@UrlMapping("/save-payment")
public ModelView savePayment(Payment payment) {
    // 'payment' is populated from form fields like payment.amount, payment.user.id, ...
    ModelView mv = new ModelView("payment-details");
    mv.addObject("payment", payment);
    return mv;
}
```

Form naming conventions supported by the binder:
- Primitive fields: `firstName`, `amount`
- Nested object fields: `payment.user.id`, `payment.user.name`
- Collections (simple values): `tags=one&tags=two` or repeated inputs with same name
- Indexed nested objects: `items[0].name`, `items[1].name` (useful for arrays/lists)

**Binding details**
- The binder collects request parameters into a `LinkedHashMap<String,String[]>` and attempts to:
  - map to method parameters by name or `@RequestParam` annotation,
  - instantiate complex objects and set fields via setters (or direct field access when no setter),
  - convert values to `int`, `long`, `double`, `Date`, and fallback to `String` when unknown,
  - build `List`/`Set` either from repeated parameters or by detecting indexed parameter names.

**Tests**
- Run unit tests for the framework:
```
mvn -f framework_new/pom.xml test
```



