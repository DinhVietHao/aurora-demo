<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <!-- Reuse head chung -->
        <jsp:include page="/WEB-INF/views/layouts/_head.jsp">
            <jsp:param name="title" value="Giỏ hàng - Aurora"/>
        </jsp:include>

        <!-- CSS riêng trang Cart -->
        <link rel="stylesheet" href="<c:url value='/assets/css/common/globals.css?v=1.0.1'/>">
        <link rel="stylesheet" href="<c:url value='/assets/css/cart/cart.css?v=1.0.1'/>">
    </head>
    <body>

        <!-- Header + các modal auth dùng chung -->
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp"/>

        <div class="container cart">
            <h4 class="cart-title">Giỏ hàng</h4>

            <div class="row">
                <!-- LEFT: danh sách sản phẩm -->
                <div class="col-md-9 cart-left">
                    <div class="row cart-header">
                        <div class="col-6">
                            <input class="form-check-input me-2 cursor-pointer cart-checkboxAll" type="checkbox"> Sản phẩm
                        </div>
                        <div class="col-1 text-center">Đơn giá</div>
                        <div class="col-2 text-center">Số lượng</div>
                        <div class="col-2 text-center">Thành tiền</div>
                        <div class="col-1 text-center">Xóa</div>
                    </div>
                    <c:choose>
                        <c:when test="${empty cartItems}">
                            <div class="alert alert-warning mt-3">Giỏ hàng của bạn đang trống.</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cartItem" items="${cartItems}">
                                <div class="row cart-body" id="cartItemId${cartItem.cartItemId}" data-cartitemid="${cartItem.cartItemId}" data-cartid="${cartItem.cartId}">
                                    <div class="col-3 d-flex align-items-center">
                                        <input class="form-check-input cursor-pointer cart-checkbox" type="checkbox" ${cartItem.isChecked ? "checked" : ""}>
                                        <a href="${ctx}/book?id=${cartItem.product.productId}" target="_blank">
                                            <img src="http://localhost:8080/aurora/assets/images/${cartItem.product.images[0].imageUrl}" class="img-fluid" alt="${cartItem.product.title}">
                                        </a>
                                    </div>

                                    <div class="col-3">
                                        <a href="${ctx}/book?id=${cartItem.product.productId}" target="_blank">
                                            <h6 class="cart-book-title"><c:out value="${cartItem.product.title}"/></h6>
                                        </a>
                                        <p class="author"><c:out value="${cartItem.product.bookDetail.author}"/></p>
                                    </div>

                                    <div class="col-1 text-center">
                                        <span class="price unit-price">
                                            <fmt:formatNumber value="${cartItem.unitPrice}" type="currency"/>
                                        </span><br>
                                        <c:if test="${cartItem.product.discount != null}">
                                            <span class="text-muted text-decoration-line-through">
                                                <fmt:formatNumber value="${cartItem.product.price}" type="currency"/>
                                            </span>
                                        </c:if>
                                    </div>

                                    <div class="col-2">
                                        <div class="text-center">
                                            <button class="btn btn-outline-secondary btn-sm minus"
                                                    data-cartitemid="${cartItem.cartItemId}"
                                                    data-cartid="${cartItem.cartId}"
                                            >
                                                -
                                            </button>
                                            <span class="mx-2 number"><c:out value="${cartItem.quantity}"/></span>
                                            <button class="btn btn-outline-secondary btn-sm plus">+</button>
                                        </div>
                                    </div>

                                    <div class="col-2 text-center price subtotal">
                                        <fmt:formatNumber value="${cartItem.subtotal}" type="currency"/>
                                    </div>

                                    <button type="button" class="col-1 button-delete text-center"
                                            data-bs-toggle="modal"
                                            data-bs-target="#deleteCartModal"
                                            data-cartitemid="${cartItem.cartItemId}"
                                            data-cartid="${cartItem.cartId}"
                                            >
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- RIGHT: địa chỉ + tổng tiền -->
                <div class="col-md-3 cart-right">
                    <div class="card-address">
                        <div class="row">
                            <div class="col"><strong>Giao tới</strong></div>
                            <div class="col text-end">
                                <a href="${ctx}/shipping-address" class="text-primary">Thay đổi</a>
                            </div>
                        </div>

                        <div class="row mt-2">
                            <div class="col">
                                <span><strong><c:out value="${shipName}"/></strong></span>&nbsp;
                                <span><c:out value="${shipPhone}"/></span>
                            </div>
                        </div>

                        <div class="row mt-2">
                            <div class="col">
                                <span class="badge bg-success"><c:out value="${shipTag}"/></span>
                                <span><c:out value="${shipAddress}"/></span>
                            </div>
                        </div>

                        <div class="row mt-3">
                            <div class="col">
                                <div class="alert alert-warning p-2 mb-0">
                                    <strong>Lưu ý:</strong> Sử dụng địa chỉ nhận hàng trước sáp nhập
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="cart-promotion">
                        <h6 class="cart-promotion-title">Aurora Khuyến Mãi</h6>

                        <div id="appliedVoucherDiscount"
                             class="d-flex justify-content-between align-items-center border p-2 rounded mb-2 d-none">
                            <span id="voucherTextDiscount"></span>
                            <button id="removeVoucherDiscount" class="btn btn-primary btn-sm">Bỏ chọn</button>
                        </div>

                        <div id="appliedVoucherShip"
                             class="d-flex justify-content-between align-items-center border p-2 rounded mb-2 d-none">
                            <span id="voucherTextShip"></span>
                            <button id="removeVoucherShip" class="btn btn-primary btn-sm">Bỏ chọn</button>
                        </div>

                        <a class="small text-primary cursor-pointer" data-bs-toggle="modal" data-bs-target="#voucherModal">
                            Chọn hoặc nhập mã khác
                        </a>
                    </div>

                    <div class="cart-pay">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tổng tiền hàng</span>
                            <span class="total-product-price">
                                <fmt:formatNumber value="${totalProductPrice}" type="currency"/>
                            </span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển</span>
                            <span class="shipping-fee">
                                <fmt:formatNumber value="${shippingFee}" type="currency"/>
                            </span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="cart-pay-success">Mã khuyến mãi từ Aurora</span>
                            <span class="cart-pay-success discount">-0đ</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="cart-pay-success">Giảm giá vận chuyển</span>
                            <span class="cart-pay-success ship-discount">-0đ</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="cart-pay-danger">Tổng tiền thanh toán</span>
                            <span class="cart-pay-danger total-payment">
                                <fmt:formatNumber value="${totalPayment}" type="currency"/>
                            </span>
                        </div>
                        <button class="button-three" id="cart-pay-button">
                            Mua Hàng (<c:out value="${cartSelectedCount}"/>)
                        </button>
                    </div>
                </div>
            </div>

            <!-- Modals tách riêng để có thể reuse -->
            <jsp:include page="/WEB-INF/views/cart/partials/_cart_delete_modal.jsp"/>
            <jsp:include page="/WEB-INF/views/cart/partials/_cart_voucher_modal.jsp"/>            
            <jsp:include page="/WEB-INF/views/cart/partials/_cart_empty_selection_modal.jsp"/>

            
        </div>

        <!-- Footer & scripts chung -->
        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp"/>
        <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp"/>

        <!-- JS riêng trang Cart -->
        <script src="<c:url value='/assets/js/cart.js'/>"></script>
    </body>
</html>