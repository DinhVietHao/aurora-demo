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
                <link rel="stylesheet" href="./assets/css/customer/order/order.css?v=1.0.2">
            </head>

            <body>
                <!-- Header + các modal auth dùng chung -->
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <div class="container my-3 information-account">
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
                                        <div class="tab-content order-body">
                                            <c:choose>
                                                <c:when test="${empty orderShops}">
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
                                                        <c:forEach var="orderShop" items="${orderShops}">
                                                            <div class="order-card">
                                                                <div class="order-header">
                                                                    <div
                                                                        class="d-flex flex-wrap justify-content-between align-items-center mb-3">
                                                                        <span class="order-id"><i
                                                                                class="bi bi-receipt me-1">
                                                                            </i>#${orderShop.groupOrderCode}</span>
                                                                        <span class="date-badge"><i
                                                                                class="bi bi-calendar"></i>
                                                                            <fmt:formatDate
                                                                                value="${orderShop.createdAt}"
                                                                                pattern="dd/MM/yyyy" />
                                                                        </span>
                                                                    </div>
                                                                </div>
                                                                <div class="row g-2">
                                                                    <div class="col-md-4">
                                                                        <div class="info-box">
                                                                            <i class="bi bi-person-circle"></i>
                                                                            <div>
                                                                                <div
                                                                                    class="fw-semibold small text-muted">
                                                                                    Người mua</div>
                                                                                <div class="fw-medium text-dark">
                                                                                    ${orderShop.customerName}</div>
                                                                            </div>
                                                                        </div>
                                                                    </div>

                                                                    <div class="col-md-4">
                                                                        <div class="info-box bg-address">
                                                                            <i class="bi bi-geo-alt"></i>
                                                                            <div>
                                                                                <div
                                                                                    class="fw-semibold small text-muted">
                                                                                    Địa chỉ giao hàng</div>
                                                                                <div class="fw-medium text-dark">
                                                                                    ${address}</div>
                                                                            </div>
                                                                        </div>
                                                                    </div>

                                                                    <div class="col-md-4">
                                                                        <div class="info-box bg-total">
                                                                            <i class="bi bi-wallet2"></i>
                                                                            <div>
                                                                                <div
                                                                                    class="fw-semibold small text-muted">
                                                                                    Tổng tiền hàng</div>
                                                                                <div class="fw-medium text-dark">
                                                                                    <fmt:formatNumber
                                                                                        value="${orderShop.subtotal}"
                                                                                        type="currency"
                                                                                        currencySymbol="₫"
                                                                                        maxFractionDigits="0" />
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>

                                                                    <div class="col-md-4">
                                                                        <div class="info-box bg-ship">
                                                                            <i class="bi bi-truck"></i>
                                                                            <div>
                                                                                <div
                                                                                    class="fw-semibold small text-muted">
                                                                                    Phí vận chuyển</div>
                                                                                <div class="fw-medium text-dark">
                                                                                    <fmt:formatNumber
                                                                                        value="${orderShop.shippingFee}"
                                                                                        type="currency"
                                                                                        currencySymbol="₫"
                                                                                        maxFractionDigits="0" />
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>

                                                                    <div class="col-md-4">
                                                                        <div class="info-box bg-ship">
                                                                            <i class="bi bi-ticket-perforated"></i>
                                                                            <div>
                                                                                <div
                                                                                    class="fw-semibold small text-muted">
                                                                                    Voucher giảm giá:</div>
                                                                                <div class="fw-medium text-dark">
                                                                                    -
                                                                                    <fmt:formatNumber
                                                                                        value="${orderShop.shopDiscount + orderShop.systemDiscount}"
                                                                                        type="currency"
                                                                                        currencySymbol="₫"
                                                                                        maxFractionDigits="0" />
                                                                                </div>
                                                                            </div>
                                                                        </div>

                                                                    </div>

                                                                    <div class="col-md-4">
                                                                        <div class="info-box bg-ship">
                                                                            <i class="bi bi-truck"></i>
                                                                            <div>
                                                                                <div
                                                                                    class="fw-semibold small text-muted">
                                                                                    Voucher freeship:</div>
                                                                                <div class="fw-medium text-dark">
                                                                                    -
                                                                                    <fmt:formatNumber
                                                                                        value="${orderShop.systemShippingDiscount}"
                                                                                        type="currency"
                                                                                        currencySymbol="₫"
                                                                                        maxFractionDigits="0" />
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                    <div class="section-title mt-3">
                                                                        <a href="/order/shop?orderShopId=${orderShop.orderShopId}"
                                                                            class="btn btn-detail">
                                                                            <i class="bi bi-eye"></i> Xem chi tiết
                                                                        </a>
                                                                        <div class="total-info">
                                                                            <span>Tổng thanh toán:</span>
                                                                            <span class="total">
                                                                                <fmt:formatNumber
                                                                                    value="${orderShop.finalAmount}"
                                                                                    type="currency" currencySymbol="₫"
                                                                                    maxFractionDigits="0" />
                                                                            </span>
                                                                        </div>
                                                                    </div>
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
                <form id="cancelOrderForm" method="POST" action="/order/cancel">
                    <input type="hidden" name="orderShopId" value="" id="cancelOrderShopId">
                    <div class="modal fade" id="cancelOrderModal" tabindex="-1" aria-labelledby="cancelOrderLabel"
                        aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content"> <!-- Header -->
                                <div class="modal-header">
                                    <h5 class="modal-title" id="cancelOrderLabel">Huỷ đơn hàng</h5> <button
                                        type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Đóng"></button>
                                </div> <!-- Body -->
                                <div class="modal-body">
                                    <p>Bạn có chắc chắn muốn huỷ đơn hàng này không?</p>

                                    <div class="mt-3">
                                        <label for="cancelReason" class="form-label">Lý do hủy đơn hàng</label>
                                        <select class="form-select" id="cancelReason" name="cancelReason" required>
                                            <option value="" selected disabled>-- Chọn lý do hủy --</option>
                                            <c:forEach var="reason" items="${cancelReasons}">
                                                <option value="${reason.label}">${reason.label}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="button-five" data-bs-dismiss="modal">Đóng</button>
                                    <button type="submit" form="cancelOrderForm" class="button-seven">Xác nhận
                                        huỷ</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                <!--End Cancel Order Modal -->

                <!-- Return Order Modal -->
                <form id="returnOrderForm" method="POST" action="/order/return">
                    <input type="hidden" name="orderShopId" value="" id="returnOrderShopId">
                    <div class="modal fade" id="returnOrderModal" tabindex="-1" aria-labelledby="returnOrderLabel"
                        aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="returnOrderLabel">Trả hàng</h5> <button type="button"
                                        class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                                </div>
                                <div class="modal-body">
                                    <p>Bạn có chắc chắn muốn trả hàng này không?</p>
                                    <div class="mt-3">
                                        <label for="returnReason" class="form-label">Lý do trả hàng</label>
                                        <select class="form-select" id="returnReason" name="returnReason" required>
                                            <option value="" selected disabled>-- Chọn lý do trả hàng --</option>
                                            <c:forEach var="reason" items="${returnReasons}">
                                                <option value="${reason.label}">${reason.label}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="button-five" data-bs-dismiss="modal">Đóng</button>
                                    <button type="submit" form="returnOrderForm" class="button-seven">Xác nhận
                                        trả hàng</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                <!--End Return Order Modal -->

                <!-- Confirm Order Received Modal -->
                <form action="/order/confirm" method="post">
                    <input type="hidden" name="orderShopId" value="" id="confirmOrderShopId">
                    <div class="modal fade" id="confirmOrderModal" tabindex="-1" aria-labelledby="confirmOrderLabel"
                        aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="confirmOrderLabel">Xác nhận đã nhận hàng</h5> <button
                                        type="button" class="btn-close" data-bs-dismiss="modal"
                                        aria-label="Đóng"></button>
                                </div>
                                <div class="modal-body">
                                    <p>Bạn đã chắc chắn nhận đủ hàng từ đơn hàng này chưa?</p>
                                    <p>Sau khi xác nhận, đơn hàng sẽ được chuyển sang trạng thái <strong>Hoàn
                                            thành</strong>.</p> <input type="hidden" name="orderId" id="confirmOrderId"
                                        value="">
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="button-five" data-bs-dismiss="modal">Đóng</button>
                                    <button type="submit" id="btnConfirmOrder" class="button-four">Xác nhận</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                <!--End Confirm Order Received Modal -->

                <!-- Link Javascript of Order -->
                <script src="./assets/js/customer/order/order.js"></script>

                <!-- Link Javascript of Information Account -->
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