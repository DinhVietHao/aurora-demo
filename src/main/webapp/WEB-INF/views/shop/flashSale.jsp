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
                                <c:if test="${not empty successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(successMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>

                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        ${fn:escapeXml(errorMessage)}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Đóng"></button>
                                    </div>
                                </c:if>
                                <div class="d-flex align-items-center mb-4">
                                    <i class="bi bi-lightning-charge-fill text-warning me-2 fs-4"></i>
                                    <h3 class="mb-0 fw-bold text-success">Tham gia chương trình Flash Sale
                                    </h3>
                                </div>

                                <div class="row" id="flashSaleList">
                                    <c:forEach var="f" items="${flashSales}">
                                        <div class="col-md-4 mb-4">
                                            <div class="card shadow-sm border-0 flashsale-card h-100">
                                                <img src="http://localhost:8080/assets/images/common/flashsale.jpg"
                                                    class="card-img-top rounded-top" alt="${f.name}">
                                                <div class="card-body">
                                                    <h5 class="card-title text-success fw-bold">${f.name}
                                                    </h5>
                                                    <p class="card-text small text-muted mb-2">
                                                        <i class="bi bi-calendar-event me-1"></i>
                                                        <fmt:formatDate value="${f.startAt}" pattern="dd/MM/yyyy" /> -
                                                        <fmt:formatDate value="${f.endAt}" pattern="dd/MM/yyyy" />
                                                    </p>

                                                    <c:choose>
                                                        <c:when test="${f.status eq 'ACTIVE'}">
                                                            <span class="badge fw-semibold bg-success">Đang diễn
                                                                ra</span>
                                                            <div class="mt-3 text-end d-flex justify-content-end gap-2">
                                                                <a href="/shop/flashSale?action=getFlashsaleItem&flashSaleId=${f.flashSaleID}"
                                                                    class="btn btn-secondary fw-semibold btn-sm">
                                                                    <i class="bi bi-eye me-1"></i> Xem danh sách
                                                                </a>
                                                                <button
                                                                    class="btn fw-semibold btn-sm text-white btn-join-flashsale"
                                                                    data-id="${f.flashSaleID}"
                                                                    style="background-color: #154C3D">
                                                                    <i class="bi bi-plus-circle me-1"></i> Tham gia
                                                                </button>
                                                            </div>
                                                        </c:when>

                                                        <c:when test="${f.status eq 'SCHEDULED'}">
                                                            <span class="badge fw-semibold bg-warning text-dark">Sắp
                                                                diễn ra</span>
                                                            <div class="mt-3 text-end d-flex justify-content-end gap-2">
                                                                <a href="/shop/flashSale?action=getFlashsaleItem&flashSaleId=${f.flashSaleID}"
                                                                    class="btn btn-secondary fw-semibold btn-sm">
                                                                    <i class="bi bi-eye me-1"></i> Xem danh sách
                                                                </a>
                                                                <button
                                                                    class="btn btn-sm fw-semibold text-white btn-join-flashsale"
                                                                    data-id="${f.flashSaleID}"
                                                                    style="background-color: #154C3D">
                                                                    <i class="bi bi-plus-circle me-1"></i> Tham gia
                                                                </button>
                                                            </div>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <span class="badge fw-semibold bg-secondary">Đã kết
                                                                thúc</span>
                                                            <div class="mt-3 text-end d-flex justify-content-end gap-2">
                                                                <a href="/shop/flashSale?action=getFlashsaleItem&flashSaleId=${f.flashSaleID}"
                                                                    class="btn btn-secondary fw-semibold btn-sm">
                                                                    <i class="bi bi-eye me-1"></i> Xem danh sách
                                                                </a>
                                                                <button class="btn btn-outline-secondary btn-sm"
                                                                    disabled>
                                                                    <i class="bi bi-lock me-1"></i> Chưa mở
                                                                </button>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </main>

                            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
                        </div>
                    </div>

                    <!-- 🔹 Modal đăng ký Flash Sale -->

                    <div class="modal fade" id="flashsaleRegisterModal" tabindex="-1">
                        <div class="modal-dialog modal-dialog-centered modal-lg">
                            <div class="modal-content shadow-lg border-0">
                                <div class="modal-header text-white" style="background-color: #154C3D">
                                    <h5 class="modal-title fw-semibold">
                                        <i class="bi bi-lightning-charge-fill me-2"></i> Đăng ký tham gia
                                        Flash Sale
                                    </h5>
                                    <button type="button" class="btn-close btn-close-white"
                                        data-bs-dismiss="modal"></button>
                                </div>

                                <div class="modal-body">
                                    <form id="flashsaleRegisterForm" action="/shop/flashSale" method="post">
                                        <!-- Hidden inputs -->
                                        <input type="hidden" name="action" value="registerFlashSale" />
                                        <input type="hidden" id="flashsaleProductSelect"
                                            name="flashsaleProductSelect" />
                                        <input type="hidden" id="flashsaleShopId" name="flashsaleShopId" />
                                        <input type="hidden" id="flashSaleId" name="flashSaleId" />

                                        <!-- Chọn sản phẩm -->
                                        <div class="mb-3 position-relative">
                                            <label class="form-label fw-bold">Chọn sản phẩm tham gia</label>
                                            <div class="dropdown">
                                                <button
                                                    class="btn w-100 text-start d-flex justify-content-between align-items-center btn-flashsale"
                                                    type="button" id="dropdownProductBtn"
                                                    style="border: 1px solid black;">
                                                    <span id="selectedProductText">-- Chọn sản phẩm --</span>
                                                    <i class="bi bi-chevron-down"></i>
                                                </button>

                                                <div class="dropdown-menu w-100 p-2 shadow" id="productDropdownMenu"
                                                    style="max-height: 400px; overflow-y: auto;">
                                                    <div class="mb-2">
                                                        <input type="text" id="searchProduct"
                                                            class="form-control form-control-sm w-100"
                                                            placeholder="🔍 Tìm sản phẩm...">
                                                    </div>
                                                    <div id="productList" class="list-group small">
                                                        <!-- JS sẽ render sản phẩm từ server -->
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Thông tin đăng ký -->
                                        <div class="row g-3">
                                            <div class="mb-3 col-4">
                                                <label class="form-label">Số lượng đăng ký</label>
                                                <input type="number" id="flashsaleQuantityInput"
                                                    name="flashsaleQuantityInput" class="form-control" />
                                            </div>
                                            <div class="mb-3 col-4">
                                                <label class="form-label">Giá Flash Sale (VND)</label>
                                                <input type="number" id="flashsalePriceInput" name="flashsalePriceInput"
                                                    class="form-control" />
                                            </div>
                                            <div class="mb-3 col-4">
                                                <label class="form-label">
                                                    Giới hạn mỗi người mua
                                                    <i class="bi bi-question-circle ms-1 text-muted"
                                                        title="Số lượng tối đa mà mỗi khách hàng được mua trong Flash Sale này"></i>
                                                </label>

                                                <input type="number" min="1" step="1" id="flashsaleLimitInput"
                                                    name="flashsaleLimitInput" class="form-control"
                                                    placeholder="(tuỳ chọn)" />
                                            </div>
                                        </div>

                                        <!-- Footer -->
                                        <div class="modal-footer bg-light">
                                            <button type="button" class="btn btn-outline-secondary"
                                                data-bs-dismiss="modal">
                                                <i class="bi bi-x-circle me-1"></i> Hủy
                                            </button>
                                            <button type="submit" class="btn btn-success text-white fw-semibold"
                                                id="flashsaleBtnSubmitRegister" style="background-color: #154C3D">
                                                <i class="bi bi-check-circle me-1"></i> Đăng ký
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Modal xác nhận đăng ký Flash Sale -->
                    <div class="modal fade" id="flashsaleConfirmModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered modal-lg">
                            <div class="modal-content border-0 shadow-lg rounded-4">

                                <!-- Header -->
                                <div class="modal-header bg-gradient text-white rounded-top-4"
                                    style="background-color: #154C3D;">
                                    <h5 class="modal-title fw-semibold d-flex align-items-center">
                                        <i class="bi bi-lightning-charge-fill me-2 fs-5"></i>
                                        Xác nhận đăng ký Flash Sale
                                    </h5>
                                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                        aria-label="Đóng"></button>
                                </div>

                                <!-- Body -->
                                <div class="modal-body p-4">
                                    <p class="text-muted mb-4">
                                        Vui lòng kiểm tra lại thông tin đăng ký trước khi xác nhận.
                                    </p>

                                    <!-- Card hiển thị thông tin -->
                                    <div class="card border-0 shadow-sm">
                                        <div class="card-body d-flex align-items-start gap-3">
                                            <img id="confirmProductImg"
                                                src="/assets/images/catalog/products/no-image.jpg" alt="Ảnh sản phẩm"
                                                class="rounded border flex-shrink-0"
                                                style="width: 80px; height: 90px; object-fit: cover;" />

                                            <div class="flex-grow-1">
                                                <h6 id="confirmProductName" class="fw-semibold mb-2 text-truncate"
                                                    style="max-width: 360px;"></h6>

                                                <p class="mb-1"><strong>Giá Flash Sale:</strong>
                                                    <span id="confirmFlashPrice" class="text-danger fw-bold"></span>
                                                </p>
                                                <p class="mb-1"><strong>Số lượng đăng ký:</strong> <span
                                                        id="confirmQuantity"></span></p>
                                                <p class="mb-1"><strong>Giới hạn mỗi người:</strong> <span
                                                        id="confirmLimit"></span></p>
                                                <p class="mb-0"><strong>Thời gian Flash Sale:</strong> <span
                                                        id="confirmFlashTime" class="fst-italic text-muted"></span></p>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="alert alert-warning d-flex align-items-center mt-3 mb-0">
                                        <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
                                        <div>
                                            Sau khi xác nhận, thông tin đăng ký sẽ được gửi lên hệ thống để duyệt.
                                            Bạn không thể chỉnh sửa lại cho đến khi có phản hồi từ quản trị viên.
                                        </div>
                                    </div>
                                </div>

                                <!-- Footer -->
                                <div class="modal-footer d-flex justify-content-between px-4 py-3 border-0">
                                    <button type="button" class="btn btn-light border fw-semibold"
                                        data-bs-dismiss="modal">
                                        <i class="bi bi-arrow-left-circle me-1"></i> Quay lại chỉnh sửa
                                    </button>
                                    <button type="button" class="btn fw-semibold px-4 text-white" id="confirmSubmitBtn"
                                        style="background-color: #154C3D;">
                                        <i class="bi bi-check-circle me-1"></i> Xác nhận đăng ký
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- JS -->
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/flashSale.js?v=1.0.2"></script>
                </body>

                </html>