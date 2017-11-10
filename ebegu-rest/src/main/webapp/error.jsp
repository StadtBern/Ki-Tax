<%@page isErrorPage="true" contentType="text/html" %>
<html>
<body>
<p>Internal Error occured at
    <%= new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date()) %>
</p>
<p>Please inform the adminstrator of this application</p>
</body>
</html>