<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Paiement Details</title>
</head>
<body>
    <h2>Détails du Paiement</h2>
    <%
        com.test.models.Paiement p = (com.test.models.Paiement) request.getAttribute("paiement");
        if (p != null) {
    %>
        <p>ID: <%= p.getId() %></p>
        <p>Amount: <%= p.getAmount() %></p>
        <p>Utilisateur ID: <%= (p.getUtilisateur()!=null? p.getUtilisateur().getId(): "n/a") %></p>
        <p>Utilisateur Name: <%= (p.getUtilisateur()!=null? p.getUtilisateur().getName(): "n/a") %></p>
    <% } else { %>
        <p>No paiement provided.</p>
    <% } %>
</body>
</html>
