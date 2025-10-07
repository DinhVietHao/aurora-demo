<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="pageTitle" value="Đăng ký mở shop" />
        <c:set var="ctx" value="${pageContext.request.contextPath}" />

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />

            <!-- CSS riêng trang Profile -->
            <link rel="stylesheet" href="./assets/css/shop/register_shop.css?v=1.0.1" />

        </head>

        <body>
            <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

            <main>
                <!-- Toast notification Add To Cart -->
                <div id="notify-toast"></div>

                <div class="register-shop">
                    <h5 class="register-shop-title">Đăng ký mở shop</h5>
                    <form id="register-shop-form" action="${ctx}/shop?action=register" method="post"
                        enctype="multipart/form-data">
                        <!-- Thông tin tài khoản -->
                        <div class="card mb-4 register-shop__section">
                            <div class="card-header bg-white fw-bold">
                                <i class="bi bi-person"></i> Thông tin tài khoản
                            </div>
                            <div class="card-body row g-3">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="fullname" class="form-label">Họ và tên</label>
                                        <input type="text" id="fullname" name="fullname" class="form-control"
                                            value="${sessionScope.AUTH_USER.fullName}" placeholder="Nhập họ và tên"
                                            required minlength="2" maxlength="150">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="phone" class="form-label">Số điện thoại</label>
                                        <input type="text" id="phone" name="phone" class="form-control"
                                            placeholder="Nhập số điện thoại" required pattern="^0\d{9,10}$"
                                            title="Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="email" class="form-label">Email</label>
                                        <input type="email" id="email" name="email" class="form-control"
                                            value="${sessionScope.AUTH_USER.email}" placeholder="Nhập email" required
                                            maxlength="255">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Thông tin shop -->
                        <div class="card mb-4 register-shop__section">
                            <div class="card-header bg-white fw-bold">
                                <i class="bi bi-shop"></i> Thông tin shop
                            </div>
                            <div class="card-body row g-3">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="shopName" class="form-label">Tên shop *</label>
                                        <input type="text" id="shopName" name="shopName" class="form-control"
                                            placeholder="Nhập tên shop" required minlength="3" maxlength="150">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="shopDesc" class="form-label">Mô tả shop</label>
                                        <textarea id="shopDesc" name="shopDesc" class="form-control"
                                            maxlength="255"></textarea>
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="shopLogo" class="form-label">Logo shop</label>
                                        <input type="file" id="shopLogo" name="shopLogo" class="form-control"
                                            accept="image/*">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Địa chỉ lấy hàng -->
                        <div class="card mb-4 register-shop__section">
                            <div class="card-header bg-white fw-bold">
                                <i class="bi bi-geo-alt"></i> Địa chỉ lấy hàng
                            </div>
                            <div class="card-body row g-3">
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="city" class="form-label">Tỉnh/Thành phố *</label>
                                        <select id="city" name="city" class="form-select" required>
                                            <option value="">Chọn tỉnh/thành phố</option>
                                        </select>
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-group">
                                        <label for="ward" class="form-label">Phường/Xã *</label>
                                        <select id="ward" name="ward" class="form-select" required>
                                            <option value="">Chọn phường/xã</option>
                                        </select>
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="addressLine" class="form-label">Địa chỉ chi tiết *</label>
                                        <input type="text" id="addressLine" name="addressLine" class="form-control"
                                            placeholder="Số nhà, tên đường..." required minlength="5" maxlength="255">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12 text-center">
                            <button type="submit" class="button-four">Đăng ký</button>
                        </div>
                    </form>
                </div>
            </main>

            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />

            <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

            <!-- JS của thông báo Toast -->
            <script src="${ctx}/assets/js/common/toast.js?v=1.0.1"></script>

            <!-- Đăng ký shop -->
            <script src="<c:url value='/assets/js/shop/registerShop.js'/>?v=1.0.1"></script>

        </body>

        </html>