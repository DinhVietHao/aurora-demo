# Hướng dẫn cập nhật các file Admin JSP

## Mục tiêu
Cập nhật tất cả các file JSP trong thư mục `src/main/webapp/WEB-INF/views/admin/` để sử dụng layout mới với sidebar và footer.

## Các thay đổi cần thực hiện

### 1. Thay đổi include head
**Từ:**
```jsp
<jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
```

**Thành:**
```jsp
<jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
```

### 2. Thay thế sidebar cứng bằng include
**Từ:**
```jsp
<div id="layoutSidenav">
    <div id="layoutSidenav_nav">
        <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
            <div class="sb-sidenav-menu">
                <div class="nav">
                    <!-- ... menu items ... -->
                </div>
            </div>
            <div class="sb-sidenav-footer">
                <div class="small">Đăng nhập với:</div>
                Aurora Admin
            </div>
        </nav>
    </div>

    <div id="layoutSidenav_content">
        <main>
```

**Thành:**
```jsp
<div id="layoutSidenav">
    <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />

    <div id="layoutSidenav_content">
        <main>
```

### 3. Thêm footer trước đóng layoutSidenav_content
**Từ:**
```jsp
        </main>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
```

**Thành:**
```jsp
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
```

### 4. Đảm bảo có adminDashboard.js
Thêm trước `</body>` nếu chưa có:
```jsp
<script src="<c:url value='/assets/js/adminDashboard.js'/>"></script>
```

## Danh sách file đã cập nhật ✅

- [x] adminDashboard.jsp
- [x] flash_sales.jsp
- [x] flash_sale_detail.jsp
- [x] flash_sale_approval.jsp
- [x] flash_sale_form.jsp

## Danh sách file cần cập nhật 📝

- [ ] voucher_details.jsp
- [ ] voucher_management.jsp
- [ ] voucher_create.jsp
- [ ] users.jsp
- [ ] shops.jsp
- [ ] shopInfo.jsp
- [ ] products.jsp
- [ ] productDetail.jsp

## Cấu trúc chuẩn của file admin JSP

```jsp
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tên Trang - Aurora Admin</title>
    <jsp:include page="/WEB-INF/views/layouts/_head_admin.jsp" />
    <!-- Thêm CSS riêng nếu cần -->
</head>
<body class="sb-nav-fixed">
<jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

<div id="layoutSidenav">
    <jsp:include page="/WEB-INF/views/layouts/_sidebar_admin.jsp" />

    <div id="layoutSidenav_content">
        <main>
            <div class="container-fluid px-4">
                <!-- Nội dung trang -->
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
    </div>
</div>

<jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />
<script src="<c:url value='/assets/js/adminDashboard.js'/>"></script>
<!-- Thêm JS riêng nếu cần -->
</body>
</html>
```

## Lợi ích của cấu trúc mới

1. **Dễ bảo trì**: Chỉ cần sửa 1 file sidebar cho tất cả trang
2. **Consistent**: Tất cả trang admin có cùng layout
3. **Responsive**: Sidebar hoạt động tốt trên mobile
4. **Professional**: Footer luôn ở cuối trang
5. **Clean code**: Giảm duplicate code

## Lưu ý

- Đảm bảo các file partial tồn tại:
  - `/WEB-INF/views/layouts/_head_admin.jsp`
  - `/WEB-INF/views/layouts/_sidebar_admin.jsp`
  - `/WEB-INF/views/layouts/_footer.jsp`
  - `/WEB-INF/views/layouts/_scripts.jsp`

- CSS và JS đã được cấu hình trong `_head_admin.jsp`
- Sidebar tự động highlight menu item active dựa trên URL
