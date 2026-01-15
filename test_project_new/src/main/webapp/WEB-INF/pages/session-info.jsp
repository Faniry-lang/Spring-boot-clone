<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.test.models.Student" %>
<!DOCTYPE html>
<html>
<head>
    <title>Informations de Session</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .info-box {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .session-data {
            background-color: #e8f5e9;
            padding: 15px;
            border-radius: 5px;
            margin-top: 15px;
        }
        .session-item {
            padding: 8px;
            margin: 5px 0;
            background-color: white;
            border-left: 3px solid #4CAF50;
            padding-left: 10px;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin-top: 10px;
        }
        .btn:hover {
            background-color: #45a049;
        }
        h1 {
            color: #333;
        }
        h3 {
            color: #4CAF50;
        }
    </style>
</head>
<body>
    <div class="info-box">
        <h1>📋 Détails de la Session</h1>

        <% Student currentStudent = (Student) request.getAttribute("currentStudent"); %>
        <% if (currentStudent != null) { %>
            <h3>👤 Utilisateur Connecté</h3>
            <div class="session-data">
                <p><strong>ID:</strong> <%= currentStudent.getId() %></p>
                <p><strong>Nom:</strong> <%= currentStudent.getName() %></p>
                <p><strong>Email:</strong> <%= currentStudent.getEmail() %></p>

                <%
                    Long duration = (Long) request.getAttribute("sessionDuration");
                    if (duration != null) {
                %>
                    <p><strong>⏱️ Durée de la session:</strong> <%= duration %> secondes</p>
                <% } %>
            </div>
        <% } else { %>
            <p>❌ Aucun utilisateur connecté</p>
        <% } %>

        <h3>📦 Contenu de la Session</h3>
        <div class="session-data">
            <%
                @SuppressWarnings("unchecked")
                Map<String, Object> sessionData = (Map<String, Object>) request.getAttribute("sessionData");

                if (sessionData != null && !sessionData.isEmpty()) {
                    for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
            %>
                <div class="session-item">
                    <strong><%= entry.getKey() %>:</strong>
                    <%= entry.getValue() != null ? entry.getValue().toString() : "null" %>
                </div>
            <%
                    }
                } else {
            %>
                <p>La session est vide</p>
            <% } %>
        </div>

        <a href="${pageContext.request.contextPath}/session/home" class="btn">Retour à l'accueil</a>
    </div>

    <div class="info-box">
        <h3>ℹ️ Explication Technique</h3>
        <p>Cette page affiche toutes les données stockées dans la session grâce à l'annotation <code>@Session</code>.</p>
        <p>Le contrôleur reçoit un <code>Map&lt;String, Object&gt;</code> qui contient toutes les clés/valeurs de la <code>HttpSession</code>.</p>
        <ul>
            <li><strong>currentStudent</strong>: L'objet Student stocké lors de la connexion</li>
            <li><strong>visitCount</strong>: Compteur de visites incrémenté à chaque page</li>
            <li><strong>loginTime</strong>: Timestamp de la connexion (en millisecondes)</li>
            <li><strong>cart</strong>: Liste d'articles du panier (si ajoutés)</li>
        </ul>
    </div>
</body>
</html>

