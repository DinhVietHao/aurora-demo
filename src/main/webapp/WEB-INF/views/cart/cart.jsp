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
                <link rel="stylesheet" href="./assets/css/cart/cart.css?v=1.0.1">
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
                                                    data-cartitemid="${cartItem.cartItemId}">
                                                    <div class="col-2 d-flex align-items-center">
                                                        <input class="form-check-input cursor-pointer cart-checkbox"
                                                            type="checkbox" ${cartItem.isChecked ? "checked" : "" }>
                                                        <a href="${ctx}/book?id=${cartItem.product.productId}"
                                                            target="_blank">
                                                            <img src="${ctx}/assets/images/catalog/thumbnails/${cartItem.product.images[0].imageUrl}"
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

                                            <div class="row cart-body__footer" data-shop-id="shop1">
                                                <div class="col-auto">
                                                    <i class="bi bi-ticket-perforated fs-3"></i>
                                                </div>
                                                <div class="col">
                                                    <div class="shop-voucher-text">Xem tất cả Voucher của Shop</div>
                                                </div>
                                                <div class="col-auto">
                                                    <a class="cursor-pointer text-primary" data-bs-toggle="modal"
                                                        data-bs-target="#shopVoucherModal_shop1" data-shop-id="shop1">
                                                        Xem thêm voucher
                                                    </a>
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
                                            <span>Phí vận chuyển</span>
                                            <span class="shipping-fee">30.000đ</span>
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
                                        <button class="button-three" id="cart-pay-button">Mua Hàng (0)</button>
                                    </div>
                                </div>
                            </div>

                            <!-- Modals tách riêng để có thể reuse -->
                            <jsp:include page="/WEB-INF/views/cart/partials/_cart_delete_modal.jsp" />

                            <jsp:include page="/WEB-INF/views/cart/partials/_cart_empty_selection_modal.jsp" />
                            <jsp:include page="/WEB-INF/views/cart/partials/_cart_voucher_modal.jsp" />
                            <jsp:include page="/WEB-INF/views/cart/partials/_cart_shop_voucher_modal.jsp" />
                        </c:otherwise>
                    </c:choose>
                </div>


                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <!-- JS riêng trang Cart -->
                <script src="<c:url value='/assets/js/cart/cart.js?v=1.0.1'/>"></script>
            </body>

            </html>