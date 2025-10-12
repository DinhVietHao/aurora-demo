<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<footer class="footer text-white pt-5 pb-3 mt-4">
    <div class="container">
        <div class="row gy-4">

            <div class="col-6 col-md-3">
                <a href="${ctx}/" class="d-flex align-items-center">
                    <img src="<c:url value='/assets/images/branding/logo-footer.png'/>" alt="Logo" class="footer-logo">
                </a>
                <p>Điểm đến lý tưởng cho người yêu sách. Khám phá, tìm hiểu và mua sắm từ bộ sưu tập phong phú thuộc mọi thể loại.</p>
                <div class="d-flex gap-3">
                    <i class="bi bi-facebook"></i>
                    <i class="bi bi-twitter"></i>
                    <i class="bi bi-instagram"></i>
                    <i class="bi bi-youtube"></i>
                </div>
            </div>

            <div class="col-6 col-md-3">
                <h6 class="fw-bold fs-5">Liên kết nhanh</h6>
                <ul class="list-unstyled">
                    <li><a href="#" class="text-white">Xem sách</a></li>
                    <li><a href="#" class="text-white">Sách mới phát hành</a></li>
                    <li><a href="#" class="text-white">Bán chạy</a></li>
                    <li><a href="#" class="text-white">Danh mục</a></li>
                    <li><a href="#" class="text-white">Tác giả</a></li>
                    <li><a href="#" class="text-white">Ưu đãi đặc biệt</a></li>
                </ul>
            </div>

            <div class="col-6 col-md-3">
                <h6 class="fw-bold fs-5">Hỗ trợ khách hàng</h6>
                <ul class="list-unstyled">
                    <li><a href="#" class="text-white">Tài khoản của tôi</a></li>
                    <li><a href="#" class="text-white">Theo dõi đơn hàng</a></li>
                    <li><a href="#" class="text-white">Thông tin giao hàng</a></li>
                    <li><a href="#" class="text-white">Trả hàng & Đổi hàng</a></li>
                    <li><a href="#" class="text-white">Câu hỏi thường gặp</a></li>
                    <li><a href="#" class="text-white">Liên hệ hỗ trợ</a></li>
                </ul>
            </div>

            <!-- Liên hệ -->
            <div class="col-6 col-md-3 ">
                <h6 class="fw-bold fs-5">Liên hệ</h6>
                <p class="mb-1">123 Đường Book<br>Thành phố Văn Học, LC 12345</p>
                <p class="mb-1">📞 +1 (555) 123-BOOK</p>
                <p>✉️ support@aurora.com</p>
            </div>
        </div>

        <!-- Chân trang dưới -->
        <div class="row border-top border-light mt-4 pt-3">
            <div class="col-md-6 text-center text-md-start">
                <small>© 2024 Aurora. Bảo lưu mọi quyền.</small>
            </div>
            <div class="col-md-6 text-center text-md-end">
                <a href="#" class="text-white me-3">Chính sách bảo mật</a>
                <a href="#" class="text-white">Điều khoản dịch vụ</a>
            </div>
        </div>
    </div>
</footer>