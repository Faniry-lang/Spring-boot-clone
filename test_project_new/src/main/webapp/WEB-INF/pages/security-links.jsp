<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Security Links</title>
</head>
<body>
<h1>Liens de test sécurité</h1>
<p>Utilisateur: ${user}</p>
<ul>
    <li><a href="${ctx}/security/anonym">Accès anonyme (toujours accessible)</a></li>
    <li><a href="security/authorized">Accès autorisé (nécessite connexion)</a></li>
    <li><a href="security/role">Accès ROLE=ADMIN (nécessite rôle ADMIN)</a></li>
</ul>
<h2>Formulaire de login</h2>
<form action="security/login" method="get">
    Nom d'utilisateur: <input type="text" name="username" /> <br/>
    Rôle: <select name="role">
        <option value="USER">USER</option>
        <option value="ADMIN">ADMIN</option>
    </select>
    <button type="submit">Se connecter</button>
</form>
<form action="security/logout" method="get">
    <button type="submit">Se déconnecter</button>
</form>
</body>
</html>
