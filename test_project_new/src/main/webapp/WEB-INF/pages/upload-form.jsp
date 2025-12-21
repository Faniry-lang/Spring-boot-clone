<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload File</title>
</head>
<body>
    <h2>Upload a File</h2>
    <form action="/paiement/upload" method="post" enctype="multipart/form-data">
        <label for="file">Choose file:</label>
        <input type="file" id="file" name="file"><br><br>
        <input type="submit" value="Upload">
    </form>
</body>
</html>
