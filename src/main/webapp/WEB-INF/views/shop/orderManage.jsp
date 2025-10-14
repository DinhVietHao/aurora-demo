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

                <body class="sb-nav-fixed" data-ctx="${ctx}">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
                                <!-- ✅ Thông báo -->
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

                                <div class="container-fluid px-4">
                                    <!-- Tiêu đề -->
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 order-management-title">Tất cả</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a href="/home">Trang chủ</a></li>
                                                <li class="breadcrumb-item"><a href="/home/dashboard">Dashboard</a></li>
                                                <li class="breadcrumb-item active" aria-current="page">Đơn hàng</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Tabs Trạng thái -->
                                    <div class="card mt-3">
                                        <div class="card-body">
                                            <ul class="nav nav-tabs order-tabs">
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'ALL' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=ALL">Tất cả</a></li>
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'PENDING' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=PENDING">Chờ xác nhận</a></li>
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'SHIPPING' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=CONFIRMED">Vận chuyển</a></li>
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'WAITING_SHIP' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=SHIPPING">Chờ giao hàng</a></li>
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'COMPLETED' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=DELIVERED">Hoàn thành</a></li>
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'CANCELLED' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=CANCELLED">Hủy</a>
                                                </li>
                                                <li class="nav-item"><a
                                                        class="nav-link ${status == 'RETURNED' ? 'active' : ''}"
                                                        href="${ctx}/shop/orders?status=RETURNED">Hoàn Tiền</a>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>

                                    <!-- Cảnh báo -->
                                    <div class="alert alert-warning alert-dismissible fade show mt-3" role="alert">
                                        <i class="bi bi-exclamation-triangle me-2"></i>
                                        <strong>Admin:</strong> Cùng nhau phấn đấu, mỗi đơn hàng là một cơ hội để thể
                                        hiện trách nhiệm của bản thân!
                                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                            aria-label="Close"></button>
                                    </div>

                                    <!-- Bộ lọc -->
                                    <div class="card mt-3">
                                        <div class="card-body">
                                            <div class="row g-3">
                                                <div class="col-md-3">
                                                    <label for="orderSearch" class="form-label">Mã đơn hàng</label>
                                                    <input type="text" class="form-control" id="orderSearch"
                                                        placeholder="Nhập mã đơn hàng">
                                                </div>
                                                <div class="col-md-3">
                                                    <label for="customerSearch" class="form-label">Tên khách
                                                        hàng</label>
                                                    <input type="text" class="form-control" id="customerSearch"
                                                        placeholder="Nhập tên khách hàng">
                                                </div>
                                                <div class="col-md-2 d-flex align-items-end gap-2">
                                                    <button type="button" class="btn btn-primary">Áp dụng</button>
                                                    <button type="button" class="btn btn-outline-secondary">Đặt
                                                        lại</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Danh sách đơn hàng -->
                                    <div class="order-summary mt-4">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <h5 class="mb-0">
                                                <c:choose>
                                                    <c:when test="${not empty orders}">
                                                        ${fn:length(orders)} đơn hàng
                                                    </c:when>
                                                    <c:otherwise>Không có đơn hàng nào</c:otherwise>
                                                </c:choose>
                                            </h5>
                                        </div>

                                        <div class="row fw-bold border-bottom pb-2">
                                            <div class="col-md-5">Sản phẩm</div>
                                            <div class="col-md-2">Tổng tiền</div>
                                            <div class="col-md-3">Trạng thái</div>
                                            <div class="col-md-2">Thao tác</div>
                                        </div>

                                        <div class="card-body py-4 px-4">
                                            <c:forEach var="order" items="${orders}">
                                                <div class="card mb-4 border-0 shadow-sm order-card-hover">
                                                    <div
                                                        class="card-header bg-light d-flex justify-content-between align-items-center py-3 rounded-top">
                                                        <div>
                                                            <strong class="text-primary">Mã đơn hàng:
                                                                #${order.orderId}</strong>
                                                            <span class="text-muted ms-2">(${order.customerName})</span>
                                                        </div>
                                                        <small class="text-muted">
                                                            <fmt:formatDate value="${order.createdAt}"
                                                                pattern="dd/MM/yyyy HH:mm" />
                                                        </small>
                                                    </div>

                                                    <div class="card-body py-4 px-4">
                                                        <c:forEach var="item" items="${order.items}">
                                                            <div class="row align-items-center py-3 border-bottom gx-0">
                                                                <div class="col-md-5 d-flex align-items-center">
                                                                    <img src="${ctx}/assets/images/catalog/products/${item.product.primaryImageUrl}"
                                                                        alt="${item.product.title}"
                                                                        class="rounded border me-3 shadow-sm"
                                                                        style="width: 95px; height: 95px; object-fit: cover;">
                                                                    <div>
                                                                        <h6 class="fw-semibold mb-1 text-dark">
                                                                            ${item.product.title}</h6>
                                                                        <small
                                                                            class="text-muted">x${item.quantity}</small>
                                                                    </div>
                                                                </div>
                                                        </c:forEach>

                                                        <!-- Tổng tiền -->
                                                        <div class="col-md-2 text-start">
                                                            <span class="fw-semibold text-dark">
                                                                <fmt:formatNumber value="${order.totalAmount}"
                                                                    type="number" />₫
                                                            </span>
                                                        </div>

                                                        <!-- Trạng thái -->
                                                        <div class="col-md-3 text-start">
                                                            <c:choose>
                                                                <c:when test="${order.orderStatus == 'PENDING'}">
                                                                    <span
                                                                        class="badge bg-warning text-dark px-3 py-2 fs-6">Chờ
                                                                        xác nhận</span>
                                                                </c:when>
                                                                <c:when test="${order.orderStatus == 'COMPLETED'}">
                                                                    <span class="badge bg-success px-3 py-2 fs-6">Hoàn
                                                                        thành</span>
                                                                </c:when>
                                                                <c:when test="${order.orderStatus == 'CANCELLED'}">
                                                                    <span class="badge bg-danger px-3 py-2 fs-6">Đã
                                                                        hủy</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span
                                                                        class="badge bg-secondary px-3 py-2 fs-6">${order.orderStatus}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>

                                                        <!-- Xem chi tiết -->
                                                        <div class="col-md-2 text-start">
                                                            <a href="${ctx}/shop/order-detail?id=${order.orderId}"
                                                                class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                                                Xem chi tiết
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>
                                        </div>
                                        </c:forEach>
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