# Framework Documentation

> **Optimist Spring Boot Clone** - Un framework MVC léger inspiré de Spring Boot pour Java.

Ce framework fournit une architecture MVC complète avec annotations, gestion de session, sécurité, upload de fichiers et serveur Tomcat embarqué.

---

## Table des matières

1. [Configuration initiale](#1-configuration-initiale)
2. [Controllers et URL Mapping](#2-controllers-et-url-mapping)
3. [ModelView et transfert d'objets](#3-modelview-et-transfert-dobjets)
4. [Redirection](#4-redirection)
5. [API REST et JSON](#5-api-rest-et-json)
6. [RequestParam et binding de paramètres](#6-requestparam-et-binding-de-paramètres)
7. [Gestion de la Session](#7-gestion-de-la-session)
8. [Sécurité et Authorization](#8-sécurité-et-authorization)
9. [Upload de fichiers](#9-upload-de-fichiers)
10. [Tomcat Embarqué](#10-tomcat-embarqué)

---

## 1. Configuration initiale

### 1.1 Structure du projet

```
mon-projet/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── monapp/
│       │           ├── Application.java
│       │           ├── controllers/
│       │           └── models/
│       ├── resources/
│       └── webapp/
│           └── WEB-INF/
│               ├── web.xml
│               ├── security-config.xml
│               └── pages/
│                   └── *.jsp
└── pom.xml
```

### 1.2 Configuration web.xml

Le fichier `web.xml` configure le `FrontServlet` qui intercepte toutes les requêtes :

```xml
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
    <servlet>
        <servlet-name>FrontServlet</servlet-name>
        <servlet-class>com.itu.framework.FrontServlet</servlet-class>
        <init-param>
            <param-name>controller-package</param-name>
            <param-value>com.monapp.controllers</param-value>
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

**Paramètres importants :**
- `controller-package` : Package contenant vos controllers
- `view-prefix` : Préfixe du chemin des vues JSP
- `view-suffix` : Extension des fichiers de vue

### 1.3 Configuration security-config.xml

Ce fichier définit les clés de session utilisées pour la sécurité :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<security>
    <keys>
        <role>userRole</role>
        <authorized>user</authorized>
        <anonym>anon</anonym>
    </keys>
</security>
```

**Clés :**
- `role` : Nom de l'attribut de session contenant le rôle de l'utilisateur
- `authorized` : Nom de l'attribut indiquant que l'utilisateur est connecté
- `anonym` : Nom de l'attribut pour les utilisateurs anonymes

---

## 2. Controllers et URL Mapping

### 2.1 Créer un Controller

Utilisez l'annotation `@Controller` pour définir un controller avec son URL de base :

```java
package com.monapp.controllers;

import com.itu.framework.annotations.Controller;
import com.itu.framework.annotations.GetMapping;
import com.itu.framework.annotations.PostMapping;
import com.itu.framework.view.ModelView;

@Controller("/hello")
public class HelloController {

    @GetMapping("/greeting")
    public ModelView sayHello() {
        ModelView mv = new ModelView("hello");
        mv.addObject("message", "Bonjour le monde!");
        return mv;
    }

    @GetMapping("/bye")
    public String sayGoodbye() {
        return "Au revoir!";
    }
}
```

**URLs générées :**
- `GET /hello/greeting` → méthode `sayHello()`
- `GET /hello/bye` → méthode `sayGoodbye()`

### 2.2 Mapping GET et POST

```java
@Controller("/users")
public class UserController {

    // Requête GET
    @GetMapping("/list")
    public ModelView listUsers() {
        ModelView mv = new ModelView("user-list");
        // ... logique
        return mv;
    }

    // Requête POST
    @PostMapping("/create")
    public ModelView createUser(User user) {
        // ... création utilisateur
        return ModelView.redirect("/users/list");
    }
}
```

### 2.3 Variables de chemin (Path Variables)

Utilisez les accolades `{...}` pour capturer des segments de l'URL :

```java
@Controller("/products")
public class ProductController {

    @GetMapping("/{id}")
    public ModelView getProduct(int id) {
        // id est automatiquement extrait de l'URL
        ModelView mv = new ModelView("product-detail");
        mv.addObject("productId", id);
        return mv;
    }

    @GetMapping("/{category}/{id}")
    public ModelView getProductByCategory(String category, int id) {
        // Les deux variables sont extraites
        ModelView mv = new ModelView("product-detail");
        mv.addObject("category", category);
        mv.addObject("productId", id);
        return mv;
    }
}
```

**Exemple d'appel :** `GET /products/electronics/42`

---

## 3. ModelView et transfert d'objets

### 3.1 Création d'un ModelView

Le `ModelView` permet de retourner une vue avec des données :

```java
@GetMapping("/home")
public ModelView home() {
    ModelView mv = new ModelView("home");  // → /WEB-INF/pages/home.jsp
    mv.addObject("title", "Page d'accueil");
    mv.addObject("user", currentUser);
    return mv;
}
```

### 3.2 Vues dans des sous-dossiers

Vous pouvez organiser vos JSP dans des sous-dossiers :

```java
@GetMapping("/form")
public ModelView showForm() {
    // Vue située dans /WEB-INF/pages/reservation/reservation-form.jsp
    ModelView mv = new ModelView("reservation/reservation-form");
    mv.addObject("hotels", hotelList);
    return mv;
}
```

### 3.3 Accès aux données dans JSP

Dans votre fichier JSP, accédez aux données via les attributs de requête :

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>${title}</title>
</head>
<body>
    <h1>Bienvenue ${user.name}!</h1>
    
    <!-- Avec scriptlet -->
    <p><%= request.getAttribute("title") %></p>
    
    <!-- Avec EL (Expression Language) -->
    <p>${title}</p>
    
    <!-- Lien avec context path -->
    <a href="${ctx}/users/list">Liste des utilisateurs</a>
</body>
</html>
```

> **Note :** La variable `${ctx}` contient automatiquement le context path de l'application.

---

## 4. Redirection

### 4.1 Redirection simple

Utilisez la méthode statique `ModelView.redirect()` :

```java
@PostMapping("/save")
public ModelView saveData(MyObject data) {
    try {
        data.save();
        return ModelView.redirect("/success");
    } catch (Exception e) {
        return ModelView.redirect("/error");
    }
}
```

### 4.2 Redirection avec paramètres

```java
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@PostMapping("/save")
public ModelView saveData(MyObject data) {
    try {
        data.save();
        return ModelView.redirect("/success?id=" + data.getId());
    } catch (Exception e) {
        // IMPORTANT: Encodez les paramètres pour éviter les caractères spéciaux
        String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        return ModelView.redirect("/error?message=" + encodedMessage + "&link=/form");
    }
}
```

### 4.3 Méthodes alternatives de redirection

```java
// Méthode 1: Factory statique
return ModelView.redirect("/target");

// Méthode 2: Setter
ModelView mv = new ModelView();
mv.setRedirectView("/target");
return mv;

// Méthode 3: Configuration manuelle
ModelView mv = new ModelView("/target");
mv.setRedirect(true);
return mv;
```

> **Important :** Utilisez `&` (et non `&&`) pour séparer les paramètres de query string.

---

## 5. API REST et JSON

### 5.1 Annotation @Json

L'annotation `@Json` permet de retourner directement du JSON :

```java
import com.itu.framework.annotations.Json;
import java.util.List;

@Controller("/api/users")
public class UserApiController {

    @GetMapping("/all")
    @Json
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Json
    public User getUser(int id) {
        return userService.findById(id);
    }

    @PostMapping("/create")
    @Json
    public User createUser(User user) {
        user.save();
        return user;
    }
}
```

**Réponse automatique :**
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
}
```

### 5.2 JSON avec ModelView

Vous pouvez aussi utiliser `@Json` avec un `ModelView` - seules les données seront sérialisées :

```java
@GetMapping("/stats")
@Json
public ModelView getStats() {
    ModelView mv = new ModelView();
    mv.addObject("totalUsers", 150);
    mv.addObject("activeUsers", 42);
    return mv;
}
```

**Réponse :**
```json
{
    "totalUsers": 150,
    "activeUsers": 42
}
```

---

## 6. RequestParam et binding de paramètres

### 6.1 Annotation @RequestParam

Utilisez `@RequestParam` pour extraire des paramètres de la query string :

```java
@Controller("/search")
public class SearchController {

    @GetMapping("/query")
    public ModelView search(@RequestParam("q") String query,
                            @RequestParam("page") int page) {
        ModelView mv = new ModelView("search-results");
        mv.addObject("query", query);
        mv.addObject("page", page);
        return mv;
    }
}
```

**Appel :** `GET /search/query?q=java&page=1`

### 6.2 Binding automatique de paramètres

Les paramètres peuvent être bindés automatiquement par nom :

```java
@GetMapping("/filter")
public ModelView filter(String category, int minPrice, int maxPrice) {
    // Les paramètres sont automatiquement extraits de la requête
    // si leurs noms correspondent aux noms des paramètres de méthode
    ModelView mv = new ModelView("filtered-products");
    return mv;
}
```

**Appel :** `GET /filter?category=books&minPrice=10&maxPrice=50`

### 6.3 Binding d'objets

Le framework peut instancier et remplir automatiquement des objets :

```java
// Classe DTO
public class CreateReservation {
    private int nbPassager;
    private int idClient;
    private int idHotel;
    private String dateHeureArrivee;
    
    // Constructeur par défaut OBLIGATOIRE
    public CreateReservation() {}
    
    // Getters et setters...
}

// Controller
@PostMapping("/save")
public ModelView saveReservation(CreateReservation dto) {
    // L'objet dto est automatiquement rempli depuis les paramètres du formulaire
    Reservation r = new Reservation();
    r.setNbPassager(dto.getNbPassager());
    r.save();
    return ModelView.redirect("/success");
}
```

**Formulaire HTML correspondant :**
```html
<form action="${ctx}/reservations/save" method="post">
    <input type="number" name="nbPassager" />
    <input type="number" name="idClient" />
    <input type="number" name="idHotel" />
    <input type="datetime-local" name="dateHeureArrivee" />
    <button type="submit">Réserver</button>
</form>
```

> **Note :** La classe doit avoir un constructeur public sans arguments.

---

## 7. Gestion de la Session

### 7.1 Annotation @Session

Injectez la session HTTP dans vos méthodes avec `@Session` :

```java
import com.itu.framework.annotations.Session;
import java.util.Map;

@Controller("/session")
public class SessionController {

    @GetMapping("/home")
    public ModelView home(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("home");
        
        // Lire depuis la session
        Object user = session.get("currentUser");
        mv.addObject("user", user);
        
        // Écrire dans la session
        Integer visitCount = (Integer) session.get("visitCount");
        if (visitCount == null) visitCount = 0;
        session.put("visitCount", ++visitCount);
        
        return mv;
    }
}
```

### 7.2 Connexion / Déconnexion

```java
@Controller("/auth")
public class AuthController {

    @PostMapping("/login")
    public ModelView login(@Session Map<String, Object> session,
                           String username, String password) {
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            // Stocker l'utilisateur en session
            session.put("user", user);
            session.put("userRole", user.getRole());
            session.put("loginTime", System.currentTimeMillis());
            return ModelView.redirect("/dashboard");
        } else {
            return ModelView.redirect("/login?error=invalid");
        }
    }

    @GetMapping("/logout")
    public ModelView logout(@Session Map<String, Object> session) {
        // Supprimer les données de session
        session.remove("user");
        session.remove("userRole");
        session.remove("loginTime");
        return ModelView.redirect("/login");
    }
}
```

### 7.3 Panier d'achat (exemple)

```java
@Controller("/cart")
public class CartController {

    @GetMapping("/add")
    public ModelView addToCart(@Session Map<String, Object> session, int productId) {
        @SuppressWarnings("unchecked")
        List<Integer> cart = (List<Integer>) session.get("cart");
        
        if (cart == null) {
            cart = new ArrayList<>();
            session.put("cart", cart);
        }
        
        cart.add(productId);
        
        return ModelView.redirect("/cart/view");
    }

    @GetMapping("/view")
    public ModelView viewCart(@Session Map<String, Object> session) {
        ModelView mv = new ModelView("cart");
        mv.addObject("cart", session.get("cart"));
        return mv;
    }

    @GetMapping("/clear")
    public ModelView clearCart(@Session Map<String, Object> session) {
        session.remove("cart");
        return ModelView.redirect("/cart/view");
    }
}
```

---

## 8. Sécurité et Authorization

### 8.1 Annotations de sécurité

Le framework fournit trois annotations pour contrôler l'accès :

| Annotation | Description |
|------------|-------------|
| `@Anonym` | Accès autorisé à tous (même non connectés) |
| `@Authorized` | Accès uniquement aux utilisateurs connectés |
| `@Role({"ROLE1", "ROLE2"})` | Accès réservé aux utilisateurs ayant un des rôles spécifiés |

### 8.2 Configuration des clés de session

Dans `security-config.xml`, définissez les noms des attributs de session :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<security>
    <keys>
        <role>userRole</role>        <!-- Attribut contenant le rôle -->
        <authorized>user</authorized> <!-- Attribut indiquant la connexion -->
    </keys>
</security>
```

### 8.3 Exemple complet

```java
@Controller("/admin")
public class AdminController {

    // Accessible à tous
    @GetMapping("/public")
    @Anonym
    public ModelView publicPage() {
        return new ModelView("public");
    }

    // Accessible uniquement si session.get("user") != null
    @GetMapping("/dashboard")
    @Authorized
    public ModelView dashboard() {
        return new ModelView("dashboard");
    }

    // Accessible si session.get("userRole") contient "ADMIN" ou "SUPER_ADMIN"
    @GetMapping("/settings")
    @Role({"ADMIN", "SUPER_ADMIN"})
    public ModelView settings() {
        return new ModelView("admin-settings");
    }

    // Accessible uniquement aux ADMIN
    @GetMapping("/users")
    @Role({"ADMIN"})
    public ModelView manageUsers() {
        return new ModelView("user-management");
    }
}
```

### 8.4 Comportement en cas de refus d'accès

Si l'utilisateur n'a pas les droits requis, le framework retourne une erreur HTTP 403 (Forbidden).

### 8.5 Connexion avec rôle

```java
@Controller("/auth")
public class AuthController {

    @PostMapping("/login")
    public ModelView login(@Session Map<String, Object> session,
                           String username, String password) {
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            // Stocker les informations requises par security-config.xml
            session.put("user", user);           // pour @Authorized
            session.put("userRole", user.getRole()); // pour @Role
            return ModelView.redirect("/admin/dashboard");
        }
        return ModelView.redirect("/login?error=1");
    }
}
```

---

## 9. Upload de fichiers

### 9.1 Recevoir des fichiers uploadés

Le framework gère automatiquement les requêtes `multipart/form-data` :

```java
@Controller("/upload")
public class UploadController {

    @PostMapping("/file")
    public ModelView uploadFile(Map<String, byte[]> files) {
        // files contient: clé = nom du fichier original, valeur = contenu en bytes
        
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            String fileName = entry.getKey();
            byte[] content = entry.getValue();
            
            // Sauvegarder le fichier
            Files.write(Paths.get("uploads/" + fileName), content);
        }
        
        ModelView mv = new ModelView("upload-result");
        mv.addObject("uploadedFiles", files.keySet());
        return mv;
    }
}
```

### 9.2 Formulaire HTML pour upload

```html
<form action="${ctx}/upload/file" method="post" enctype="multipart/form-data">
    <input type="file" name="document" />
    <input type="file" name="photo" multiple />
    <button type="submit">Envoyer</button>
</form>
```

### 9.3 Upload avec autres champs

```java
@PostMapping("/document")
public ModelView uploadDocument(String title, 
                                String description,
                                Map<String, byte[]> files) {
    // title et description sont des champs texte du formulaire
    // files contient les fichiers uploadés
    
    Document doc = new Document();
    doc.setTitle(title);
    doc.setDescription(description);
    
    if (!files.isEmpty()) {
        Map.Entry<String, byte[]> firstFile = files.entrySet().iterator().next();
        doc.setFileName(firstFile.getKey());
        doc.setContent(firstFile.getValue());
    }
    
    doc.save();
    return ModelView.redirect("/documents/list");
}
```

---

## 10. Tomcat Embarqué

### 10.1 Configuration de l'application

Créez une classe principale avec l'annotation `@FrameworkApplication` :

```java
package com.monapp;

import com.itu.framework.FrameworkRunner;
import com.itu.framework.annotations.FrameworkApplication;

@FrameworkApplication(port = 8080)
public class Application {

    public static void main(String[] args) {
        FrameworkRunner.run(Application.class, args);
    }
}
```

### 10.2 Changer le port au runtime

Vous pouvez spécifier un port différent via les arguments :

```bash
java -jar mon-application.jar --port=9090
```

### 10.3 Configuration Maven pour Fat JAR

Ajoutez le plugin `maven-shade-plugin` dans votre `pom.xml` :

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>com.monapp.Application</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 10.4 Dépendance framework

```xml
<dependency>
    <groupId>com.itu</groupId>
    <artifactId>framework</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 10.5 Lancement

```bash
# Compilation
mvn clean package

# Exécution
java -jar target/mon-application-shaded.jar

# Avec port personnalisé
java -jar target/mon-application-shaded.jar --port=8081
```

**Sortie attendue :**
```
FrontServlet: Scanning controllers in package: com.monapp.controllers
FrontServlet: Mapped 5 URLs.
Mapped URL: /hello/greeting
Mapped URL: /users/list
...
Started embedded Tomcat on port 8080
```

---

## Annexe A: Récapitulatif des annotations

| Annotation | Cible | Description |
|------------|-------|-------------|
| `@Controller("/base")` | Classe | Déclare un controller avec URL de base |
| `@GetMapping("/path")` | Méthode | Mappe une requête GET |
| `@PostMapping("/path")` | Méthode | Mappe une requête POST |
| `@RequestParam("name")` | Paramètre | Extrait un paramètre de requête |
| `@Session` | Paramètre | Injecte la map de session |
| `@Json` | Méthode | Retourne du JSON au lieu d'une vue |
| `@Anonym` | Méthode | Accès public (sans authentification) |
| `@Authorized` | Méthode | Requiert une authentification |
| `@Role({"R1","R2"})` | Méthode | Requiert un rôle spécifique |
| `@FrameworkApplication` | Classe | Point d'entrée avec Tomcat embarqué |

---

## Annexe B: Bonnes pratiques

1. **Constructeurs par défaut** : Toutes les classes utilisées pour le binding doivent avoir un constructeur public sans arguments.

2. **Compilation avec -parameters** : Pour que le binding par nom fonctionne, compilez avec l'option `-parameters` :
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-compiler-plugin</artifactId>
       <configuration>
           <compilerArgs>
               <arg>-parameters</arg>
           </compilerArgs>
       </configuration>
   </plugin>
   ```

3. **Encodage URL** : Toujours encoder les valeurs dans les redirections :
   ```java
   String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
   return ModelView.redirect("/error?msg=" + encoded);
   ```

4. **Context path** : Utilisez `${ctx}` dans les JSP pour les liens :
   ```jsp
   <a href="${ctx}/users/list">Liste</a>
   ```

5. **Séparateur de paramètres** : Utilisez `&` (et non `&&`) dans les query strings.

6. **Sécurité** : Placez les JSP dans `/WEB-INF/pages/` pour empêcher l'accès direct.

---

## Annexe C: Dépannage

### Erreur 404 - URL not found
- Vérifiez que le controller-package dans `web.xml` est correct
- Vérifiez les annotations `@Controller` et `@GetMapping`/`@PostMapping`
- Consultez les logs au démarrage pour voir les URLs mappées

### Erreur 403 - Access denied
- Vérifiez que l'utilisateur est connecté (session contient les bonnes clés)
- Vérifiez le `security-config.xml` pour les noms des clés
- Vérifiez les annotations `@Role`, `@Authorized`, `@Anonym`

### Erreur 500 - Error executing method
- Consultez les logs du serveur pour l'exception complète
- Vérifiez que les classes DTO ont un constructeur par défaut
- Vérifiez les types de paramètres (conversion automatique limitée)

### JSP non trouvée
- Vérifiez le `view-prefix` et `view-suffix` dans `web.xml`
- Vérifiez que le fichier JSP existe dans le bon dossier
- Pour les sous-dossiers : `new ModelView("folder/page")` → `/WEB-INF/pages/folder/page.jsp`

---

*Documentation Framework v1.0*
*Dernière mise à jour : Février 2026*
