<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <fmt:setLocale value="vi_VN" />
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <!-- Reuse head chung -->
                <jsp:include page="/WEB-INF/views/layouts/_head.jsp">
                    <jsp:param name="title" value="Giỏ hàng - Aurora" />
                </jsp:include>

                <!-- CSS riêng trang Cart -->
                <link rel="stylesheet" href="./assets/css/customer/profile/information_account.css?v=1.0.1">
                <link rel="stylesheet" href="./assets/css/customer/order/order.css">
                <link rel="stylesheet" href="./assets/css/customer/address/address.css?v=1.0.1">
            </head>

            <body>
                <!-- Header + các modal auth dùng chung -->
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <div class="container mt-3 information-account">
                    <div class="row ">
                        <div class="col-3 col-md-2 information-account__sidebar">

                            <div class="text-center mb-4">
                                <c:choose>
                                    <c:when test="${user.avatarUrl != null && !user.avatarUrl.isEmpty()}">
                                        <c:set var="avatarPath"
                                            value="http://localhost:8080/assets/images/avatars/${user.avatarUrl}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="avatarPath"
                                            value="http://localhost:8080/assets/images/common/avatar.png" />
                                    </c:otherwise>
                                </c:choose>
                                <img id="avatarSidebar" src="${avatarPath}" alt="avatar"
                                    class="information-account__image">
                                <p class="mt-2 fw-bold mb-0">${user.fullName}</p>
                            </div>

                            <!-- sidebar profile -->
                            <ul class="nav mb-3 " id="profileTabs" role="tablist">
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" href="">
                                        <i class="bi bi-bell me-2"></i> Thông báo
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark " href="/profile">
                                        <i class="bi bi-person me-2"></i> Hồ sơ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark " href="/address">
                                        <i class="bi bi-geo-alt me-2"></i> Địa Chỉ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark active" href="/order">
                                        <i class="bi bi-box-seam me-2"></i> Quản lý đơn hàng
                                    </a>
                                </li>
                            </ul>
                        </div>

                        <div class="col-9 col-md-10 ">
                            <div class="tab-content" id="profileTabsContent">
                                <!-- Thông báo -->
                                <div class="tab-pane fade" id="notify" role="tabpanel" aria-labelledby="notify-tab">
                                    <div class="text-center mt-5">
                                        <img src="./assets/images/mascot_fail.svg" alt="">
                                        <p class="text-muted mt-3">Chưa có thông báo</p>
                                    </div>
                                </div>

                                <!-- Quản lý đơn hàng -->
                                <div class="tab-pane fade show active order-management" id="order" role="tabpanel"
                                    aria-labelledby="order-tab">
                                    <div class="order-content">
                                        <ul class="nav nav-tabs mb-3 order-tabs" id="orderTabs" role="tablist">
                                            <li class="nav-item">
                                                <a class="nav-link ${empty param.status ? 'active' : ''}"
                                                    href="order">Tất cả</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${param.status == 'pending' ? 'active' : ''}"
                                                    href="order?status=pending">Chờ xác nhận</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${param.status == 'shipping' ? 'active' : ''}"
                                                    href="order?status=shipping">Vận chuyển</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${param.status == 'watting_ship' ? 'active' : ''}"
                                                    href="order?status=watting_ship">Chờ giao hàng</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${param.status == 'completed' ? 'active' : ''}"
                                                    href="order?status=completed">Hoàn thành</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${param.status == 'cancelled' ? 'active' : ''}"
                                                    href="order?status=cancelled">Đã hủy</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link ${param.status == 'returned' ? 'active' : ''}"
                                                    href="order?status=returned">Trả hàng</a>
                                            </li>
                                        </ul>
                                        <div class="tab-content order-body">
                                            <c:choose>
                                                <c:when test="${empty orders}">
                                                    <div class="text-center">
                                                        <img src="./assets/images/common/empty-order.png" alt="">
                                                        <p class="text-muted">Chưa có đơn hàng</p>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <!-- Search -->
                                                    <div class="header-search mb-3">
                                                        <span class="icon">
                                                            <i class="bi bi-search"></i>
                                                        </span>
                                                        <input type="text" class="form-control rounded-pill"
                                                            placeholder="Tìm đơn hàng theo mã đơn hàng, nhà bán hoặc tên sản phẩm">
                                                        <button class="btn btn-light btn-sm rounded-pill">Tìm
                                                            kiếm</button>
                                                    </div>

                                                    <div class="tab-pane fade show active " id="all" role="tabpanel">
                                                        <c:forEach var="entry" items="${orders}">
                                                            <div class="order-card">
                                                                <div class="order-card__header">
                                                                    <span><strong><i class="bi bi-shop me-2"></i>
                                                                            ${entry.value[0].shopName}</strong>
                                                                        <a href="./viewShop.html"
                                                                            class="button-outline mx-2">
                                                                            Xem
                                                                            shop</a></span>
                                                                    <div>
                                                                        <span class="text-color">Trạng thái: </span>
                                                                        <span
                                                                            class="badge bg-success">${entry.value[0].shopStatus}</span>
                                                                    </div>
                                                                </div>
                                                                <c:forEach var="order" items="${entry.value}">
                                                                    <div class="order-card__body">
                                                                        <div class="col-2 text-center">
                                                                            <img class="order-card__image"
                                                                                src="${ctx}/assets/images/catalog/products/${order.imageUrl}"
                                                                                alt="Ảnh sảm phẩm">
                                                                        </div>
                                                                        <div class="col-10">
                                                                            <h6>${order.productName}</h6>
                                                                            <p class="mb-2 text-color">Thể loại:
                                                                                <c:forEach var="category"
                                                                                    items="${order.categories}"
                                                                                    varStatus="st">
                                                                                    <c:out value="${category.name}" />
                                                                                    <c:if test="${!st.last}">, </c:if>
                                                                                </c:forEach>
                                                                            </p>
                                                                            <div class="d-flex justify-content-between">
                                                                                <p class="text-color">Số lượng:
                                                                                    ${order.quantity}
                                                                                </p>
                                                                                <div>
                                                                                    <c:if
                                                                                        test="${order.originalPrice != order.salePrice}">
                                                                                        <span
                                                                                            class="text-decoration-line-through text-color">
                                                                                            <fmt:formatNumber
                                                                                                value="${order.originalPrice}"
                                                                                                type="currency" />
                                                                                        </span>
                                                                                    </c:if>
                                                                                    <span class="fw-bold text-danger">
                                                                                        <fmt:formatNumber
                                                                                            value="${order.salePrice}"
                                                                                            type="currency" />
                                                                                    </span>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </c:forEach>

                                                                <div class="text-end">
                                                                    <p class="text-color">Thành tiền: <span
                                                                            class="order-card__price">
                                                                            <fmt:formatNumber
                                                                                value="${entry.value[0].finalAmount}"
                                                                                type="currency" />
                                                                        </span></span>
                                                                    </p>

                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'PENDING'}">
                                                                        <button class="button-six"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#cancelOrderModal"> Hủy
                                                                            đơn</button>
                                                                    </c:if>
                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'CANCELLED'}">
                                                                        <button class="button-four"><i
                                                                                class="bi bi-arrow-repeat me-1"></i> Mua
                                                                            lại</button>
                                                                    </c:if>
                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'PENDING_PAYMENT'}">
                                                                        <button class="button-four">
                                                                            Thanh toán
                                                                            lại</button>
                                                                    </c:if>
                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'COMPLETED'}">
                                                                        <button class="button-four"><i
                                                                                class="bi bi-arrow-repeat me-1"></i> Mua
                                                                            lại</button>
                                                                        <button class="button-five"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#ratingModal">
                                                                            Đánh giá
                                                                            shop</button>
                                                                    </c:if>
                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'WAITING_SHIP'}">
                                                                        <button class="button-four"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#confirmOrderModal">Đã nhận
                                                                            hàng</button>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <!-- Cancel Order Modal -->
                <div class="modal fade" id="cancelOrderModal" tabindex="-1" aria-labelledby="cancelOrderLabel"
                    aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content"> <!-- Header -->
                            <div class="modal-header">
                                <h5 class="modal-title" id="cancelOrderLabel">Huỷ đơn hàng</h5> <button type="button"
                                    class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div> <!-- Body -->
                            <div class="modal-body">
                                <p>Bạn có chắc chắn muốn huỷ đơn hàng này không?</p>
                                <form id="cancelOrderForm">
                                    <div class="mt-3">
                                        <label for="cancelReason" class="form-label">Lý do hủy đơn hàng</label>
                                        <select class="form-select" id="cancelReason" name="cancelReason" required>
                                            <option value="" selected disabled>-- Chọn lý do hủy --</option>
                                            <option value="update_phone_address">Cập nhật số điện thoại hoặc địa chỉ
                                                nhận hàng</option>
                                            <option value="ordered_wrong_product">Đặt nhầm sản phẩm</option>
                                            <option value="ordered_wrong_quantity">Nhập sai số lượng sản phẩm</option>
                                            <option value="ordered_duplicate">Đặt trùng đơn hàng</option>
                                            <option value="changed_mind">Không muốn mua nữa</option>
                                            <option value="found_better_price">Tìm thấy sản phẩm giá rẻ hơn ở nơi khác
                                            </option>
                                            <option value="choose_other_shop">Muốn mua ở shop khác</option>
                                            <option value="shipping_fee_changed">Phí vận chuyển thay đổi so với khi đặt
                                            </option>
                                            <option value="personal_reason">Lý do cá nhân / không tiện nhận hàng
                                            </option>
                                        </select>
                                    </div>
                            </div>
                            <div class="modal-footer"> <button type="button" class="button-five"
                                    data-bs-dismiss="modal">Đóng</button> <button type="submit" form="cancelOrderForm"
                                    class="button-seven">Xác nhận huỷ</button> </div>
                        </div>
                    </div>
                </div>
                <!--End Cancel Order Modal -->

                <!-- Confirm Order Received Modal -->
                <div class="modal fade" id="confirmOrderModal" tabindex="-1" aria-labelledby="confirmOrderLabel"
                    aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="confirmOrderLabel">Xác nhận đã nhận hàng</h5> <button
                                    type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <p>Bạn đã chắc chắn nhận đủ hàng từ đơn <strong>#12345</strong> chưa?</p>
                                <p>Sau khi xác nhận, đơn hàng sẽ được chuyển sang trạng thái <strong>Hoàn
                                        thành</strong>.</p> <input type="hidden" name="orderId" id="confirmOrderId"
                                    value="">
                            </div>
                            <div class="modal-footer"> <button type="button" class="button-five"
                                    data-bs-dismiss="modal">Đóng</button> <button type="button" id="btnConfirmOrder"
                                    class="button-four">Xác nhận</button> </div>
                        </div>
                    </div>
                </div>
                <!--End Confirm Order Received Modal -->

                <!-- Link Javascript of Comment -->
                <script src="./assets/js/customer/order/order.js"></script>

                <script src="./assets/js/customer/profile/information_account.js"></script>
                <c:if test="${not empty sessionScope.toastMsg}">
                    <script>
                        toast({
                            title: "${sessionScope.toastType == 'success' ? 'Thành công' : 'Thất bại'}",
                            message: "${sessionScope.toastMsg}",
                            type: "${sessionScope.toastType}",
                            duration: 3000
                        });
                    </script>
                    <c:remove var="toastMsg" scope="session" />
                    <c:remove var="toastType" scope="session" />
                </c:if>

            </body>

            </html>