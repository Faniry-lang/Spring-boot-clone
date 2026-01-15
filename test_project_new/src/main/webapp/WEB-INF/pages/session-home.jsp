<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.test.models.Student" %>
<!DOCTYPE html>
<html>
<head>
    <title>Session Test - Home</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 900px;
            margin: 20px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .header {
            background-color: #4CAF50;
            color: white;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .session-info {
            background-color: #e3f2fd;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #2196F3;
        }
        .student-list {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:hover {
            background-color: #f5f5f5;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
        }
        .btn:hover {
            background-color: #45a049;
        }
        .btn-logout {
            background-color: #f44336;
        }
        .btn-logout:hover {
            background-color: #da190b;
        }
        .btn-info {
            background-color: #2196F3;
        }
        .btn-info:hover {
            background-color: #0b7dda;
        }
        .btn-cart {
            background-color: #ff9800;
        }
        .btn-cart:hover {
            background-color: #e68900;
        }
        .links {
            margin-top: 20px;
        }
        .links a {
            margin-right: 10px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🎓 Gestion de Session - Sprint 11</h1>
        <p>Test de l'annotation @Session avec Map&lt;String, Object&gt;</p>
    </div>

    <div class="session-info">
        <h3>📊 Informations de Session</h3>
        <% Student currentStudent = (Student) request.getAttribute("currentStudent"); %>
        <% if (currentStudent != null) { %>
            <p><strong>✅ Utilisateur connecté:</strong> <%= currentStudent.getName() %> (<%= currentStudent.getEmail() %>)</p>
        <% } else { %>
            <p><strong>❌ Aucun utilisateur connecté</strong></p>
        <% } %>
        <p><strong>🔢 Nombre de visites:</strong> <%= request.getAttribute("visitCount") %></p>
    </div>

    <div class="student-list">
        <h2>Liste des Étudiants</h2>
        <p>Cliquez sur "Se connecter" pour stocker un étudiant dans la session</p>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Email</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <%
                    @SuppressWarnings("unchecked")
                    List<Student> students = (List<Student>) request.getAttribute("students");
                    if (students != null) {
                        for (Student student : students) {
                %>
                <tr>
                    <td><%= student.getId() %></td>
                    <td><%= student.getName() %></td>
                    <td><%= student.getEmail() %></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/session/login?id=<%= student.getId() %>" class="btn">
                            Se connecter
                        </a>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>

    <div class="links">
        <h3>🔗 Actions</h3>
        <% if (currentStudent != null) { %>
            <a href="${pageContext.request.contextPath}/session/logout" class="btn btn-logout">Se déconnecter</a>
        <% } %>
        <a href="${pageContext.request.contextPath}/session/info" class="btn btn-info">Voir détails session</a>
        <a href="${pageContext.request.contextPath}/session/cart/view" class="btn btn-cart">Voir panier</a>
        <a href="${pageContext.request.contextPath}/session/cart/add?item=Livre" class="btn btn-cart">Ajouter Livre au panier</a>
        <a href="${pageContext.request.contextPath}/session/cart/add?item=Stylo" class="btn btn-cart">Ajouter Stylo au panier</a>
    </div>

    <div style="margin-top: 30px; padding: 15px; background-color: #fff3cd; border-radius: 5px;">
        <h4>💡 Comment ça marche ?</h4>
        <ul>
            <li>L'annotation <code>@Session</code> permet d'injecter un <code>Map&lt;String, Object&gt;</code> dans les méthodes du contrôleur</li>
            <li>Les modifications apportées au Map sont automatiquement synchronisées avec la HttpSession</li>
            <li>Le compteur de visites s'incrémente à chaque rechargement de la page</li>
            <li>La connexion stocke l'étudiant dans la session</li>
            <li>Le panier simule un panier d'achat persisté en session</li>
        </ul>
    </div>
</body>
</html>

