<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.test.models.Student" %>
<!DOCTYPE html>
<html>
<head>
    <title>Déconnexion - Résultat</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .message-box {
            background-color: #fff3cd;
            border: 1px solid #ffeeba;
            color: #856404;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
        }
        .btn:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <h1>Déconnexion</h1>

    <div class="message-box">
        <h2>👋 Vous êtes maintenant déconnecté</h2>
        <%
            Student student = (Student) request.getAttribute("student");
            if (student != null) {
        %>
            <p>Au revoir <strong><%= student.getName() %></strong> !</p>
        <% } %>
        <p>Les informations de session ont été supprimées.</p>
    </div>

    <a href="${pageContext.request.contextPath}/session/home" class="btn">Retour à l'accueil</a>
</body>
</html>

