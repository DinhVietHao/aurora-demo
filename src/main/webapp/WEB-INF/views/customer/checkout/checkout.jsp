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
                <link rel="stylesheet" href="./assets/css/customer/cart/cart.css">
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
                                        <div class="col-6">Sản phẩm</div>
                                        <div class="col-2 text-center">Đơn giá</div>
                                        <div class="col-2 text-center">Số lượng</div>
                                        <div class="col-2 text-center">Thành tiền</div>
                                    </div>


                                    <c:forEach var="shopCart" items="${shopCarts}">
                                        <div class="row cart-body" data-shop-id="${shopCart.shop.shopId}">
                                            <div class="col-12 cart-body__header">
                                                <span><strong><i
                                                            class="bi bi-shop me-2"></i>${shopCart.shop.name}</strong>
                                                </span>
                                            </div>

                                            <c:forEach var="cartItem" items="${shopCart.items}">
                                                <div class="row cart-body__item" id="cartItemId${cartItem.cartItemId}"
                                                    data-cartitemid="${cartItem.cartItemId}"
                                                    data-userid="${cartItem.userId}">
                                                    <div class="col-2 d-flex align-items-center">
                                                        <input class="form-check-input cursor-pointer cart-checkbox"
                                                            type="checkbox" ${cartItem.isChecked ? "checked" : "" }
                                                            hidden>
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

                                                    <div class="col-2 text-center">
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
                                                            <span class="mx-2 number">
                                                                <c:out value="${cartItem.quantity}" />
                                                            </span>
                                                        </div>
                                                    </div>

                                                    <div class="col-2 text-center price subtotal">
                                                        <fmt:formatNumber value="${cartItem.subtotal}"
                                                            type="currency" />
                                                    </div>
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
                                                aria-hidden="true" data-shop-id="${shopCart.shop.shopId}">
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
                                                                    <input type="text"
                                                                        class="form-control voucherShopInput"
                                                                        placeholder="Nhập mã giảm giá">
                                                                </div>
                                                                <div class="col-4">
                                                                    <button
                                                                        class="button-four w-100 applyVoucherShop">Áp
                                                                        dụng</button>
                                                                </div>
                                                                <span class="text-danger small voucherShopMessage">
                                                                </span>
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
                                                                                            pattern="dd/MM/yyyy" />
                                                                                    </small>
                                                                                </div>
                                                                                <input class="voucher-input-shop"
                                                                                    type="radio"
                                                                                    name="voucherShopDiscount_${shopCart.shop.shopId}"
                                                                                    value="${voucher.code}"
                                                                                    data-value="${voucher.code}"
                                                                                    data-text="Giảm 8% tối đa 30K"
                                                                                    data-discount="${voucher.value}"
                                                                                    data-type="${voucher.discountType}"
                                                                                    data-max="${voucher.maxAmount}"
                                                                                    data-min-order-amount="${voucher.minOrderAmount}"
                                                                                    data-shop-id="${shopCart.shop.shopId}">
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
                                    <div class="row cart-footer">
                                        <div class="col-12">
                                            <h6 class="cart-footer-title">Chọn hình thức thanh toán</h6>
                                        </div>

                                        <div class="row align-items-center mb-3">
                                            <div class="col-1">
                                                <input type="radio" name="payment" id="cash">
                                            </div>
                                            <div class="col-1">
                                                <img src="./assets/images/payment/cash.png" alt="Cash" width="32">
                                            </div>
                                            <div class="col">
                                                <label for="cash" class="mb-0">Thanh toán tiền mặt</label>
                                            </div>
                                        </div>

                                        <div class="row align-items-center">
                                            <div class="col-1">
                                                <input type="radio" name="payment" id="vnpay">
                                            </div>
                                            <div class="col-1">
                                                <img src="./assets/images/payment/vnpay.png" alt="VNPAY" width="32">
                                            </div>
                                            <div class="col">
                                                <label for="vnpay" class="mb-0">
                                                    VNPAY <br>
                                                    <small class="text-muted">Quét Mã QR từ ứng dụng ngân hàng</small>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- RIGHT: địa chỉ + tổng tiền -->
                                <div class="col-md-3 cart-right">
                                    <c:if test="${not empty addresses}">
                                        <div class="card-address">
                                            <div class="row">
                                                <div class="col">
                                                    <strong>Giao tới</strong>
                                                </div>
                                                <div class="col text-end">
                                                    <a data-bs-toggle="modal" data-bs-target="#addressModal"
                                                        class="text-primary cursor-pointer ">Thay đổi</a>
                                                </div>
                                            </div>
                                            <div class=" row mt-2">
                                                <div class="col">
                                                    <span><strong>${address.recipientName}</strong></span> &nbsp;
                                                    <span>${address.phone}</span>
                                                </div>
                                            </div>
                                            <div class="row mt-2">
                                                <div class="col">
                                                    <span class="badge bg-success">Nhà</span>
                                                    <span> ${address.description},
                                                        ${address.ward}, ${address.city}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
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
                                            <span class="badge bg-primary">FREESHIP</span>
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
                                            <span>Phí vận chuyển</span>
                                            <span class="shipping-fee">
                                                <fmt:formatNumber value="${shippingFee}" /> đ
                                            </span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="cart-pay-success">Tổng cộng Voucher giảm giá</span>
                                            <span class="cart-pay-success discount">0đ</span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="cart-pay-success">Tổng tiền phí vận chuyển
                                            </span>
                                            <span class="cart-pay-success ship-discount">0đ</span>
                                        </div>
                                        <hr>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="cart-pay-danger">Tổng tiền thanh toán</span>
                                            <span class="cart-pay-danger total-payment">142.000đ</span>
                                        </div>
                                        <button class="button-three" id="btnPay">Đặt hàng</button>
                                    </div>
                                </div>
                            </div>
                            <!-- Modals tách riêng để có thể reuse -->
                            <jsp:include page="/WEB-INF/views/customer/cart/partials/_cart_delete_modal.jsp" />
                            <jsp:include page="/WEB-INF/views/customer/cart/partials/_cart_empty_selection_modal.jsp" />
                            <!-- Modal Voucher System  -->
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
                                                        placeholder="Nhập mã giảm giá" id="voucherSystemInput">
                                                </div>
                                                <div class="col-4">
                                                    <button class="button-four w-100" id="applySystemVoucher">Áp
                                                        dụng</button>
                                                </div>
                                                <span class="text-danger small" id="voucherSystemMessage">
                                                </span>
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
                                                                                pattern="dd/MM/yyyy" />
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
                                                                        value="${systemVoucher.code}"
                                                                        data-value="${systemVoucher.code}"
                                                                        data-text="${voucherText}"
                                                                        data-discount="${systemVoucher.value}"
                                                                        data-type="${systemVoucher.discountType}"
                                                                        data-max="${systemVoucher.maxAmount}"
                                                                        data-min-order-amount="${systemVoucher.minOrderAmount}">
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
                                                                    <input type="radio" name="voucherShip"
                                                                        value="${systemVoucher.code}"
                                                                        data-value="${systemVoucher.code}"
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
                            <!--End Modal Voucher System  -->
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Modal Address -->
                <form action="/checkout" method="GET">
                    <div class="modal fade" id="addressModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-scrollable">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Địa Chỉ Của Tôi</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <c:choose>
                                        <c:when test="${empty addresses}">
                                            <div class="text-center mt-5">
                                                <img src="./assets/images/common/addressEmpty.png" alt="">
                                                <p class="text-muted mt-3">Bạn chưa có địa chỉ nào.</p>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="list-group">
                                                <c:forEach var="address" items="${addresses}">
                                                    <label class="list-group-item d-flex justify-content-between">
                                                        <div class="address-card ">
                                                            <div class="d-flex align-items-center mb-1">
                                                                <input type="radio" name="addressId"
                                                                    value="${address.addressId}" class="me-2"
                                                                    ${address.addressId==selectedAddressId ? 'checked'
                                                                    : '' } />
                                                                <h6 class="card-name mb-0">${address.recipientName}</h6>
                                                                <c:if test="${address.userAddress.defaultAddress}">
                                                                    <span
                                                                        class="card-default d-flex align-items-center mx-2">Mặc
                                                                        định</span>
                                                                </c:if>
                                                            </div>
                                                            <p class="mb-1">Địa chỉ: ${address.description},
                                                                ${address.ward}, ${address.city}
                                                            </p>
                                                            <p class="mb-1">Việt Nam</p>
                                                            <p class="mb-3">Điện thoại: ${address.phone}</p>
                                                            <button type="button"
                                                                class="button-four mb-3 update-address"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#updateAddressModal"
                                                                data-addressid="${address.addressId}">Cập nhật</button>
                                                        </div>
                                                    </label>
                                                </c:forEach>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="button-five" data-bs-dismiss="modal">Hủy</button>
                                    <button type="submit" class="button-four" id="select-address-btn">Xác nhận</button>
                                </div>
                            </div>
                        </div>

                    </div>
                </form>
                <!--End Modal Address -->

                <!--Modal Add Address -->
                <div class="modal fade" id="addAddressModal" tabindex="-1" aria-labelledby="addAddressModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog ">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5" id="addAddressModalLabel">Địa chỉ của tôi</h1>
                                <a type="button" class="btn-close" href="/cart" aria-label="Close"></a>
                            </div>
                            <div class="modal-body">
                                <form class="shipping-address" id="form-create-address" action="/address/add"
                                    method="post">
                                    <input type="hidden" name="from" value="checkout">
                                    <div class="row mb-3">
                                        <div class="col-md-6 form-group">
                                            <label for="fullName" class="form-label">Họ tên</label>
                                            <input type="text" class="form-control" id="fullName"
                                                placeholder="Nhập họ tên" name="fullName">
                                            <span class="form-message"></span>
                                        </div>
                                        <div class="col-md-6 form-group">
                                            <label for="phone" class="form-label">Điện thoại di động</label>
                                            <input type="text" class="form-control" id="phone"
                                                placeholder="Nhập số điện thoại" name="phone">
                                            <span class="form-message"></span>
                                        </div>
                                    </div>

                                    <div class="row mb-3">
                                        <div class="col-md-6 form-group">
                                            <label for="province" class="form-label">Tỉnh/Thành phố</label>
                                            <select class="form-select" id="addProvince" name="city">
                                                <option value="">Chọn Tỉnh/Thành phố</option>
                                            </select>
                                            <span class="form-message"></span>
                                        </div>
                                        <div class="col-md-6 form-group">
                                            <label for="ward" class="form-label">Phường/Xã</label>
                                            <select class="form-select" id="addWard" name="ward">
                                                <option value="">Chọn Phường/Xã</option>
                                            </select>
                                            <span class="form-message"></span>
                                        </div>
                                    </div>

                                    <div class="mb-3 form-group">
                                        <label for="address" class="form-label">Địa chỉ</label>
                                        <textarea class="form-control" id="address"
                                            placeholder="Ví dụ: 52, đường Trần Hưng Đạo" name="description"></textarea>
                                        <span class="form-message"></span>
                                    </div>
                                    <div class="form-check mb-3 <c:if test='${empty addresses}'>d-none</c:if>">
                                        <input class="form-check-input" type="checkbox" value="true" id="checkChecked"
                                            name="isDefault" <c:if test='${empty addresses}'>checked</c:if> />
                                        <label class="form-check-label" for="checkChecked">
                                            Đặt làm địa chỉ mặc định
                                        </label>
                                    </div>

                                    <div class="modal-footer">
                                        <a type="reset" class="button-five" href="/cart">Trở
                                            lại</a>
                                        <button type="submit" class="button-four">Hoàn thành</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <!--End Modal Add Address -->

                <!--Modal Update Address -->
                <div class="modal fade  " id="updateAddressModal" tabindex="-1"
                    aria-labelledby="updateAddressModalLabel" aria-hidden="true">
                    <div class="modal-dialog ">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h1 class="modal-title fs-5" id="updateAddressModalLabel">Địa chỉ của tôi</h1>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form class="shipping-address" id="form-update-address" method="POST"
                                    action="/address/update">
                                    <input type="hidden" name="addressId" value="" />
                                    <input type="hidden" name="from" value="checkout">
                                    <div class="row mb-3">
                                        <div class="col-md-6 form-group">
                                            <label for="fullName" class="form-label">Họ tên</label>
                                            <input type="text" class="form-control update-fullname" id="updateFullname"
                                                placeholder="Nhập họ tên" name="fullName" value="">
                                            <span class="form-message"></span>
                                        </div>
                                        <div class="col-md-6 form-group">
                                            <label for="phone" class="form-label">Điện thoại di động</label>
                                            <input type="text" class="form-control update-phone" id="updatePhone"
                                                placeholder="Nhập số điện thoại" name="phone" value="">
                                            <span class="form-message"></span>
                                        </div>
                                    </div>

                                    <div class="row mb-3">
                                        <div class="col-md-6 form-group">
                                            <label for="province" class="form-label">Tỉnh/Thành phố</label>
                                            <select class="form-select" name="city" id="updateProvince">
                                                <option value="" class="update-city"></option>
                                            </select>
                                            <span class="form-message"></span>
                                        </div>
                                        <div class="col-md-6 form-group">
                                            <label for="ward" class="form-label">Phường/Xã</label>
                                            <select class="form-select " name="ward" id="updateWard">
                                                <option value="" class="update-ward"></option>
                                            </select>
                                            <span class="form-message"></span>
                                        </div>
                                    </div>

                                    <div class="mb-3 form-group">
                                        <label for="address" class="form-label">Địa chỉ</label>
                                        <textarea class="form-control update-description" id="updateAddress"
                                            placeholder="Ví dụ: 52, đường Trần Hưng Đạo" name="description"
                                            value=""></textarea>
                                        <span class="form-message"></span>
                                    </div>
                                    <div class="form-check mb-3">
                                        <input class="form-check-input update-default" type="checkbox" value=""
                                            id="checkChecked" name="isDefault">
                                        <label class="form-check-label" for="checkChecked">
                                            Đặt làm địa chỉ mặc định
                                        </label>
                                    </div>

                                    <div class="modal-footer">
                                        <button type="reset" class="button-five" data-bs-dismiss="modal">Trở
                                            lại</button>
                                        <button class="button-four form-submit">Hoàn thành</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <!--End Modal Update Address -->

                <!-- Modal Error Payment -->
                <div class="modal fade" id="paymentErrorModal" tabindex="-1" aria-labelledby="paymentErrorLabel"
                    aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content text-center p-3">
                            <div class="modal-body">
                                <p class="mb-3">Vui lòng chọn hình thức thanh toán</p>
                                <button type="button" class="button-four" data-bs-dismiss="modal">Đóng</button>
                            </div>
                        </div>
                    </div>
                </div>
                <!--End Modal Error Payment -->

                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <!-- JS riêng trang Cart -->
                <script src="<c:url value='/assets/js/customer/checkout/checkout.js'/>"></script>

                <!-- Link javascript of Shipping Address -->
                <script src="./assets/js/customer/address/address.js?v=1.0.1"></script>


                <c:if test="${!isAddress}">
                    <script>
                        const addAddressModalEl = document.getElementById("addAddressModal");
                        if (addAddressModalEl) {
                            const modal = new bootstrap.Modal(addAddressModalEl);
                            modal.show();
                        }

                        Validator({
                            form: '#form-create-address',
                            formGroupSelector: '.form-group',
                            errorSelector: '.form-message',
                            rules: [
                                Validator.isRequired('#fullName', 'Vui lòng nhập họ tên'),
                                Validator.isRequired('#phone', 'Vui lòng nhập số điện thoại'),
                                Validator.isRequired('#addProvince', 'Vui lòng chọn Tỉnh/Thành phố'),
                                Validator.isRequired('#addWard', 'Vui lòng chọn Phường/Xã'),
                                Validator.isRequired('#address', 'Vui lòng nhập đại chỉ'),
                            ],
                        })
                        const addProvinceSelect = document.getElementById("addProvince");
                        const addWardSelect = document.getElementById("addWard");

                        initProvinceWard(addProvinceSelect, addWardSelect);


                        Validator({
                            form: '#form-update-address',
                            formGroupSelector: '.form-group',
                            errorSelector: '.form-message',
                            rules: [
                                Validator.isRequired('#updateFullname', 'Vui lòng nhập tên đầy đủ'),
                                Validator.isRequired('#updatePhone', 'Vui lòng nhập số điện thoại'),
                                Validator.isRequired('#updateProvince', 'Vui lòng chọn Tỉnh/Thành phố'),
                                Validator.isRequired('#updateWard', 'Vui lòng chọn Phường/Xã'),
                                Validator.isRequired('#updateAddress', 'Vui lòng nhập đại chỉ')
                            ]
                        })

                    </script>
                </c:if>
            </body>

            </html>