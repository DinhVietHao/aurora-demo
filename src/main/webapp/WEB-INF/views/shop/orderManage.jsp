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
                    <title>Quản lý Đơn hàng - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css">
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/orderManagement.css">
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
                                        <h1 class="mt-4 order-management-title">Tất cả</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="home.html">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="adminDashboard.html">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item active" aria-current="page">Đơn hàng</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Order Status Tabs -->
                                    <div class="row mt-4">
                                        <div class="col-12">
                                            <div class="card">
                                                <div class="card-body">
                                                    <ul class="nav nav-tabs order-tabs" id="orderTabs" role="tablist">
                                                        <li class="nav-item" role="presentation">
                                                            <button class="nav-link active" id="all-tab"
                                                                data-bs-toggle="tab" data-bs-target="#all" type="button"
                                                                role="tab">
                                                                Tất cả
                                                            </button>
                                                        </li>
                                                        <li class="nav-item" role="presentation">
                                                            <button class="nav-link" id="pending-tab"
                                                                data-bs-toggle="tab" data-bs-target="#pending"
                                                                type="button" role="tab">
                                                                Chờ xác nhận
                                                            </button>
                                                        </li>
                                                        <li class="nav-item" role="presentation">
                                                            <button class="nav-link" id="confirmed-tab"
                                                                data-bs-toggle="tab" data-bs-target="#confirmed"
                                                                type="button" role="tab">
                                                                Chờ lấy hàng
                                                            </button>
                                                        </li>
                                                        <li class="nav-item" role="presentation">
                                                            <button class="nav-link" id="shipping-tab"
                                                                data-bs-toggle="tab" data-bs-target="#shipping"
                                                                type="button" role="tab">
                                                                Đang giao (18)
                                                            </button>
                                                        </li>
                                                        <li class="nav-item" role="presentation">
                                                            <button class="nav-link" id="delivered-tab"
                                                                data-bs-toggle="tab" data-bs-target="#delivered"
                                                                type="button" role="tab">
                                                                Đã giao
                                                            </button>
                                                        </li>
                                                        <li class="nav-item" role="presentation">
                                                            <button class="nav-link" id="cancelled-tab"
                                                                data-bs-toggle="tab" data-bs-target="#cancelled"
                                                                type="button" role="tab">
                                                                Trả hàng/Hoàn tiền/Hủy (4)
                                                            </button>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Warning Alert -->
                                    <div class="row mt-3">
                                        <div class="col-12">
                                            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                                                <i class="bi bi-exclamation-triangle me-2"></i>
                                                <strong>Lưu ý:</strong> Tỷ lệ đơn hàng không thành công của Shop trong
                                                tuần trước là 10%. Nếu tỷ lệ này vượt quá 10% vào tuần này, bạn có thể
                                                bị phạt tối đa 2 điểm tín. Vui lòng cập nhật giao hàng đúng hạn để tránh
                                                bị phạt.
                                                <button type="button" class="btn-close" data-bs-dismiss="alert"
                                                    aria-label="Close"></button>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Search and Filter Section -->
                                    <div class="row mt-3">
                                        <div class="col-12">
                                            <div class="card">
                                                <div class="card-body">
                                                    <div class="row g-3">
                                                        <div class="col-md-3">
                                                            <label for="orderSearch" class="form-label">Mã đơn
                                                                hàng</label>
                                                            <input type="text" class="form-control" id="orderSearch"
                                                                placeholder="Nhập mã đơn hàng">
                                                        </div>
                                                        <div class="col-md-3">
                                                            <label for="customerSearch" class="form-label">Tên khách
                                                                hàng</label>
                                                            <input type="text" class="form-control" id="customerSearch"
                                                                placeholder="Nhập tên khách hàng">
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label for="orderStatus" class="form-label">Đơn vị vận
                                                                chuyển</label>
                                                            <select class="form-select" id="orderStatus">
                                                                <option value="">Tất cả ĐVVC</option>
                                                                <option value="ghn">Giao hàng nhanh</option>
                                                                <option value="ghtk">Giao hàng tiết kiệm</option>
                                                                <option value="viettel">Viettel Post</option>
                                                            </select>
                                                        </div>
                                                        <div class="col-md-2">
                                                            <label class="form-label">&nbsp;</label>
                                                            <div class="d-flex gap-2">
                                                                <button type="button" class="btn btn-primary">Áp
                                                                    dụng</button>
                                                                <button type="button"
                                                                    class="btn btn-outline-secondary">Đặt lại</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Orders Summary -->
                                    <div class="row mt-3">
                                        <div class="col-12">
                                            <div class="d-flex justify-content-between align-items-center">
                                                <h5 class="mb-0">2373 Đơn hàng</h5>
                                                <div class="d-flex align-items-center gap-3">
                                                    <span class="text-muted">Sản phẩm</span>
                                                    <span class="text-muted">Tổng Đơn hàng</span>
                                                    <span class="text-muted">Trạng thái</span>
                                                    <span class="text-muted">Đơn vị vận chuyển</span>
                                                    <span class="text-muted">Đơn vị vận chuyển</span>
                                                    <span class="text-muted">Thao tác</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Tab Content -->
                                    <div class="tab-content mt-3" id="orderTabContent">
                                        <div class="tab-pane fade show active" id="all" role="tabpanel">
                                            <!-- Order Items -->
                                            <div class="order-list">
                                                <!-- Order 1 -->
                                                <div class="order-item">
                                                    <div class="order-header">
                                                        <div class="d-flex align-items-center">
                                                            <span class="order-id">Gemini2019</span>
                                                            <span class="badge bg-danger ms-2">
                                                                <i class="bi bi-flag"></i>
                                                            </span>
                                                        </div>
                                                        <div class="order-code">Mã đơn hàng: 250912440CPRXU</div>
                                                    </div>
                                                    <div class="order-content">
                                                        <div class="row align-items-center">
                                                            <div class="col-md-4">
                                                                <div class="d-flex">
                                                                    <img src="./assets/images/book-placeholder.jpg"
                                                                        alt="Book" class="order-product-image">
                                                                    <div class="ms-3">
                                                                        <h6 class="product-name">Cây Thúc Thần Kỳ, Cây
                                                                            Cầm Đồng Trong Tín Điều, và Thú Handmade:
                                                                            Nghệ Thuật Thủ Công Việt Tân Hành</h6>
                                                                        <p class="text-muted mb-0">x1</p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-total">
                                                                    <div class="fw-bold">643.400</div>
                                                                    <div class="text-muted small">Tổng tiền khi chưa
                                                                        khấu trừ</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-status">
                                                                    <span class="badge bg-warning">Đã giao cho
                                                                        ĐVVC</span>
                                                                    <div class="text-muted small">Giao hàng đang giao
                                                                        cho Người mua</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="shipping-info">
                                                                    <div class="fw-bold">Nhanh</div>
                                                                    <div class="text-muted small">SPX Express</div>
                                                                    <div class="text-muted small">SP1A3B2C3D4E5F6</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-actions" data-productid="1">
                                                                    <a href="" class="btn btn-outline-primary btn-sm">
                                                                        Xem chi tiết
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Order 2 -->
                                                <div class="order-item">
                                                    <div class="order-header">
                                                        <div class="d-flex align-items-center">
                                                            <span class="order-id">anhhieuvan6453</span>
                                                            <span class="badge bg-danger ms-2">
                                                                <i class="bi bi-flag"></i>
                                                            </span>
                                                        </div>
                                                        <div class="order-code">Mã đơn hàng: 250918C3F8P6</div>
                                                    </div>
                                                    <div class="order-content">
                                                        <div class="row align-items-center">
                                                            <div class="col-md-4">
                                                                <div class="d-flex">
                                                                    <img src="./assets/images/book-placeholder.jpg"
                                                                        alt="Book" class="order-product-image">
                                                                    <div class="ms-3">
                                                                        <h6 class="product-name">PHÁP HOA TÔN GIÁO Những
                                                                            Lưu Ý Lớp Mẫn Sáu Hạt và Bài Tập Handmade,
                                                                            Tr</h6>
                                                                        <p class="text-muted mb-0">x1</p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-total">
                                                                    <div class="fw-bold">412.100</div>
                                                                    <div class="text-muted small">Thành tiền khi chưa
                                                                        khấu trừ</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-status">
                                                                    <span class="badge bg-warning">Đã giao cho
                                                                        ĐVVC</span>
                                                                    <div class="text-muted small">Giao hàng đang giao
                                                                        cho Người mua</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="shipping-info">
                                                                    <div class="fw-bold">Nhanh</div>
                                                                    <div class="text-muted small">SPX Express</div>
                                                                    <div class="text-muted small">SP1A3B2C3D4E5F6</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-actions">
                                                                    <a href="/order?productId=1"
                                                                        class="btn btn-outline-primary btn-sm">
                                                                        Xem chi tiết
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Order 3 -->
                                                <div class="order-item">
                                                    <div class="order-header">
                                                        <div class="d-flex align-items-center">
                                                            <span class="order-id">lonha1417</span>
                                                            <span class="badge bg-danger ms-2">
                                                                <i class="bi bi-flag"></i>
                                                            </span>
                                                        </div>
                                                        <div class="order-code">Mã đơn hàng: 250914GF1H4G03</div>
                                                    </div>
                                                    <div class="order-content">
                                                        <div class="row align-items-center">
                                                            <div class="col-md-4">
                                                                <div class="d-flex">
                                                                    <img src="./assets/images/book-placeholder.jpg"
                                                                        alt="Book" class="order-product-image">
                                                                    <div class="ms-3">
                                                                        <h6 class="product-name">1 Lúp Đào Kiểm Những
                                                                            Suy Nghĩ Để Thương - Lúp Ở Át Tương Tự -
                                                                            Dành - Tương Tự Những Suy Nghĩ Để Thương
                                                                        </h6>
                                                                        <p class="text-muted mb-0">x1</p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-total">
                                                                    <div class="fw-bold">1166.625</div>
                                                                    <div class="text-muted small">Thành tiền khi chưa
                                                                        khấu trừ</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-status">
                                                                    <span class="badge bg-warning">Đã giao cho
                                                                        ĐVVC</span>
                                                                    <div class="text-muted small">Giao hàng đang giao
                                                                        cho Người mua</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="shipping-info">
                                                                    <div class="fw-bold">Nhanh</div>
                                                                    <div class="text-muted small">SPX Express</div>
                                                                    <div class="text-muted small">SP1A3B2C3D4E5F6</div>
                                                                </div>
                                                            </div>
                                                            <div class="col-md-2">
                                                                <div class="order-actions" data-productid="1">
                                                                    <a href="/order?productId=1"
                                                                        class="btn btn-outline-primary btn-sm">
                                                                        Xem chi tiết
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </div>
                                        </div>

                                        <!-- Pagination -->
                                        <div class="row mt-4">
                                            <div class="col-12">
                                                <div class="pagination-container">
                                                    <nav aria-label="Order pagination">
                                                        <ul class="pagination">
                                                            <li class="page-item disabled">
                                                                <a class="page-link" href="#" tabindex="-1"
                                                                    aria-disabled="true">Trước</a>
                                                            </li>
                                                            <li class="page-item active" aria-current="page">
                                                                <a class="page-link" href="#">1</a>
                                                            </li>
                                                            <li class="page-item">
                                                                <a class="page-link" href="#">2</a>
                                                            </li>
                                                            <li class="page-item">
                                                                <a class="page-link" href="#">3</a>
                                                            </li>
                                                            <li class="page-item">
                                                                <span class="page-link">...</span>
                                                            </li>
                                                            <li class="page-item">
                                                                <a class="page-link" href="#">95</a>
                                                            </li>
                                                            <li class="page-item">
                                                                <a class="page-link" href="#">Sau</a>
                                                            </li>
                                                        </ul>
                                                    </nav>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />

                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/orderManagement.js?v=1.0.1"></script>
                </body>

                </html>