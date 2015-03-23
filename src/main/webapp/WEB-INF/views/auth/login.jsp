<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
</head>
<body>
<div id="wrapper">
    <h3>Login with Username and Password</h3>

    <c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null}">
        <span style="color: red">
            <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
            <c:remove var="SPRING_SECURITY_LAST_EXCEPTION"/>
        </span>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/login"
               modelAttribute="loginForm">
        <table>
            <tr>
                <td><form:label path="username">User:</form:label></td>
                <td><form:input path="username"/>(demo)</td>
                <td><form:errors path="username"/></td>
            </tr>
            <tr>
                <td><form:label path="password">Password:</form:label></td>
                <td><form:password path="password" showPassword="true"/>(demo)</td>
                <td><form:errors path="password"/></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><form:button>Login</form:button></td>
            </tr>
        </table>
    </form:form>
</div>
</body>
</html>