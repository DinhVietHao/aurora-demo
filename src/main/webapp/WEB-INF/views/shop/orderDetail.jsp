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
                    <title>Chi tiết Đơn hàng - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/orderDetails.css?v=1.0.10">
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />
                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <!-- Page Header -->
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 order-details-title">Chi tiết Đơn hàng</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="/home">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="/shop/dashboard">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item"><a href="/">Đơn hàng</a>
                                                </li>
                                                <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Order Header Info -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card order-header-card">
                                                <div class="card-body">
                                                    <div class="row">
                                                        <div class="col-md-8">
                                                            <div class="order-info">
                                                                <h4 class="order-code">Đơn hàng #250912440CPRXU</h4>
                                                                <div class="order-meta">
                                                                    <span class="badge bg-warning order-status">Đã giao
                                                                        cho ĐVVC</span>
                                                                    <span class="order-date">Đặt hàng: 12/09/2024
                                                                        14:30</span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-4 text-md-end">
                                                            <div class="order-actions">
                                                                <button class="btn btn-outline-primary btn-sm me-2">
                                                                    <i class="bi bi-printer"></i> In đơn hàng
                                                                </button>
                                                                <button class="btn btn-primary btn-sm">
                                                                    <i class="bi bi-pencil"></i> Cập nhật trạng thái
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Order Details Content -->
                                    <div class="row mt-4">
                                        <!-- Customer Information -->
                                        <div class="col-md-6">
                                            <div class="card h-100">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-person-circle me-2"></i>Thông tin khách hàng
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="customer-info">
                                                        <div class="info-row">
                                                            <strong>Tên khách hàng:</strong>
                                                            <span>Gemini2019</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Email:</strong>
                                                            <span>gemini2019@email.com</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Số điện thoại:</strong>
                                                            <span>0123 456 789</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Địa chỉ giao hàng:</strong>
                                                            <span>123 Đường ABC, Phường XYZ, Quận 1, TP.HCM</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Ghi chú:</strong>
                                                            <span class="text-muted">Giao hàng trong giờ hành
                                                                chính</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Shipping Information -->
                                        <div class="col-md-6">
                                            <div class="card h-100">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-truck me-2"></i>Thông tin vận chuyển
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="shipping-info">
                                                        <div class="info-row">
                                                            <strong>Đơn vị vận chuyển:</strong>
                                                            <span>SPX Express</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Mã vận đơn:</strong>
                                                            <span class="tracking-code">SP1A3B2C3D4E5F6</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Loại giao hàng:</strong>
                                                            <span>Nhanh</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Phí vận chuyển:</strong>
                                                            <span>25.000 VNĐ</span>
                                                        </div>
                                                        <div class="info-row">
                                                            <strong>Trạng thái:</strong>
                                                            <span class="badge bg-warning">Đang giao hàng</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Order Items -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-box-seam me-2"></i>Sản phẩm đã đặt
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="table-responsive">
                                                        <table class="table table-hover">
                                                            <thead>
                                                                <tr>
                                                                    <th>Sản phẩm</th>
                                                                    <th>Đơn giá</th>
                                                                    <th>Số lượng</th>
                                                                    <th>Thành tiền</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <div class="d-flex align-items-center">
                                                                            <img src="./assets/images/book-placeholder.jpg"
                                                                                alt="Book" class="product-image me-3">
                                                                            <div>
                                                                                <h6 class="product-name mb-1">Cây Thúc
                                                                                    Thần Kỳ, Cây Cầm Đồng Trong Tín
                                                                                    Điều, và Thú Handmade: Nghệ Thuật
                                                                                    Thủ Công Việt Tân Hành</h6>
                                                                                <small class="text-muted">SKU:
                                                                                    BK001234</small>
                                                                            </div>
                                                                        </div>
                                                                    </td>
                                                                    <td>
                                                                        <span class="price">618.400 VNĐ</span>
                                                                    </td>
                                                                    <td>
                                                                        <span class="quantity">1</span>
                                                                    </td>
                                                                    <td>
                                                                        <span class="total-price">618.400 VNĐ</span>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Order Summary -->
                                    <div class="row mt-4">
                                        <div class="col-md-8">
                                            <!-- Order Timeline -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-clock-history me-2"></i>Lịch sử đơn hàng
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="timeline">
                                                        <div class="timeline-item completed">
                                                            <div class="timeline-marker"></div>
                                                            <div class="timeline-content">
                                                                <h6>Đơn hàng đã được đặt</h6>
                                                                <p class="text-muted mb-0">12/09/2024 14:30</p>
                                                            </div>
                                                        </div>
                                                        <div class="timeline-item completed">
                                                            <div class="timeline-marker"></div>
                                                            <div class="timeline-content">
                                                                <h6>Đơn hàng đã được xác nhận</h6>
                                                                <p class="text-muted mb-0">12/09/2024 15:15</p>
                                                            </div>
                                                        </div>
                                                        <div class="timeline-item completed">
                                                            <div class="timeline-marker"></div>
                                                            <div class="timeline-content">
                                                                <h6>Đang chuẩn bị hàng</h6>
                                                                <p class="text-muted mb-0">13/09/2024 09:00</p>
                                                            </div>
                                                        </div>
                                                        <div class="timeline-item active">
                                                            <div class="timeline-marker"></div>
                                                            <div class="timeline-content">
                                                                <h6>Đã giao cho đơn vị vận chuyển</h6>
                                                                <p class="text-muted mb-0">13/09/2024 16:30</p>
                                                            </div>
                                                        </div>
                                                        <div class="timeline-item">
                                                            <div class="timeline-marker"></div>
                                                            <div class="timeline-content">
                                                                <h6>Đang giao hàng</h6>
                                                                <p class="text-muted mb-0">Dự kiến: 14/09/2024</p>
                                                            </div>
                                                        </div>
                                                        <div class="timeline-item">
                                                            <div class="timeline-marker"></div>
                                                            <div class="timeline-content">
                                                                <h6>Giao hàng thành công</h6>
                                                                <p class="text-muted mb-0">Dự kiến: 15/09/2024</p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-md-4">
                                            <!-- Payment Summary -->
                                            <div class="card">
                                                <div class="card-header">
                                                    <h5 class="card-title mb-0">
                                                        <i class="bi bi-receipt me-2"></i>Tóm tắt thanh toán
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <div class="payment-summary">
                                                        <div class="summary-row">
                                                            <span>Tạm tính:</span>
                                                            <span>618.400 VNĐ</span>
                                                        </div>
                                                        <div class="summary-row">
                                                            <span>Phí vận chuyển:</span>
                                                            <span>25.000 VNĐ</span>
                                                        </div>
                                                        <div class="summary-row">
                                                            <span>Giảm giá:</span>
                                                            <span class="text-success">-0 VNĐ</span>
                                                        </div>
                                                        <hr>
                                                        <div class="summary-row total">
                                                            <strong>Tổng cộng:</strong>
                                                            <strong class="text-primary">643.400 VNĐ</strong>
                                                        </div>
                                                        <hr>
                                                        <div class="payment-method">
                                                            <strong>Phương thức thanh toán:</strong>
                                                            <div class="mt-2">
                                                                <span class="badge bg-success">
                                                                    <i class="bi bi-credit-card me-1"></i>Thanh toán
                                                                    online
                                                                </span>
                                                            </div>
                                                            <small class="text-muted">Đã thanh toán</small>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>
                        </div>
                        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
                    </div>

                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/orderDetails.js?v=1.0.2"></script>
                </body>

                </html>