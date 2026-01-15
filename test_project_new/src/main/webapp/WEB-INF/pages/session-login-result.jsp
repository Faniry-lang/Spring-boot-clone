<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.test.models.Student" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion - Résultat</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .message-box {
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .success {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        .error {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
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
    <h1>Résultat de la Connexion</h1>

    <%
        Boolean success = (Boolean) request.getAttribute("success");
        Student student = (Student) request.getAttribute("student");

        if (success != null && success) {
    %>
        <div class="message-box success">
            <h2>✅ Connexion réussie !</h2>
            <p><strong>Étudiant:</strong> <%= student.getName() %></p>
            <p><strong>Email:</strong> <%= student.getEmail() %></p>
            <p>Les informations de l'étudiant ont été stockées dans la session.</p>
        </div>
    <% } else { %>
        <div class="message-box error">
            <h2>❌ Échec de la connexion</h2>
            <p>Étudiant non trouvé. Veuillez vérifier l'ID.</p>
        </div>
    <% } %>

    <a href="${pageContext.request.contextPath}/session/home" class="btn">Retour à l'accueil</a>
    <a href="${pageContext.request.contextPath}/session/info" class="btn">Voir les détails de session</a>
</body>
</html>

