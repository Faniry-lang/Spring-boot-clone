<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Login Result</title>
</head>
<body>
<h1>Résultat du login</h1>
<% Boolean success = (Boolean) request.getAttribute("success"); %>
<% if (success != null && success) { %>
    <p>Connecté: <%= request.getAttribute("username") %> (role: <%= request.getAttribute("role") %>)</p>
<% } else { %>
    <p>Non connecté</p>
<% } %>
<p><a href="/test_project/security/links">Retour aux liens</a></p>
</body>
</html>
