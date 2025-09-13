<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- 
  Nếu bạn muốn tự động chuyển về /home (HomeServlet) ngay khi vào root,
  bỏ comment dòng dưới:
  <c:redirect url="${pageContext.request.contextPath}/home"/>
--%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8"/>
  <title>Aurora Demo – Index</title>

  <!-- Static assets (dùng c:url để tự thêm contextPath) -->
  <link rel="stylesheet" href="<c:url value='/assets/css/common/globals.css'/>">
  <style>
    /* Fallback nếu chưa build CSS */
    :root { color-scheme: light dark; }
    body { font-family: system-ui, sans-serif; margin: 0; }
    .wrap { max-width: 960px; margin: 40px auto; padding: 24px; }
    .card { border: 1px solid rgba(0,0,0,.1); border-radius: 12px; padding: 20px; }
    .grid { display: grid; gap: 12px; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); }
    a.btn { display:inline-block; padding:10px 14px; border-radius:10px; text-decoration:none; border:1px solid rgba(0,0,0,.2); }
  </style>
</head>
<body>
  <div class="wrap">
    <header style="display:flex;align-items:center;gap:16px;margin-bottom:16px;">
      <img src="<c:url value='/assets/images/branding/logo-header.png'/>" alt="Aurora" height="40">
      <h1 style="margin:0;">Aurora Demo</h1>
    </header>

    <div class="card">
      <p><strong>JSP/JSTL OK.</strong> Context path:
        <code>${pageContext.request.contextPath}</code>
      </p>

      <p>Giờ máy chủ:
        <fmt:formatDate value="<%= new java.util.Date() %>" pattern="HH:mm:ss dd/MM/yyyy"/>
      </p>

      <div class="grid">
        <a class="btn" href="<c:url value='/home'/>">Đi đến Trang chủ (/home)</a>
        <a class="btn" href="<c:url value='/catalog/books'/>">Danh sách sách (/catalog/books)</a>
        <a class="btn" href="<c:url value='/cart'/>">Giỏ hàng (/cart)</a>
        <a class="btn" href="<c:url value='/payment'/>">Thanh toán (/payment)</a>
        <a class="btn" href="<c:url value='/shipping/address'/>">Địa chỉ giao hàng (/shipping/address)</a>
      </div>

      <hr style="margin:20px 0;">

      <p>Kiểm tra vòng lặp JSTL:</p>
      <c:set var="samples" value="${['Sách', 'Túi vải', 'Bút máy']}" />
      <ul>
        <c:forEach var="item" items="${samples}">
          <li>${item}</li>
        </c:forEach>
      </ul>

      <p>
        Ảnh tĩnh thử: 
        <img src="<c:url value='/assets/images/catalog/thumbnails/thumb-main.jpg'/>" alt="thumb" height="60">
      </p>

      <button id="btn-toast" class="btn">Test toast.js</button>
    </div>
  </div>

  <!-- JS chung -->
  <script src="<c:url value='/assets/js/common/toast.js'/>"></script>
  <script>
    document.getElementById('btn-toast')?.addEventListener('click', () => {
      if (typeof showToast === 'function') {
        showToast('Hello từ index.jsp!');
      } else {
        alert('toast.js chưa nạp, kiểm tra path /assets/js/common/toast.js');
      }
    });
  </script>
</body>
</html>
