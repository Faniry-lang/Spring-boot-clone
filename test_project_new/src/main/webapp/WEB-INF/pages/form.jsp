<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <html>

    <head>
        <title>User Form</title>
    </head>

    <body>
        <h2>Enter User Details</h2>
        <form action="save-user" method="post">
            <label for="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName"><br><br>

            <label for="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName"><br><br>

            <input type="submit" value="Submit">
        </form>
    </body>

    </html>