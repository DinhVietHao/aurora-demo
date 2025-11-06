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
                <link rel="stylesheet" href="${ctx}/assets/css/customer/profile/information_account.css">
                <link rel="stylesheet" href="${ctx}/assets/css/customer/order/order.css">
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
                                                <a class="nav-link ${param.status == 'waiting_ship' ? 'active' : ''}"
                                                    href="order?status=waiting_ship">Chờ giao hàng</a>
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
                                                <c:when test="${empty orderShops}">
                                                    <div class="text-center">
                                                        <img src="./assets/images/common/empty-order.png" alt="" />
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
                                                        <c:forEach var="entry" items="${orderShops}">
                                                            <div class="order-card">
                                                                <div class="order-card__header">
                                                                    <span><strong><i class="bi bi-shop me-2"></i>
                                                                            ${entry.value[0].shopName}</strong>
                                                                        <a href="${ctx}/home?action=view-shop&shopId=${entry.value[0].shopId}"
                                                                            class="button-outline mx-2">
                                                                            Xem
                                                                            shop</a></span>

                                                                    <div class="d-flex align-items-center">
                                                                        <div class="text-color small me-2">
                                                                            <small class="text-muted">
                                                                                <i class="bi bi-calendar3 me-1"></i>
                                                                                Ngày đặt:
                                                                                <fmt:formatDate
                                                                                    value="${entry.value[0].createdAt}"
                                                                                    pattern="dd-MM-yyyy" />
                                                                            </small>
                                                                        </div>

                                                                        <div><span class="text-color">Trạng thái:
                                                                            </span>
                                                                            <c:set var="badgeClass"
                                                                                value="bg-secondary text-light" />

                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'PENDING_PAYMENT'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-warning text-dark" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'PENDING'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-info text-dark" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'WAITING_SHIP'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-primary" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'SHIPPING'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-secondary" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'CONFIRM'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-info" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'COMPLETED'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-success" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'CANCELLED'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-danger" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'RETURNED_REQUESTED'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-warning text-dark" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'RETURNED'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-success" />
                                                                                </c:when>
                                                                                <c:when
                                                                                    test="${entry.value[0].shopStatus == 'RETURNED_REJECTED'}">
                                                                                    <c:set var="badgeClass"
                                                                                        value="bg-danger" />
                                                                                </c:when>
                                                                            </c:choose>

                                                                            <span class="badge ${badgeClass}">
                                                                                ${entry.value[0].vietnameseStatus}
                                                                            </span>
                                                                        </div>
                                                                    </div>

                                                                </div>

                                                                <c:forEach var="orderShop" items="${entry.value}">
                                                                    <a href="/order/detail?id=${orderShop.orderShopId}">
                                                                        <div class="order-card__body">
                                                                            <div class="col-2 text-center">
                                                                                <img class="order-card__image"
                                                                                    src="${ctx}/assets/images/catalog/products/${orderShop.imageUrl}"
                                                                                    alt="Ảnh sảm phẩm">
                                                                            </div>
                                                                            <div class="col-10">
                                                                                <h6>${orderShop.productName}</h6>
                                                                                <div
                                                                                    class="d-flex justify-content-between">
                                                                                    <p class="text-color">Số lượng:
                                                                                        ${orderShop.quantity}
                                                                                    </p>
                                                                                    <div>
                                                                                        <c:if
                                                                                            test="${orderShop.originalPrice != orderShop.salePrice}">
                                                                                            <span
                                                                                                class="text-decoration-line-through text-color">
                                                                                                <fmt:formatNumber
                                                                                                    value="${orderShop.originalPrice}"
                                                                                                    type="currency" />
                                                                                            </span>
                                                                                        </c:if>
                                                                                        <span
                                                                                            class="fw-bold text-danger">
                                                                                            <fmt:formatNumber
                                                                                                value="${orderShop.salePrice}"
                                                                                                type="currency" />
                                                                                        </span>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </a>
                                                                </c:forEach>

                                                                <div class="text-end">
                                                                    <p class="text-color">Thành tiền: <span
                                                                            class="order-card__price">
                                                                            <fmt:formatNumber
                                                                                value="${entry.value[0].shopFinalAmount}"
                                                                                type="currency" />
                                                                        </span></span>
                                                                    </p>

                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'PENDING'}">
                                                                        <button class="button-six btn-cancel-order"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#cancelOrderModal"
                                                                            data-order-shop-id="${entry.value[0].orderShopId}">
                                                                            Hủy
                                                                            đơn</button>
                                                                    </c:if>
                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'CANCELLED'}">
                                                                        <button class="button-four btnRepurchase"
                                                                            data-order-shop-id="${entry.value[0].orderShopId}"><i
                                                                                class="bi bi-arrow-repeat me-1"></i>
                                                                            Mua
                                                                            lại</button>
                                                                    </c:if>


                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus == 'COMPLETED'}">
                                                                        <button class="button-four btnRepurchase"
                                                                            data-order-shop-id="${entry.value[0].orderShopId}"><i
                                                                                class="bi bi-arrow-repeat me-1"></i>
                                                                            Mua
                                                                            lại</button>
                                                                        <button class="button-five"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#ratingModal">
                                                                            Đánh giá
                                                                            shop</button>
                                                                    </c:if>

                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus == 'RETURNED'}">
                                                                        <button class="button-four btnRepurchase"
                                                                            data-order-shop-id="${entry.value[0].orderShopId}"><i
                                                                                class="bi bi-arrow-repeat me-1"></i>
                                                                            Mua
                                                                            lại</button>
                                                                    </c:if>

                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus == 'COMPLETED' && entry.value[0].canReturn}">
                                                                        <button class="button-seven btn-return-order"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#returnOrderModal"
                                                                            data-order-shop-id="${entry.value[0].orderShopId}">
                                                                            Trả hàng
                                                                        </button>
                                                                    </c:if>

                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'CONFIRM'}">
                                                                        <button class="button-four btn-confirm-order"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#confirmOrderModal"
                                                                            data-order-shop-id="${entry.value[0].orderShopId}">Đã
                                                                            nhận
                                                                            hàng</button>
                                                                    </c:if>

                                                                    <c:if
                                                                        test="${entry.value[0].shopStatus  == 'PENDING_PAYMENT'}">
                                                                        <form action="/order/repayment" method="post">
                                                                            <input type="hidden" name="paymentId"
                                                                                value="${entry.value[0].paymentId}" />
                                                                            <button class="button-four">
                                                                                Thanh toán
                                                                                lại</button>
                                                                        </form>

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

                <!-- Modal product reviews -->
                <div class="modal fade" id="ratingModal" tabindex="-1" aria-labelledby="ratingModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog modal-lg modal-dialog-centered">
                        <div class="modal-content rounded-3 shadow">
                            <div class="modal-header">
                                <h5 class="modal-title fw-bold" id="ratingModalLabel">Đánh Giá Sản Phẩm</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <div class="d-flex mb-3">
                                    <img src="./assets/images/product-1.png" class="rounded border me-3" alt="Sản phẩm">
                                    <div>
                                        <p class="mb-1 fw-semibold">Sách Gặp Chính Mình - Phương pháp sống tỉnh thức -
                                            Cẩm nang tâm
                                            linh thực dụng</p>
                                        <small class="text-muted">Phân loại: Bìa mềm</small>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="fw-semibold">Nội dung đánh giá</label>
                                    <textarea class="form-control" rows="3"
                                        placeholder="Hãy chia sẻ trải nghiệm của bạn về sản phẩm này với những người mua khác nhé."></textarea>
                                </div>
                                <div class="mb-3">
                                    <label for="fileInput" class="button-four me-2">
                                        <i class="bi bi-camera"></i> Thêm Hình ảnh
                                    </label>
                                    <input type="file" id="fileInput" style="display: none;" multiple>
                                    <div id="previewImages" class="d-flex flex-wrap mt-2 gap-2"></div>

                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="button-five" data-bs-dismiss="modal">Trở lại</button>
                                <button type="button" class="button-four">Hoàn thành</button>
                            </div>
                        </div>
                    </div>
                </div>
                <!--End Modal product reviews -->

                <!-- Link Javascript of Order -->
                <script src="${ctx}/assets/js/customer/order/order.js"></script>
                <!-- Include scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

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