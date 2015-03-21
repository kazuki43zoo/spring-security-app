<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
</head>

<sec:authentication property="principal.account" var="account" />

<body>
Welcome a home page !
<p>
    Welcome <c:out value="${account.firstName}" /> <c:out value="${account.lastName}" /> !!
</p>
<p>
    <form:form action="${pageContext.request.contextPath}/logout">
        <button>Logout</button>
    </form:form>
</p>
<ul>
    <li><a href="${pageContext.request.contextPath}/account">view account</a></li>
</ul>
</body>
</html>
