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
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/flashsale.css?v=1.0.1" />
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
                                                            <span class="badge bg-success">Đang diễn
                                                                ra</span>
                                                            <div class="mt-3 text-end">
                                                                <button
                                                                    class="btn btn-outline-success btn-sm btn-join-flashsale"
                                                                    data-id="${f.flashSaleID}">
                                                                    <i class="bi bi-plus-circle me-1"></i>
                                                                    Tham gia
                                                                </button>
                                                            </div>
                                                        </c:when>

                                                        <c:when test="${f.status eq 'SCHEDULED'}">
                                                            <span class="badge bg-warning text-dark">Sắp
                                                                diễn ra</span>
                                                            <div class="mt-3 text-end">
                                                                <button
                                                                    class="btn btn-outline-success btn-sm btn-join-flashsale"
                                                                    data-id="${f.flashSaleID}">
                                                                    <i class="bi bi-plus-circle me-1"></i>
                                                                    Tham gia
                                                                </button>
                                                            </div>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">Đã kết
                                                                thúc</span>
                                                            <div class="mt-3 text-end">
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



                    <!-- 🔹 Modal đăng ký Flash Sale (đã đổi ID) -->
                    <div class="modal fade" id="flashsaleRegisterModal" tabindex="-1">
                        <div class="modal-dialog modal-dialog-centered modal-lg">
                            <div class="modal-content shadow-lg border-0">
                                <div class="modal-header text-white">
                                    <h5 class="modal-title">
                                        <i class="bi bi-lightning-charge-fill me-2"></i> Đăng ký tham gia
                                        Flash Sale
                                    </h5>
                                    <button type="button" class="btn-close btn-close-white"
                                        data-bs-dismiss="modal"></button>
                                </div>

                                <div class="modal-body">
                                    <form id="flashsaleRegisterForm" action="/shop/flashSale" method="post">
                                        <input type="hidden" id="flashsaleProductSelect" name="productId" />
                                        <!-- Danh sách sản phẩm -->
                                        <div class="mb-3 position-relative">
                                            <label class="form-label fw-bold">Chọn sản phẩm tham gia</label>

                                            <div class="dropdown">
                                                <button
                                                    class="btn btn-outline-secondary w-100 text-start d-flex justify-content-between align-items-center"
                                                    type="button" id="dropdownProductBtn">
                                                    <input type="hidden" id="selectedProductId"
                                                        name="selectedProductId" />
                                                    <span id="selectedProductText">-- Chọn sản phẩm --</span>
                                                    <i class="bi bi-chevron-down"></i>
                                                </button>
                                                <input type="hidden" id="hiddenSelectInput" name="productId" />

                                                <div class="dropdown-menu w-100 p-2 shadow" id="productDropdownMenu"
                                                    style="max-height: 400px; overflow-y: auto;">
                                                    <!-- Bộ lọc -->
                                                    <div class="d-flex gap-2 mb-2">
                                                        <select id="filterCategory"
                                                            class="form-select form-select-sm w-50">
                                                            <option value="">Tất cả thể loại</option>
                                                            <option value="thoi-trang">Thời trang</option>
                                                            <option value="giay">Giày dép</option>
                                                            <option value="phu-kien">Phụ kiện</option>
                                                        </select>
                                                        <input type="text" id="searchProduct"
                                                            class="form-control form-control-sm w-50"
                                                            placeholder="🔍 Tìm sản phẩm...">
                                                    </div>

                                                    <div id="productList" class="list-group small">
                                                        <!-- Danh sách sản phẩm -->
                                                        <button
                                                            class="list-group-item list-group-item-action d-flex align-items-center"
                                                            data-id="101" data-category="thoi-trang"
                                                            data-name="Áo Thun Năng Động">
                                                            <img src="http://localhost:8080/assets/images/catalog/products/1760193019915_khonggiadinh-1.jpg"
                                                                class="rounded me-2" alt="">
                                                            <div>
                                                                <div class="fw-semibold">Áo Thun Năng Động
                                                                </div>
                                                                <small class="text-muted">250.000 đ</small>
                                                            </div>
                                                        </button>

                                                        <button
                                                            class="list-group-item list-group-item-action d-flex align-items-center"
                                                            data-id="102" data-category="giay"
                                                            data-name="Giày Sneaker Classic">
                                                            <img src="http://localhost:8080/assets/images/catalog/products/1760193019915_khonggiadinh-1.jpg"
                                                                class="rounded me-2" alt="">
                                                            <div>
                                                                <div class="fw-semibold">Giày Sneaker
                                                                    Classic</div>
                                                                <small class="text-muted">890.000 đ</small>
                                                            </div>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>


                                        <!-- Thông tin đăng ký -->
                                        <div class="row g-3">
                                            <div class="mb-3 col-6">
                                                <label class="form-label">Số lượng đăng ký</label>
                                                <input type="number" id="flashsaleQuantityInput" name="quantity"
                                                    class="form-control" />
                                            </div>

                                            <div class="mb-3 col-6">
                                                <label class="form-label">Giá Flash Sale (VND)</label>
                                                <input type="number" id="flashsalePriceInput" name="price"
                                                    class="form-control" />
                                            </div>
                                        </div>
                                        <div class="modal-footer bg-light">
                                            <button type="button" class="btn btn-outline-secondary"
                                                data-bs-dismiss="modal">
                                                <i class="bi bi-x-circle me-1"></i> Hủy
                                            </button>
                                            <button type="submit" class="btn btn-success"
                                                id="flashsaleBtnSubmitRegister">
                                                <i class="bi bi-check-circle me-1"></i> Đăng ký
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- JS -->
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/flashSale.js?v=1.0.2"></script>
                </body>

                </html>