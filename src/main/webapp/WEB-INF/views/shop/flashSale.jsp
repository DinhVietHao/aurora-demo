<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tham gia Flash Sale</title>

        <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
        <link rel="stylesheet" href="${ctx}/assets/css/shop/flashsale.css?v=1.0.2" />
    </head>

    <body class="sb-nav-fixed">
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

        <div id="layoutSidenav">
            <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

            <div id="layoutSidenav_content">
                <main class="container-fluid px-4 py-4">
                    <div class="d-flex align-items-center mb-4">
                        <i class="bi bi-lightning-charge-fill text-warning me-2 fs-4"></i>
                        <h3 class="mb-0 fw-bold text-success">Tham gia chương trình Flash Sale</h3>
                    </div>
                    <!-- Danh sách sự kiện -->
                    <!-- Danh sách sự kiện -->
                    <div class="row" id="flashSaleList">
                        <!-- Sự kiện 1 -->
                        <div class="col-md-4 mb-4">
                            <div class="card shadow-sm border-0 flashsale-card h-100">
                                <img src="http://localhost:8080/assets/images/common/flashsale.jpg"
                                    class="card-img-top rounded-top" alt="Flash Sale Tháng 10">
                                <div class="card-body">
                                    <h5 class="card-title text-success fw-bold">Flash Sale Tháng 10</h5>
                                    <p class="card-text small text-muted mb-2">
                                        <i class="bi bi-calendar-event me-1"></i>
                                        20/10/2025 - 25/10/2025
                                    </p>
                                    <span class="badge bg-success">Đang mở đăng ký</span>
                                    <div class="mt-3 text-end">
                                        <button class="btn btn-outline-success btn-sm btn-join-flashsale" data-id="1">
                                            <i class="bi bi-plus-circle me-1"></i> Tham gia
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Sự kiện 2 -->
                        <div class="col-md-4 mb-4">
                            <div class="card shadow-sm border-0 flashsale-card h-100">
                                <img src="http://localhost:8080/assets/images/common/flashsale.jpg"
                                    class="card-img-top rounded-top" alt="Black Friday 2025">
                                <div class="card-body">
                                    <h5 class="card-title text-success fw-bold">Black Friday 2025</h5>
                                    <p class="card-text small text-muted mb-2">
                                        <i class="bi bi-calendar-event me-1"></i>
                                        29/11/2025 - 30/11/2025
                                    </p>
                                    <span class="badge bg-warning text-dark">Sắp diễn ra</span>
                                    <div class="mt-3 text-end">
                                        <button class="btn btn-outline-success btn-sm btn-join-flashsale" data-id="2">
                                            <i class="bi bi-plus-circle me-1"></i> Tham gia
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Sự kiện 3 -->
                        <div class="col-md-4 mb-4">
                            <div class="card shadow-sm border-0 flashsale-card h-100">
                                <img src="http://localhost:8080/assets/images/common/flashsale.jpg"
                                    class="card-img-top rounded-top" alt="Giáng Sinh 2025">
                                <div class="card-body">
                                    <h5 class="card-title text-success fw-bold">Giáng Sinh 2025</h5>
                                    <p class="card-text small text-muted mb-2">
                                        <i class="bi bi-calendar-event me-1"></i>
                                        24/12/2025 - 26/12/2025
                                    </p>
                                    <span class="badge bg-secondary">Chưa mở</span>
                                    <div class="mt-3 text-end">
                                        <button class="btn btn-outline-secondary btn-sm" disabled>
                                            <i class="bi bi-lock me-1"></i> Chưa mở
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>

                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
            </div>
        </div>

        <!-- 🔹 Modal đăng ký Flash Sale (đã đổi ID) -->
        <div class="modal fade" id="flashsaleRegisterModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title">
                            <i class="bi bi-cart-plus me-2"></i> Đăng ký tham gia Flash Sale
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>

                    <div class="modal-body">
                        <form id="flashsaleRegisterForm">
                            <div class="mb-3">
                                <label class="form-label">Chọn sản phẩm</label>
                                <select class="form-select" id="flashsaleProductSelect">
                                    <option value="">-- Chọn sản phẩm --</option>
                                    <option value="101">Áo Thun Năng Động</option>
                                    <option value="102">Giày Sneaker Classic</option>
                                    <option value="103">Balo Thời Trang</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Số lượng đăng ký</label>
                                <input type="number" class="form-control" id="flashsaleQuantityInput" min="1" value="1">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Giá Flash Sale (VNĐ)</label>
                                <input type="number" class="form-control" id="flashsalePriceInput"
                                    placeholder="Nhập giá khuyến mãi">
                            </div>
                        </form>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-success" id="flashsaleBtnSubmitRegister">
                            <i class="bi bi-check-circle me-1"></i> Đăng ký
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${ctx}/assets/js/shop/flashSale.js?v=1.0.1"></script>
    </body>

    </html>