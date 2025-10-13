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
                <link rel="stylesheet" href="./assets/css/common/globals.css?v=1.0.1'">
                <link rel="stylesheet" href="./assets/css/customer/cart/cart.css?v=1.0.1">
            </head>

            <body>

                <!-- Header + các modal auth dùng chung -->
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <div class="container cart">
                    <h4 class="cart-title">Giỏ hàng</h4>
                    <c:choose>
                        <c:when test="${empty shopCarts}">
                            <div class="text-center">
                                <img src="${ctx}/assets/images/common/cartEmpty.png" alt="Cart Empty">
                                <p class="text-muted">Giỏ hàng trống</p>
                                <p>Bạn tham khảo thêm các sản phẩm được Aurora gợi ý bên dưới nhé!</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row cart-left">
                                <!-- LEFT: danh sách sản phẩm -->
                                <div class="col-md-9 ">
                                    <div class="row cart-header">
                                        <div class="col-6">
                                            <input class="form-check-input me-2 cursor-pointer cart-checkboxAll"
                                                type="checkbox"> Sản phẩm
                                        </div>
                                        <div class="col-1 text-center">Đơn giá</div>
                                        <div class="col-2 text-center">Số lượng</div>
                                        <div class="col-2 text-center">Thành tiền</div>
                                        <div class="col-1 text-center">Xóa</div>
                                    </div>


                                    <c:forEach var="shopCart" items="${shopCarts}">
                                        <div class="row cart-body" data-shop-id="${shopCart.shop.shopId}">
                                            <div class="col-12 cart-body__header">
                                                <span><strong><i
                                                            class="bi bi-shop me-2"></i>${shopCart.shop.name}</strong>
                                                    <a href="#" class="button-outline mx-2"> Xem shop</a>
                                                </span>
                                            </div>
                                            <c:forEach var="cartItem" items="${shopCart.items}">
                                                <div class="row cart-body__item" id="cartItemId${cartItem.cartItemId}"
                                                    data-cartitemid="${cartItem.cartItemId}"
                                                    data-userid="${cartItem.userId}">
                                                    <div class="col-2 d-flex align-items-center">
                                                        <input class="form-check-input cursor-pointer cart-checkbox"
                                                            type="checkbox" ${cartItem.isChecked ? "checked" : "" }>
                                                        <a href="${ctx}/book?id=${cartItem.product.productId}"
                                                            target="_blank">
                                                            <img src="${ctx}/assets/images/catalog/products/${cartItem.product.images[0].url}"
                                                                class="img-fluid" alt="${cartItem.product.title}">
                                                        </a>
                                                    </div>

                                                    <div class="col-4">
                                                        <a href="${ctx}/book?id=${cartItem.product.productId}"
                                                            target="_blank">
                                                            <h6 class="cart-book-title">
                                                                <c:out value="${cartItem.product.title}" />
                                                            </h6>
                                                        </a>
                                                        <p class="author">
                                                            <c:out value="${cartItem.product.bookDetail.author}" />
                                                        </p>
                                                    </div>

                                                    <div class="col-1 text-center">
                                                        <span class="price unit-price">
                                                            <fmt:formatNumber value="${cartItem.unitPrice}"
                                                                type="currency" />
                                                        </span><br>
                                                        <c:if
                                                            test="${cartItem.product.originalPrice != cartItem.unitPrice}">
                                                            <span class="text-muted text-decoration-line-through">
                                                                <fmt:formatNumber
                                                                    value="${cartItem.product.originalPrice}"
                                                                    type="currency" />
                                                            </span>
                                                        </c:if>
                                                    </div>

                                                    <div class="col-2">
                                                        <div class="text-center">
                                                            <button class="btn btn-outline-secondary btn-sm minus"
                                                                data-cartitemid="${cartItem.cartItemId}">
                                                                -
                                                            </button>
                                                            <span class="mx-2 number">
                                                                <c:out value="${cartItem.quantity}" />
                                                            </span>
                                                            <button
                                                                class="btn btn-outline-secondary btn-sm plus">+</button>
                                                        </div>
                                                    </div>

                                                    <div class="col-2 text-center price subtotal">
                                                        <fmt:formatNumber value="${cartItem.subtotal}"
                                                            type="currency" />
                                                    </div>

                                                    <button type="button" class="col-1 button-delete text-center"
                                                        data-bs-toggle="modal" data-bs-target="#deleteCartModal"
                                                        data-cartitemid="${cartItem.cartItemId}">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </div>
                                            </c:forEach>

                                            <div class="row cart-body__footer" data-shop-id="${shopCart.shop.shopId}">
                                                <div class="col-auto">
                                                    <i class="bi bi-ticket-perforated fs-3"></i>
                                                </div>
                                                <div class="col">
                                                    <div class="shop-voucher-text">Xem tất cả Voucher của Shop</div>
                                                </div>
                                                <div class="col-auto">
                                                    <a class="cursor-pointer text-primary" data-bs-toggle="modal"
                                                        data-bs-target="#shopVoucherModal_${shopCart.shop.shopId}"
                                                        data-shop-id="${shopCart.shop.shopId}">
                                                        Xem thêm voucher
                                                    </a>
                                                </div>
                                            </div>
                                            <!-- Modal Voucher Shop -->

                                            <div class="modal fade cart-shop-voucher"
                                                id="shopVoucherModal_${shopCart.shop.shopId}" tabindex="-1"
                                                aria-hidden="true">
                                                <div class="modal-dialog modal-dialog-scrollable">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">${shopCart.shop.name}</h5>
                                                            <button type="button" class="btn-close"
                                                                data-bs-dismiss="modal"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="row mb-3 align-items-center">
                                                                <div class="col-8">
                                                                    <input type="text" class="form-control"
                                                                        placeholder="Nhập mã giảm giá">
                                                                </div>
                                                                <div class="col-4">
                                                                    <button class="button-four w-100">Áp
                                                                        dụng</button>
                                                                </div>
                                                            </div>
                                                            <c:choose>
                                                                <c:when test="${empty shopCart.vouchers}">
                                                                    <div class="text-center">
                                                                        <img src="${ctx}/assets/images/common/voucherEmpty.png"
                                                                            alt="Cart Empty">
                                                                        <p class="text-muted">Chưa có mã giảm giá nào
                                                                            của shop</p>
                                                                        <p>Nhập mã giá có thể sử dụng vào thanh bên trên
                                                                        </p>
                                                                    </div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="list-group">
                                                                        <c:forEach var="voucher"
                                                                            items="${shopCart.vouchers}">
                                                                            <label
                                                                                class="list-group-item d-flex justify-content-between align-items-center">
                                                                                <div>
                                                                                    <span class="badge bg-success">
                                                                                        ${shopCart.shop.name}</span>
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${voucher.discountType == 'PERCENT'}">
                                                                                            <span class="ms-2">Giảm
                                                                                                <fmt:formatNumber
                                                                                                    value="${voucher.value}"
                                                                                                    type="number"
                                                                                                    maxFractionDigits="0" />
                                                                                                %
                                                                                                tối đa
                                                                                                <fmt:formatNumber
                                                                                                    value="${voucher.maxAmount / 1000}"
                                                                                                    type="number" />K
                                                                                            </span>
                                                                                            <br />
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <span class="ms-2">Giảm
                                                                                                <fmt:formatNumber
                                                                                                    value="${voucher.value / 1000}"
                                                                                                    type="number" />K
                                                                                            </span>
                                                                                            <br />
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                    <small>
                                                                                        Cho đơn từ
                                                                                        <fmt:formatNumber
                                                                                            value="${voucher.minOrderAmount / 1000}"
                                                                                            type="number" />K
                                                                                        - HSD:
                                                                                        <fmt:formatDate
                                                                                            value="${voucher.endAt}"
                                                                                            pattern="dd/MM/yyyyy" />
                                                                                    </small>
                                                                                </div>
                                                                                <input type="radio"
                                                                                    name="voucherShopDiscount_${shopCart.shop.shopId}"
                                                                                    value="discount1"
                                                                                    data-text="Giảm 8% tối đa 30K"
                                                                                    data-discount="${voucher.value}"
                                                                                    data-type="${voucher.discountType}"
                                                                                    data-max="${voucher.maxAmount}"
                                                                                    data-min-order-amount="${voucher.minOrderAmount}">
                                                                            </label>
                                                                        </c:forEach>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="button-five"
                                                                data-bs-dismiss="modal">Trở lại</button>
                                                            <button type="button" class="button-four confirmShopVoucher"
                                                                data-shop-id="${shopCart.shop.shopId}">OK</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>

                                <!-- RIGHT: địa chỉ + tổng tiền -->
                                <div class="col-md-3 cart-right">
                                    <div class="cart-promotion">
                                        <h6 class="cart-promotion-title">Aurora Khuyến Mãi</h6>

                                        <div id="appliedVoucherDiscount"
                                            class="d-flex justify-content-between align-items-center border p-2 rounded mb-2 d-none">
                                            <span id="voucherTextDiscount"></span>
                                            <button id="removeVoucherDiscount" class="btn btn-primary btn-sm">Bỏ
                                                chọn</button>
                                        </div>

                                        <div id="appliedVoucherShip"
                                            class="d-flex justify-content-between align-items-center border p-2 rounded mb-2 d-none">
                                            <span id="voucherTextShip"></span>
                                            <button id="removeVoucherShip" class="btn btn-primary btn-sm">Bỏ
                                                chọn</button>
                                        </div>

                                        <a class="small text-primary cursor-pointer" data-bs-toggle="modal"
                                            data-bs-target="#voucherModal">
                                            Chọn hoặc nhập mã khác
                                        </a>
                                    </div>

                                    <div class="cart-pay">
                                        <div class="d-flex justify-content-between mb-2">
                                            <span>Tổng tiền hàng</span>
                                            <span class="total-product-price">188.000đ</span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="cart-pay-success">Tổng cộng Voucher giảm giá</span>
                                            <span class="cart-pay-success discount">0đ</span>
                                        </div>
                                        <hr>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="cart-pay-danger">Tổng tiền thanh toán</span>
                                            <span class="cart-pay-danger total-payment">142.000đ</span>
                                        </div>
                                        <button class="button-three" id="cart-pay-button">Mua Hàng (0)</button>
                                    </div>
                                </div>
                            </div>
                            <!-- Modals tách riêng để có thể reuse -->
                            <jsp:include page="/WEB-INF/views/customer/cart/partials/_cart_delete_modal.jsp" />
                            <jsp:include page="/WEB-INF/views/customer/cart/partials/_cart_empty_selection_modal.jsp" />
                            <!-- Modal Voucher Cart -->
                            <div class="modal fade cart-system-voucher" id="voucherModal" tabindex="-1"
                                aria-hidden="true">
                                <div class="modal-dialog modal-dialog-scrollable">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Aurora Khuyến Mãi</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="row mb-3 align-items-center">
                                                <div class="col-8">
                                                    <input type="text" class="form-control"
                                                        placeholder="Nhập mã giảm giá">
                                                </div>
                                                <div class="col-4">
                                                    <button class="button-four w-100">Áp dụng</button>
                                                </div>
                                            </div>
                                            <c:choose>
                                                <c:when test="${empty systemVouchers}">
                                                    <div class="text-center">
                                                        <img src="${ctx}/assets/images/common/voucherEmpty.png"
                                                            alt="Cart Empty">
                                                        <p class="text-muted">Chưa có mã giảm giá nào
                                                            của Aurora</p>
                                                        <p>Nhập mã giá có thể sử dụng vào thanh bên trên
                                                        </p>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <h6 class="fw-bold mb-2">Mã Giảm Giá</h6>
                                                    <div class="list-group">
                                                        <c:forEach var="systemVoucher" items="${systemVouchers}">
                                                            <c:if test="${systemVoucher.discountType != 'SHIPPING'}">
                                                                <label
                                                                    class="list-group-item d-flex justify-content-between align-items-center">
                                                                    <div>
                                                                        <span class="badge bg-success">Aurora</span>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${systemVoucher.discountType == 'PERCENT'}">
                                                                                <span class="ms-2">Giảm
                                                                                    <fmt:formatNumber
                                                                                        value="${systemVoucher.value}"
                                                                                        type="number"
                                                                                        maxFractionDigits="0" />%
                                                                                    tối đa
                                                                                    <fmt:formatNumber
                                                                                        value="${systemVoucher.maxAmount / 1000}"
                                                                                        type="number" />K
                                                                                </span>
                                                                                <br />
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="ms-2">Giảm
                                                                                    <fmt:formatNumber
                                                                                        value="${systemVoucher.value / 1000}"
                                                                                        type="number" />K
                                                                                </span>
                                                                                <br />
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                        <small>
                                                                            Cho đơn từ
                                                                            <fmt:formatNumber
                                                                                value="${systemVoucher.minOrderAmount / 1000}"
                                                                                type="number" />K
                                                                            - HSD:
                                                                            <fmt:formatDate
                                                                                value="${systemVoucher.endAt}"
                                                                                pattern="dd/MM/yyyyy" />
                                                                        </small>
                                                                    </div>
                                                                    <fmt:formatNumber
                                                                        value="${systemVoucher.maxAmount/1000}"
                                                                        type="number" var="maxAmount" />
                                                                    <fmt:formatNumber
                                                                        value="${systemVoucher.value/1000}"
                                                                        type="number" var="valueAmount" />
                                                                    <fmt:formatNumber value="${systemVoucher.value}"
                                                                        type="number" maxFractionDigits="0"
                                                                        var="valuePercent" />
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${systemVoucher.discountType == 'PERCENT'}">
                                                                            <c:set var="voucherText"
                                                                                value="Giảm ${valuePercent}% tối đa ${maxAmount}K" />
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <c:set var="voucherText"
                                                                                value="Giảm ${valueAmount}K" />
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <input type="radio" name="voucherDiscount"
                                                                        value="discount1" data-text="${voucherText}"
                                                                        data-discount="${systemVoucher.value}"
                                                                        data-type="${voucher.discountType}"
                                                                        data-max="${voucher.maxAmount}"
                                                                        data-min-order-amount="${voucher.minOrderAmount}"
                                                                        data-voucherid="${voucher.maxAmount}">
                                                                </label>
                                                            </c:if>
                                                        </c:forEach>
                                                    </div>

                                                    <h6 class="fw-bold mt-4 mb-2">Mã Vận Chuyển</h6>
                                                    <div class="list-group">
                                                        <c:forEach var="systemVoucher" items="${systemVouchers}">
                                                            <c:if test="${systemVoucher.discountType == 'SHIPPING'}">
                                                                <label
                                                                    class="list-group-item d-flex justify-content-between align-items-center">
                                                                    <div>
                                                                        <span class="badge bg-primary">FREESHIP</span>
                                                                        <span class="ms-2">Giảm
                                                                            <fmt:formatNumber
                                                                                value="${systemVoucher.value / 1000}"
                                                                                type="number" />K
                                                                        </span><br>
                                                                        <small> Cho đơn từ
                                                                            <fmt:formatNumber
                                                                                value="${systemVoucher.minOrderAmount / 1000}"
                                                                                type="number" />K
                                                                            - HSD:
                                                                            <fmt:formatDate
                                                                                value="${systemVoucher.endAt}"
                                                                                pattern="dd/MM/yyyyy" />
                                                                        </small>
                                                                    </div>
                                                                    <input type="radio" name="voucherShip" value="ship1"
                                                                        data-text="Giảm <fmt:formatNumber value='${systemVoucher.value/1000}' type='number' />K"
                                                                        data-ship="${systemVoucher.value}"
                                                                        data-min-order-amount="${systemVoucher.minOrderAmount}">
                                                                </label>
                                                            </c:if>
                                                        </c:forEach>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <!-- Footer -->
                                        <div class="modal-footer">
                                            <button type="button" class="button-five" data-bs-dismiss="modal">Trở
                                                lại</button>
                                            <button type="button" class="button-four" id="confirmVoucher">OK</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--End Modal Voucher Cart -->
                        </c:otherwise>
                    </c:choose>
                </div>


                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <!-- JS riêng trang Cart -->
                <script src="<c:url value='/assets/js/customer/cart/cart.js?v=1.0.1'/>"></script>
            </body>

            </html>