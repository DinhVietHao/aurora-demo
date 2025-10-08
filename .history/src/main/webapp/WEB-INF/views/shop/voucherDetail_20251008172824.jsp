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
                    <title>Chi tiết Voucher - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css?v=1.0.1" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/voucherDetails.css">
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
                                        <h1 class="mt-4 details-title">Chi tiết Voucher</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="home.html">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="adminDashboard.html">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item"><a href="promotionManagement.html">Khuyến
                                                        mãi</a></li>
                                                <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Voucher Header -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card voucher-header-card">
                                                <div class="card-body">
                                                    <div class="row align-items-center">
                                                        <div class="col-md-8">
                                                            <div class="voucher-info">
                                                                <div class="voucher-code-display">
                                                                    <span class="code-text"
                                                                        id="voucherCode">NEWUSER50</span>
                                                                    <button class="btn btn-sm btn-outline-primary ms-2"
                                                                        onclick="copyVoucherCode()" title="Sao chép mã">
                                                                        <i class="bi bi-clipboard"></i>
                                                                    </button>
                                                                </div>
                                                                <h4 class="voucher-name" id="voucherName">Voucher cho
                                                                    người dùng mới</h4>
                                                                <p class="voucher-description" id="voucherDescription">
                                                                    Giảm giá đặc biệt dành cho khách hàng mới đăng ký
                                                                    tài khoản</p>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-4 text-md-end">
                                                            <div class="voucher-actions">
                                                                <span class="badge bg-success status-badge mb-2"
                                                                    id="voucherStatus">Hoạt động</span>
                                                                <div class="action-buttons">
                                                                    <button class="btn btn-warning"
                                                                        onclick="editVoucher()">
                                                                        <i class="bi bi-pencil me-2"></i>Chỉnh sửa
                                                                    </button>
                                                                    <button class="btn btn-danger"
                                                                        onclick="deleteVoucher()">
                                                                        <i class="bi bi-trash me-2"></i>Xóa
                                                                    </button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Voucher Details -->
                                    <div class="row mt-4">
                                        <div class="col-lg-8">
                                            <!-- Basic Information -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-info-circle me-2"></i>Thông tin cơ bản
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Loại giảm giá:</label>
                                                                <span class="badge bg-info" id="discountType">Phần trăm
                                                                    (%)</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Giá trị giảm:</label>
                                                                <span class="discount-value"
                                                                    id="discountValue">50%</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Giảm tối đa:</label>
                                                                <span id="maxDiscount">200.000 VNĐ</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Đơn hàng tối thiểu:</label>
                                                                <span id="minOrderValue">200.000 VNĐ</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Time & Usage -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-clock me-2"></i>Thời gian & Sử dụng
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Ngày bắt đầu:</label>
                                                                <span id="startDate">01/01/2024 00:00</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Ngày kết thúc:</label>
                                                                <span id="endDate">31/01/2024 23:59</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Giới hạn sử dụng:</label>
                                                                <span id="usageLimit">1.000 lượt</span>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="info-item">
                                                                <label>Đã sử dụng:</label>
                                                                <span id="usedCount" class="text-success">240
                                                                    lượt</span>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <!-- Usage Progress -->
                                                    <div class="mt-3">
                                                        <label class="form-label">Tiến độ sử dụng:</label>
                                                        <div class="progress">
                                                            <div class="progress-bar bg-success" role="progressbar"
                                                                style="width: 24%" id="usageProgress">
                                                                24%
                                                            </div>
                                                        </div>
                                                        <small class="text-muted">240 / 1.000 lượt sử dụng</small>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Usage History -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-list-ul me-2"></i>Lịch sử sử dụng gần đây
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="table-responsive">
                                                        <table class="table table-hover">
                                                            <thead>
                                                                <tr>
                                                                    <th>Đơn hàng</th>
                                                                    <th>Khách hàng</th>
                                                                    <th>Giá trị đơn</th>
                                                                    <th>Giảm giá</th>
                                                                    <th>Thời gian</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <td><a
                                                                            href="orderDetails.html?id=ORD001">#ORD001</a>
                                                                    </td>
                                                                    <td>Nguyễn Văn A</td>
                                                                    <td>450.000 VNĐ</td>
                                                                    <td class="text-success">-200.000 VNĐ</td>
                                                                    <td>15/01/2024 14:30</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><a
                                                                            href="orderDetails.html?id=ORD002">#ORD002</a>
                                                                    </td>
                                                                    <td>Trần Thị B</td>
                                                                    <td>320.000 VNĐ</td>
                                                                    <td class="text-success">-160.000 VNĐ</td>
                                                                    <td>14/01/2024 09:15</td>
                                                                </tr>
                                                                <tr>
                                                                    <td><a
                                                                            href="orderDetails.html?id=ORD003">#ORD003</a>
                                                                    </td>
                                                                    <td>Lê Văn C</td>
                                                                    <td>280.000 VNĐ</td>
                                                                    <td class="text-success">-140.000 VNĐ</td>
                                                                    <td>13/01/2024 16:45</td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                    <div class="text-center">
                                                        <button class="btn btn-outline-primary btn-sm">
                                                            <i class="bi bi-eye me-2"></i>Xem tất cả
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-lg-4">
                                            <!-- Voucher Preview -->
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-eye me-2"></i>Xem trước voucher
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="voucher-preview">
                                                        <div class="voucher-preview-card">
                                                            <div class="voucher-header">
                                                                <div class="voucher-code-preview">NEWUSER50</div>
                                                                <div class="voucher-type-preview">%</div>
                                                            </div>
                                                            <div class="voucher-body">
                                                                <div class="voucher-name-preview">Voucher cho người dùng
                                                                    mới</div>
                                                                <div class="voucher-discount-preview">50%</div>
                                                                <div class="voucher-condition-preview">Đơn tối thiểu:
                                                                    200.000 VNĐ</div>
                                                            </div>
                                                            <div class="voucher-footer">
                                                                <div class="voucher-date-preview">01/01/2024 -
                                                                    31/01/2024</div>
                                                                <div class="voucher-usage-preview">Còn lại: 760 lượt
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Statistics -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-graph-up me-2"></i>Thống kê
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="stats-list">
                                                        <div class="stat-item">
                                                            <div class="stat-label">Tổng tiết kiệm:</div>
                                                            <div class="stat-value text-success">12.500.000 VNĐ</div>
                                                        </div>
                                                        <div class="stat-item">
                                                            <div class="stat-label">Trung bình mỗi đơn:</div>
                                                            <div class="stat-value">52.083 VNĐ</div>
                                                        </div>
                                                        <div class="stat-item">
                                                            <div class="stat-label">Tỷ lệ sử dụng:</div>
                                                            <div class="stat-value">24%</div>
                                                        </div>
                                                        <div class="stat-item">
                                                            <div class="stat-label">Khách hàng duy nhất:</div>
                                                            <div class="stat-value">187 người</div>
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
                    <script src="${ctx}/assets/js/shop/voucherDetails.js"></script>
                </body>

                </html>