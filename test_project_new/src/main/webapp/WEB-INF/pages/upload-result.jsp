<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload Result</title>
</head>
<body>
    <h2>Upload Result</h2>
    <p>Filename: <%= request.getAttribute("filename") %></p>
    <p>Size: <%= request.getAttribute("size") %> bytes</p>
</body>
</html>
