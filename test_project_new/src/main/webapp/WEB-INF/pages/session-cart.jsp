<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Panier - Session</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 700px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .cart-box {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .cart-item {
            background-color: #fff3e0;
            padding: 12px;
            margin: 8px 0;
            border-radius: 4px;
            border-left: 4px solid #ff9800;
        }
        .empty-cart {
            background-color: #f5f5f5;
            padding: 20px;
            text-align: center;
            border-radius: 5px;
            color: #666;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin: 5px;
        }
        .btn:hover {
            background-color: #45a049;
        }
        .btn-warning {
            background-color: #ff9800;
        }
        .btn-warning:hover {
            background-color: #e68900;
        }
        .btn-danger {
            background-color: #f44336;
        }
        .btn-danger:hover {
            background-color: #da190b;
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        h1 {
            color: #333;
        }
    </style>
</head>
<body>
    <div class="cart-box">
        <h1>🛒 Mon Panier</h1>

        <%
            String message = (String) request.getAttribute("message");
            if (message != null) {
        %>
            <div class="success-message">
                <%= message %>
            </div>
        <% } %>

        <%
            @SuppressWarnings("unchecked")
            List<String> cart = (List<String>) request.getAttribute("cart");

            if (cart != null && !cart.isEmpty()) {
        %>
            <p><strong>Nombre d'articles:</strong> <%= cart.size() %></p>

            <h3>Articles dans le panier:</h3>
            <%
                int index = 1;
                for (String item : cart) {
            %>
                <div class="cart-item">
                    <strong>#<%= index++ %></strong> - <%= item %>
                </div>
            <% } %>

            <div style="margin-top: 20px;">
                <a href="${pageContext.request.contextPath}/session/cart/clear" class="btn btn-danger">🗑️ Vider le panier</a>
            </div>
        <% } else { %>
            <div class="empty-cart">
                <h3>😔 Votre panier est vide</h3>
                <p>Ajoutez des articles pour commencer !</p>
            </div>
        <% } %>

        <div style="margin-top: 30px; border-top: 1px solid #ddd; padding-top: 20px;">
            <h3>➕ Ajouter des articles</h3>
            <a href="${pageContext.request.contextPath}/session/cart/add?item=Livre" class="btn btn-warning">Ajouter: Livre</a>
            <a href="${pageContext.request.contextPath}/session/cart/add?item=Stylo" class="btn btn-warning">Ajouter: Stylo</a>
            <a href="${pageContext.request.contextPath}/session/cart/add?item=Cahier" class="btn btn-warning">Ajouter: Cahier</a>
            <a href="${pageContext.request.contextPath}/session/cart/add?item=Sac" class="btn btn-warning">Ajouter: Sac</a>
        </div>

        <div style="margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/session/home" class="btn">🏠 Retour à l'accueil</a>
        </div>
    </div>

    <div style="margin-top: 20px; padding: 15px; background-color: #e3f2fd; border-radius: 5px;">
        <h4>💡 À propos du panier en session</h4>
        <p>Le panier est stocké dans la session sous forme de <code>List&lt;String&gt;</code>.</p>
        <p>Grâce à l'annotation <code>@Session</code>, le contrôleur peut accéder et modifier cette liste.</p>
        <p>Les modifications sont automatiquement persistées dans la <code>HttpSession</code>.</p>
    </div>
</body>
</html>

