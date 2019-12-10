<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%
    response.sendRedirect(request.getContextPath() + "/product?method=index");
%>
</body>
</html>
