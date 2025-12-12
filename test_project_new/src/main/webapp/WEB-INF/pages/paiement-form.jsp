<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Paiement Form</title>
</head>
<body>
    <h2>Créer un Paiement</h2>
    <form action="/paiement/save" method="post">
        <label for="id">Paiement ID:</label>
        <input type="text" id="id" name="id"><br><br>

        <label for="amount">Amount:</label>
        <input type="text" id="amount" name="amount"><br><br>

        <label for="utilisateur">Utilisateur:</label>
        <select id="utilisateur" name="utilisateur.id">
            <% java.util.List users = (java.util.List) request.getAttribute("users");
               if (users != null) {
                   for (Object o : users) {
                       com.test.models.Utilisateur u = (com.test.models.Utilisateur) o;
            %>
                       <option value="<%=u.getId()%>"><%=u.getName()%></option>
            <%     }
               }
            %>
        </select>
        <br><br>

        <input type="submit" value="Submit">
    </form>
</body>
</html>
