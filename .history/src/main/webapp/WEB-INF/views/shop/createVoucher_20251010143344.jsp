<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Tạo Voucher mới - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/createVoucher.css">
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <div id="layoutSidenav_nav">
                            <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
                                <div class="sb-sidenav-menu">
                                    <div class="nav">
                                        <div class="sb-sidenav-menu-heading">Tổng quan</div>
                                        <a class="nav-link" href="adminDashboard.html">
                                            <div class="sb-nav-link-icon"><i class="bi bi-speedometer2"></i></div>
                                            Dashboard
                                        </a>

                                        <div class="sb-sidenav-menu-heading">Quản lý</div>
                                        <a class="nav-link" href="shopInfo.html">
                                            <div class="sb-nav-link-icon"><i class="bi bi-shop"></i></div>
                                            Quản lý shop
                                        </a>
                                        <a class="nav-link" href="productManagement.html">
                                            <div class="sb-nav-link-icon"><i class="bi bi-box-seam"></i></div>
                                            Sản phẩm
                                        </a>
                                        <a class="nav-link" href="orderManagement.html">
                                            <div class="sb-nav-link-icon"><i class="bi bi-cart3"></i></div>
                                            Đơn hàng
                                        </a>
                                        <a class="nav-link active" href="promotionManagement.html">
                                            <div class="sb-nav-link-icon"><i class="bi bi-ticket-perforated"></i></div>
                                            Khuyến mãi
                                        </a>
                                        <a class="nav-link" href="#!">
                                            <div class="sb-nav-link-icon"><i class="bi bi-people"></i></div>
                                            Tài khoản
                                        </a>
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
                                <div class="container-fluid px-4">
                                    <!-- Page Header -->
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 create-title">Tạo Voucher mới</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="home.html">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="adminDashboard.html">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item"><a href="promotionManagement.html">Khuyến
                                                        mãi</a></li>
                                                <li class="breadcrumb-item active" aria-current="page">Tạo mới</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Create Form -->
                                    <div class="row mt-4">
                                        <div class="col-lg-8">
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-plus-circle me-2"></i>Thông tin voucher
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <form id="createVoucherForm">
                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="voucherCode" class="form-label">Mã
                                                                        voucher <span
                                                                            class="text-danger">*</span></label>
                                                                    <input type="text" class="form-control"
                                                                        id="voucherCode" placeholder="VD: NEWUSER50"
                                                                        required>
                                                                    <div class="form-text">Mã voucher phải là duy nhất
                                                                        và không chứa khoảng trắng</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="voucherName" class="form-label">Tên
                                                                        voucher <span
                                                                            class="text-danger">*</span></label>
                                                                    <input type="text" class="form-control"
                                                                        id="voucherName"
                                                                        placeholder="VD: Voucher cho người dùng mới"
                                                                        required>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label for="voucherDescription" class="form-label">Mô
                                                                tả</label>
                                                            <textarea class="form-control" id="voucherDescription"
                                                                rows="3"
                                                                placeholder="Mô tả chi tiết về voucher..."></textarea>
                                                        </div>

                                                        <div class="row">
                                                            <div class="col-md-4">
                                                                <div class="mb-3">
                                                                    <label for="discountType" class="form-label">Loại
                                                                        giảm giá <span
                                                                            class="text-danger">*</span></label>
                                                                    <select class="form-select" id="discountType"
                                                                        required>
                                                                        <option value="">Chọn loại giảm giá</option>
                                                                        <option value="percentage">Phần trăm (%)
                                                                        </option>
                                                                        <option value="fixed">Số tiền cố định (VNĐ)
                                                                        </option>
                                                                    </select>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <div class="mb-3">
                                                                    <label for="discountValue" class="form-label">Giá
                                                                        trị giảm <span
                                                                            class="text-danger">*</span></label>
                                                                    <input type="number" class="form-control"
                                                                        id="discountValue" placeholder="0" min="0"
                                                                        required>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <div class="mb-3">
                                                                    <label for="maxDiscount" class="form-label">Giảm tối
                                                                        đa (VNĐ)</label>
                                                                    <input type="number" class="form-control"
                                                                        id="maxDiscount" placeholder="0" min="0">
                                                                    <div class="form-text">Chỉ áp dụng cho giảm theo %
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="minOrderValue" class="form-label">Đơn
                                                                        hàng tối thiểu (VNĐ)</label>
                                                                    <input type="number" class="form-control"
                                                                        id="minOrderValue" placeholder="0" min="0">
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="usageLimit" class="form-label">Giới hạn
                                                                        sử dụng</label>
                                                                    <input type="number" class="form-control"
                                                                        id="usageLimit" placeholder="Không giới hạn"
                                                                        min="1">
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="row">
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="startDate" class="form-label">Ngày bắt
                                                                        đầu <span class="text-danger">*</span></label>
                                                                    <input type="datetime-local" class="form-control"
                                                                        id="startDate" required>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-6">
                                                                <div class="mb-3">
                                                                    <label for="endDate" class="form-label">Ngày kết
                                                                        thúc <span class="text-danger">*</span></label>
                                                                    <input type="datetime-local" class="form-control"
                                                                        id="endDate" required>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="mb-3">
                                                            <div class="form-check">
                                                                <input class="form-check-input" type="checkbox"
                                                                    id="isActive" checked>
                                                                <label class="form-check-label" for="isActive">
                                                                    Kích hoạt voucher ngay sau khi tạo
                                                                </label>
                                                            </div>
                                                        </div>

                                                        <div class="d-flex gap-3">
                                                            <button type="submit" class="btn btn-success">
                                                                <i class="bi bi-check-circle me-2"></i>Tạo voucher
                                                            </button>
                                                            <button type="button" class="btn btn-secondary"
                                                                onclick="window.history.back()">
                                                                <i class="bi bi-arrow-left me-2"></i>Quay lại
                                                            </button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-lg-4">
                                            <!-- Preview Card -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-eye me-2"></i>Xem trước voucher
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="voucher-preview">
                                                        <div class="voucher-preview-card">
                                                            <div class="voucher-header">
                                                                <div class="voucher-code-preview" id="previewCode">
                                                                    VOUCHER_CODE</div>
                                                                <div class="voucher-type-preview" id="previewType">%
                                                                </div>
                                                            </div>
                                                            <div class="voucher-body">
                                                                <div class="voucher-name-preview" id="previewName">Tên
                                                                    voucher</div>
                                                                <div class="voucher-discount-preview"
                                                                    id="previewDiscount">0%</div>
                                                                <div class="voucher-condition-preview"
                                                                    id="previewCondition">Đơn tối thiểu: 0 VNĐ</div>
                                                            </div>
                                                            <div class="voucher-footer">
                                                                <div class="voucher-date-preview" id="previewDate">Chưa
                                                                    có thời hạn</div>
                                                                <div class="voucher-usage-preview" id="previewUsage">
                                                                    Không giới hạn</div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Tips Card -->
                                            <div class="card mt-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-lightbulb me-2"></i>Gợi ý
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="tips-list">
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Mã voucher nên ngắn gọn và dễ nhớ</span>
                                                        </div>
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Đặt giới hạn sử dụng để tránh lạm dụng</span>
                                                        </div>
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Kiểm tra kỹ thời gian hiệu lực</span>
                                                        </div>
                                                        <div class="tip-item">
                                                            <i class="bi bi-check-circle text-success me-2"></i>
                                                            <span>Mô tả rõ ràng điều kiện áp dụng</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>

                            <footer class="footer text-white pt-5 pb-3 mt-4">
                                <div class="container">
                                    <div class="row gy-4">
                                        <div class="col-6 col-md-3">
                                            <a href="#" class="d-flex align-items-center">
                                                <img src="./assets/images/logo-footer.png" alt="Logo"
                                                    class="footer-logo">
                                            </a>
                                            <p>Your ultimate destination for books. Discover, explore, and purchase from
                                                our vast collection of
                                                books across all genres.</p>
                                            <div class="d-flex gap-3">
                                                <i class="bi bi-facebook"></i>
                                                <i class="bi bi-twitter"></i>
                                                <i class="bi bi-instagram"></i>
                                                <i class="bi bi-youtube"></i>
                                            </div>
                                        </div>

                                        <div class="col-6 col-md-3">
                                            <h6 class="fw-bold fs-5">Quick Links</h6>
                                            <ul class="list-unstyled">
                                                <li><a href="#" class="text-white">Browse Books</a></li>
                                                <li><a href="#" class="text-white">New Arrivals</a></li>
                                                <li><a href="#" class="text-white">Best Sellers</a></li>
                                                <li><a href="#" class="text-white">Special Offers</a></li>
                                                <li><a href="#" class="text-white">Gift Cards</a></li>
                                            </ul>
                                        </div>

                                        <div class="col-6 col-md-3">
                                            <h6 class="fw-bold fs-5">Categories</h6>
                                            <ul class="list-unstyled">
                                                <li><a href="#" class="text-white">Fiction</a></li>
                                                <li><a href="#" class="text-white">Non-Fiction</a></li>
                                                <li><a href="#" class="text-white">Science & Technology</a></li>
                                                <li><a href="#" class="text-white">Children's Books</a></li>
                                                <li><a href="#" class="text-white">Educational</a></li>
                                            </ul>
                                        </div>

                                        <div class="col-6 col-md-3">
                                            <h6 class="fw-bold fs-5">Contact Us</h6>
                                            <p class="mb-1">📍 123 Book Street, Reading City</p>
                                            <p class="mb-1">📞 +1 (555) 123-BOOK</p>
                                            <p>✉️ support@aurora.com</p>
                                        </div>
                                    </div>

                                    <!-- Bottom Footer -->
                                    <div class="row border-top border-light mt-4 pt-3">
                                        <div class="col-md-6 text-center text-md-start">
                                            <small>© 2024 Aurora. All rights reserved.</small>
                                        </div>
                                        <div class="col-md-6 text-center text-md-end">
                                            <a href="#" class="text-white me-3">Privacy Policy</a>
                                            <a href="#" class="text-white">Terms of Service</a>
                                        </div>
                                    </div>
                                </div>
                            </footer>
                        </div>
                    </div>

                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="assets/js/promotionCreate.js"></script>
                </body>

                </html>