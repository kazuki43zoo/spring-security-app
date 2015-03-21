<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
</head>
<body>
<div id="wrapper">
    <h3>Login with Username and Password</h3>

    <c:if test="${param.error}">
        ${SPRING_SECURITY_LAST_EXCEPTION}
        <c:out value="${SPRING_SECURITY_LAST_EXCEPTION}"/>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/login">
        <table>
            <tr>
                <td><label for="username">User:</label></td>
                <td><input type="text" id="username"
                           name="username" value='demo'>(demo)
                </td>
            </tr>
            <tr>
                <td><label for="password">Password:</label></td>
                <td><input type="password" id="password"
                           name="password" value="demo"/>(demo)
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><input name="submit" type="submit" value="Login"/></td>
            </tr>
        </table>
    </form:form>
</div>
</body>
</html>