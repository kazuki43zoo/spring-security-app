<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Account Information</title>
</head>
<body>
<div id="wrapper">
    <h1>Account Information</h1>
    <table>
        <tr>
            <th>Username</th>
            <td><c:out value="${account.username}"/></td>
        </tr>
        <tr>
            <th>First name</th>
            <td><c:out value="${account.firstName}"/></td>
        </tr>
        <tr>
            <th>Last name</th>
            <td><c:out value="${account.lastName}"/></td>
        </tr>
    </table>
</div>
</body>
</html>
