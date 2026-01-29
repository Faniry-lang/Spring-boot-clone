# Quick Start Guide: Setup a New Project

This guide explains how to create a minimal project from scratch using our framework.

## 1. Project Structure
Your project should follow the standard Maven layout:

```text
my-web-app/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/app/
        │       ├── Application.java        (EntryPoint)
        │       └── controllers/           (Your Controllers)
        └── webapp/
            └── WEB-INF/
                ├── web.xml                (Servlet Configuration)
                └── pages/                 (Your JSPs)
                    └── index.jsp
```

## 2. Maven Configuration (`pom.xml`)
Copy this minimal `pom.xml`. It includes the framework dependency and the plugin required to build a standalone executable JAR.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.app</groupId>
    <artifactId>my-web-app</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.itu.framework</groupId>
            <artifactId>framework</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.4.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>com.app.Application</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## 3. The Application Class (`Application.java`)
This is your entry point. Annotate it with `@FrameworkApplication`.

```java
package com.app;

import com.itu.framework.FrameworkRunner;
import com.itu.framework.annotations.FrameworkApplication;

@FrameworkApplication(port = 8080)
public class Application {
    public static void main(String[] args) {
        FrameworkRunner.run(Application.class, args);
    }
}
```

## 4. Servlet Configuration (`src/main/webapp/WEB-INF/web.xml`)
Configure the `FrontServlet` and tell it where to find your controllers and views.

```xml
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0">
    <servlet>
        <servlet-name>FrontServlet</servlet-name>
        <servlet-class>com.itu.framework.FrontServlet</servlet-class>
        <init-param>
            <param-name>controller-package</param-name>
            <param-value>com.app.controllers</param-value>
        </init-param>
        <init-param>
            <param-name>view-prefix</param-name>
            <param-value>/WEB-INF/pages/</param-value>
        </init-param>
        <init-param>
            <param-name>view-suffix</param-name>
            <param-value>.jsp</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>FrontServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

## 5. Your First Controller (`com/app/controllers/HelloController.java`)
```java
package com.app.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GetMapping;

@Controller("/hello")
public class HelloController {
    @GetMapping("/world")
    public String greet() {
        return "Hello from your new framework!";
    }
}
```

## 6. Build and Run
1. Open your terminal in the project root.
2. Build the project:
   ```bash
   mvn package
   ```
3. Run the standalone JAR:
   ```bash
   java -jar target/my-web-app-1.0-SNAPSHOT.jar
   ```
4. Open your browser at: `http://localhost:8080/hello/world`
