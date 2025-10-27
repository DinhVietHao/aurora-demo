<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <fmt:setLocale value="vi_VN" />
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
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/orderDetails.css?v=1.0.1">
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />
                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <!-- Header -->
                                    <div class="d-flex justify-content-between align-items-center mt-4">
                                        <h1 class="order-details-title">Chi tiết Đơn hàng #${orderShop.orderShopId}</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item"><a
                                                        href="${ctx}/shop/dashboard">Dashboard</a></li>
                                                <li class="breadcrumb-item"><a href="${ctx}/shop/orders">Đơn hàng</a>
                                                </li>
                                                <li class="breadcrumb-item active" aria-current="page">Chi tiết</li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <!-- Thông tin đơn hàng -->
                                    <div class="card mt-4 order-header-card">
                                        <div class="card-body d-flex justify-content-between align-items-center">
                                            <div>
                                                <h5>Mã đơn hàng: #${orderShop.orderShopId}</h5>
                                                <c:choose>
                                                    <c:when test="${orderShop.status == 'PENDING'}">
                                                        <span class="badge bg-warning text-dark">Chờ xác nhận</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'SHIPPING'}">
                                                        <span class="badge bg-primary">Đang giao hàng</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'WAITING_SHIP'}">
                                                        <span class="badge bg-info text-dark">Chờ giao hàng</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'CONFIRM'}">
                                                        <span class="badge bg-secondary">Chờ xác nhận của khách
                                                            hàng</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'COMPLETED'}">
                                                        <span class="badge bg-success">Hoàn thành</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'RETURNED_REJECTED'}">
                                                        <span class="badge bg-danger">Trả hàng thất bại</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'RETURNED'}">
                                                        <span class="badge bg-success">Đã xác nhận trả hàng</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'RETURNED_REQUESTED'}">
                                                        <span class="badge bg-warning">Yêu cầu trả hàng</span>
                                                    </c:when>
                                                    <c:when test="${orderShop.status == 'CANCELLED'}">
                                                        <span class="badge bg-danger">Đã hủy</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Không xác định</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="text-muted ms-2">
                                                    Ngày tạo:
                                                    <fmt:formatDate value="${orderShop.createdAt}"
                                                        pattern="dd/MM/yyyy HH:mm" />
                                                </span>

                                                <c:if
                                                    test="${orderShop.status == 'CANCELLED' && not empty orderShop.cancelReason}">
                                                    <div class="mt-2 text-danger fw-semibold">
                                                        <i class="bi bi-exclamation-triangle"></i>
                                                        Lý do hủy: ${orderShop.cancelReason}
                                                    </div>
                                                </c:if>
                                                <c:if
                                                    test="${(orderShop.status == 'RETURNED' || orderShop.status == 'RETURNED_REJECTED' || orderShop.status == 'RETURNED_REQUESTED') && not empty orderShop.returnReason}">
                                                    <div class="mt-2 text-danger fw-semibold">
                                                        <i class="bi bi-arrow-counterclockwise"></i>
                                                        Lý do hoàn tiền: ${orderShop.returnReason}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div>

                                                <c:choose>
                                                    <c:when test="${orderShop.status == 'PENDING'}">
                                                        <form action="${ctx}/shop/orders?action=update-status"
                                                            method="post" class="d-inline status-form">
                                                            <input type="hidden" name="orderShopId"
                                                                value="${orderShop.orderShopId}" />
                                                            <input type="hidden" name="newStatus" value="SHIPPING" />
                                                            <button type="button"
                                                                class="btn btn-warning btn-sm btn-show-modal"
                                                                data-bs-toggle="modal" data-bs-target="#confirmModal"
                                                                data-message="Bạn có chắc rằng đơn hàng này đã được đóng gói và sẵn sàng giao cho đơn vị vận chuyển?"
                                                                data-form="status-form">
                                                                <i class="bi bi-truck"></i> Chuyển trạng thái giao
                                                                hàng
                                                            </button>
                                                        </form>
                                                    </c:when>


                                                    <c:when test="${orderShop.status == 'SHIPPING'}">
                                                        <form action="${ctx}/shop/orders?action=update-status"
                                                            method="post" class="d-inline status-form">
                                                            <input type="hidden" name="orderShopId"
                                                                value="${orderShop.orderShopId}" />
                                                            <input type="hidden" name="newStatus"
                                                                value="WAITING_SHIP" />
                                                            <button type="button"
                                                                class="btn btn-primary btn-sm btn-show-modal"
                                                                data-bs-toggle="modal" data-bs-target="#confirmModal"
                                                                data-message="Bạn có chắc rằng đơn hàng đã tới được địa phận của khách hàng và chuẩn bị giao hàng?"
                                                                data-form="status-form">
                                                                <i class="bi bi-box-seam"></i> Chuyển trạng thái
                                                                đợi giao hàng
                                                            </button>
                                                        </form>
                                                    </c:when>


                                                    <c:when test="${orderShop.status == 'WAITING_SHIP'}">
                                                        <form action="${ctx}/shop/orders?action=update-status"
                                                            method="post" class="d-inline status-form">
                                                            <input type="hidden" name="orderShopId"
                                                                value="${orderShop.orderShopId}" />
                                                            <input type="hidden" name="newStatus" value="CONFIRM" />
                                                            <button type="button"
                                                                class="btn btn-success btn-sm btn-show-modal"
                                                                data-bs-toggle="modal" data-bs-target="#confirmModal"
                                                                data-message="Đơn hàng đã đến tay người nhận chuyển sang xác nhận của khách hàng?"
                                                                data-form="status-form">
                                                                <i class="bi bi-check2-circle"></i> Đơn hàng đã đến tay
                                                                người nhận
                                                            </button>
                                                        </form>
                                                    </c:when>

                                                    <c:when test="${orderShop.status == 'RETURNED_REQUESTED'}">
                                                        <form action="${ctx}/shop/orders?action=update-status"
                                                            method="post" class="d-inline status-form">
                                                            <input type="hidden" name="orderShopId"
                                                                value="${orderShop.orderShopId}" />
                                                            <input type="hidden" name="newStatus"
                                                                value="RETURNED_REJECTED" />
                                                            <button type="button"
                                                                class="btn btn-danger btn-sm btn-show-modal"
                                                                data-bs-toggle="modal" data-bs-target="#confirmModal"
                                                                data-message="Bạn có chắc chắn muốn từ chối yêu cầu trả hàng của khách không?"
                                                                data-form="status-form">
                                                                <i class="bi bi-x-circle"></i> Từ chối trả hàng
                                                            </button>
                                                        </form>
                                                        <form action="${ctx}/shop/orders?action=update-status"
                                                            method="post" class="d-inline status-form">
                                                            <input type="hidden" name="orderShopId"
                                                                value="${orderShop.orderShopId}" />
                                                            <input type="hidden" name="newStatus" value="RETURNED" />
                                                            <button type="button"
                                                                class="btn btn-warning btn-sm btn-show-modal"
                                                                data-bs-toggle="modal" data-bs-target="#confirmModal"
                                                                data-message="Xác nhận đồng ý cho khách trả hàng?"
                                                                data-form="status-form">
                                                                <i class="bi bi-arrow-counterclockwise"></i> Xác nhận
                                                                trả hàng
                                                            </button>
                                                        </form>
                                                    </c:when>

                                                    <c:when test="${orderShop.status == 'CONFIRM'}">
                                                        <button class="btn btn-warning btn-sm" disabled>
                                                            <i class="bi bi-lock"></i> Đang đợi khách hàng xác nhận
                                                        </button>
                                                    </c:when>

                                                    <c:when test="${orderShop.status == 'CANCELLED'}">
                                                        <button class="btn btn-secondary btn-sm" disabled>
                                                            <i class="bi bi-lock"></i> Đơn hàng đã hủy
                                                        </button>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <button class="btn btn-success btn-sm" disabled>
                                                            <i class="bi bi-lock"></i> Đơn hàng đã hoàn
                                                            tất
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Thông tin khách hàng + Tóm tắt thanh toán -->
                                    <div class="row mt-4">
                                        <!-- Customer Info -->
                                        <div class="col-md-6">
                                            <div class="card h-100">
                                                <div class="card-header">
                                                    <h5><i class="bi bi-person-circle me-2"></i>Thông tin khách hàng
                                                    </h5>
                                                </div>
                                                <div class="card-body">
                                                    <p><strong>Tên:</strong> ${orderShop.user.fullName}</p>
                                                    <p><strong>Email:</strong> ${orderShop.user.email}</p>
                                                    <p><strong>Điện thoại:</strong> ${orderShop.address.phone}</p>
                                                    <p><strong>Địa chỉ:</strong> ${orderShop.address.description},
                                                        ${orderShop.address.ward},
                                                        ${orderShop.address.district}, ${orderShop.address.city}</p>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Payment Summary -->
                                        <div class="col-md-6">
                                            <div class="card h-100">
                                                <div class="card-header">
                                                    <h5><i class="bi bi-receipt me-2"></i>Tóm tắt thanh toán</h5>
                                                </div>
                                                <div class="card-body">

                                                    <div class="d-flex justify-content-between">
                                                        <strong>Tạm tính:</strong>
                                                        <span>
                                                            <fmt:formatNumber value="${orderShop.subtotal}"
                                                                pattern="#,##0" /> VND
                                                        </span>
                                                    </div>

                                                    <div class="d-flex justify-content-between">
                                                        <strong>Phí vận chuyển:</strong>
                                                        <span>
                                                            <fmt:formatNumber value="${orderShop.shippingFee}"
                                                                pattern="#,##0" /> VND
                                                        </span>
                                                    </div>

                                                    <div class="d-flex justify-content-between">
                                                        <strong>Phí voucher:</strong>
                                                        <span>
                                                            -
                                                            <fmt:formatNumber value="${orderShop.discount}"
                                                                pattern="#,##0" /> VND
                                                        </span>
                                                    </div>

                                                    <hr>

                                                    <c:set var="totalAmount"
                                                        value="${orderShop.subtotal + orderShop.shippingFee - orderShop.discount}" />

                                                    <c:if test="${totalAmount < 0}">
                                                        <c:set var="totalAmount" value="0" />
                                                    </c:if>

                                                    <div class="d-flex justify-content-between">
                                                        <strong>Tổng cộng:</strong>
                                                        <span class="text-primary fw-bold">
                                                            <fmt:formatNumber value="${totalAmount}" pattern="#,##0" />
                                                            VND
                                                        </span>
                                                    </div>

                                                    <hr>

                                                    <div class="payment-method">
                                                        <strong>Phương thức thanh toán:</strong>
                                                        <div class="mt-2">
                                                            <span class="badge bg-success">
                                                                <i class="bi bi-credit-card me-1"></i>Thanh toán online
                                                            </span>
                                                        </div>
                                                        <small class="text-muted">Đã thanh toán</small>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <!-- Danh sách sản phẩm -->
                                    <div class="card mt-4">
                                        <div class="card-header">
                                            <h5><i class="bi bi-box-seam me-2"></i>Sản phẩm trong đơn hàng</h5>
                                        </div>
                                        <div class="card-body">
                                            <div class="table-responsive">
                                                <table class="table table-hover align-middle">
                                                    <thead>
                                                        <tr>
                                                            <th>Sản phẩm</th>
                                                            <th>Đơn giá</th>
                                                            <th>Số lượng</th>
                                                            <th>Thành tiền</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="item" items="${orderShop.items}">
                                                            <tr>
                                                                <td>
                                                                    <div class="d-flex align-items-center">
                                                                        <img src="http://localhost:8080/assets/images/catalog/products/${item.product.primaryImageUrl}"
                                                                            alt="Ảnh sản phẩm"
                                                                            class="product-image me-3"
                                                                            style="width: 100px; height: 100px; object-fit: cover;">
                                                                        <div>
                                                                            <strong>${item.product.title}</strong>
                                                                        </div>
                                                                    </div>
                                                                </td>
                                                                <td>
                                                                    <fmt:formatNumber value="${item.salePrice}"
                                                                        type="currency" currencySymbol="₫" />
                                                                </td>
                                                                <td>${item.quantity}</td>
                                                                <td>
                                                                    <fmt:formatNumber value="${item.subtotal}"
                                                                        type="currency" currencySymbol="₫" />
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>

                            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />

                            <!-- 🔹 Modal xác nhận -->
                            <div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel"
                                aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header confirm-header">
                                            <h5 class="modal-title" id="confirmModalLabel">
                                                <i class="bi bi-question-circle"></i> Xác nhận hành động
                                            </h5>
                                            <button type="button" class="btn-close btn-close-white"
                                                data-bs-dismiss="modal" aria-label="Đóng"></button>
                                        </div>
                                        <b>
                                            <div class="modal-body fs-6 text-muted" id="confirmMessage">
                                            </div>
                                        </b>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-outline-secondary"
                                                data-bs-dismiss="modal">
                                                <i class="bi bi-x-circle"></i> Hủy
                                            </button>
                                            <button type="button" class="btn btn-primary" id="confirmSubmit">
                                                <i class="bi bi-check2-circle"></i> Xác nhận
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/orderDetails.js?v=1.0.1"></script>
                </body>

                </html>