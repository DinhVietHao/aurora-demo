<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> 
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Aurora Demo</title>
        <meta charset="UTF-8" />
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 2rem;
            }
            h1 {
                color: #2c7be5;
            }
            p {
                color: #555;
            }
        </style>
    </head>
    <body>
        <h1>Xin chào, Aurora!</h1>
        <p>Ứng dụng JSP đang chạy thành công trên Jetty 12 🚀</p>

        <h3>Kiểm tra JSTL</h3>
        <c:set var="now" value="<%= new java.util.Date() %>" />
        <p>Thời gian hiện tại: <strong>${now}</strong></p>

        <p>
            <a href="WEB-INF/views/auth/login.jsp"
                >Đi tới trang Login (nếu có)</a
            >
        </p>
    </body>
</html>