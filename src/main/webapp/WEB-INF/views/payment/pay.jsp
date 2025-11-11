<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <jsp:include page="/WEB-INF/views/layouts/_head.jsp">
            <jsp:param name="title" value="Thanh toán - Aurora"/>
        </jsp:include>

        <link rel="stylesheet" href="<c:url value='/assets/css/globals.css?v=1.0.1'/>">
        <link rel="stylesheet" href="<c:url value='/assets/css/cart.css?v=1.0.1'/>">
    </head>
    <body>

        <jsp:include page="/WEB-INF/views/layouts/_header.jsp"/>

        <div class="container cart">
            <h4 class="cart-title">Thanh Toán</h4>

            <div class="row">
                <!-- LEFT: danh sách sản phẩm -->
                <div class="col-md-9 cart-left">
                    <div class="row cart-header">
                        <div class="col-6">Sản phẩm</div>
                        <div class="col-2 text-center">Đơn giá</div>
                        <div class="col-2 text-center">Số lượng</div>
                        <div class="col-2 text-center">Thành tiền</div>
                    </div>

                    <c:forEach var="it" items="${items}">
                        <div class="row cart-body">
                            <div class="col-6 d-flex align-items-center">
                                <a href="${ctx}/book/${it.productId}" target="_blank">
                                    <img src="${ctx}/assets/images/${it.image}" class="img-fluid" alt="${it.title}">
                                </a>
                                <div class="px-3">
                                    <a href="${ctx}/book/${it.productId}" target="_blank">
                                        <h6 class="cart-book-title"><c:out value="${it.title}"/></h6>
                                    </a>
                                    <p class="author"><c:out value="${it.author}"/></p>
                                </div>
                            </div>

                            <div class="col-2 text-center">
                                <span class="price unit-price"><fmt:formatNumber value="${it.unitPrice}" type="currency"/></span><br>
                                <c:if test="${it.originalPrice != null}">
                                    <span class="text-muted text-decoration-line-through">
                                        <fmt:formatNumber value="${it.originalPrice}" type="currency"/>
                                    </span>
                                </c:if>
                            </div>
                            <div class="col-2 text-center"><c:out value="${it.quantity}"/></div>
                            <div class="col-2 text-center price subtotal">
                                <fmt:formatNumber value="${it.unitPrice * it.quantity}" type="currency"/>
                            </div>
                        </div>
                    </c:forEach>

                    <!-- Hình thức thanh toán -->
                    <div class="row cart-footer mt-3">
                        <div class="col-12"><h6 class="cart-footer-title">Chọn hình thức thanh toán</h6></div>

                        <div class="row align-items-center mb-3">
                            <div class="col-1"><input type="radio" name="payment" id="cash" value="CASH"></div>
                            <div class="col-1"><img src="<c:url value='/assets/images/cash.png'/>" alt="Cash" width="32"></div>
                            <div class="col"><label for="cash" class="mb-0">Thanh toán tiền mặt</label></div>
                        </div>

                        <div class="row align-items-center">
                            <div class="col-1"><input type="radio" name="payment" id="vnpay" value="VNPAY"></div>
                            <div class="col-1"><img src="<c:url value='/assets/images/vnpay.png'/>" alt="VNPAY" width="32"></div>
                            <div class="col">
                                <label for="vnpay" class="mb-0">VNPAY <br><small class="text-muted">Quét Mã QR từ ứng dụng ngân hàng</small></label>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- RIGHT: địa chỉ & tổng tiền -->
                <div class="col-md-3 cart-right">
                    <div class="card-address">
                        <div class="row">
                            <div class="col"><strong>Giao tới</strong></div>
                            <div class="col text-end"><a href="${ctx}/shipping-address" class="text-primary">Thay đổi</a></div>
                        </div>
                        <div class="row mt-2"><div class="col">
                                <span><strong><c:out value="${shipName}"/></strong></span>&nbsp;<span><c:out value="${shipPhone}"/></span>
                            </div></div>
                        <div class="row mt-2"><div class="col">
                                <span class="badge bg-success"><c:out value="${shipTag}"/></span>
                                <span><c:out value="${shipAddress}"/></span>
                            </div></div>
                        <div class="row mt-3"><div class="col">
                                <div class="alert alert-warning p-2 mb-0"><strong>Lưu ý:</strong> Sử dụng địa chỉ nhận hàng trước sáp nhập</div>
                            </div></div>
                    </div>

                    <div class="cart-promotion">
                        <h6 class="cart-promotion-title">Aurora Khuyến Mãi</h6>
                        <div id="appliedVoucherDiscount" class="d-flex justify-content-between align-items-center border p-2 rounded mb-2 d-none">
                            <span id="voucherTextDiscount"></span>
                            <button id="removeVoucherDiscount" class="btn btn-primary btn-sm">Bỏ chọn</button>
                        </div>
                        <div id="appliedVoucherShip" class="d-flex justify-content-between align-items-center border p-2 rounded mb-2 d-none">
                            <span id="voucherTextShip"></span>
                            <button id="removeVoucherShip" class="btn btn-primary btn-sm">Bỏ chọn</button>
                        </div>
                        <a class="small text-primary cursor-pointer" data-bs-toggle="modal" data-bs-target="#voucherModal">Chọn hoặc nhập mã khác</a>
                    </div>

                    <div class="cart-pay">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tổng tiền hàng</span>
                            <span class="total-product-price"><fmt:formatNumber value="${totalProductPrice}" type="currency"/></span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển</span>
                            <span class="shipping-fee"><fmt:formatNumber value="${shippingFee}" type="currency"/></span>
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
                            <span class="cart-pay-danger total-payment"><fmt:formatNumber value="${totalPayment}" type="currency"/></span>
                        </div>
                        <button class="button-three" id="btnPay">Thanh Toán</button>
                    </div>
                </div>
            </div>

            <!-- Modals -->
            <jsp:include page="/WEB-INF/views/products/_cart_voucher_modal.jsp"/>
            <jsp:include page="/WEB-INF/views/checkout/_vnpay_modal.jsp"/>
            <jsp:include page="/WEB-INF/views/checkout/_payment_error_modal.jsp"/>
        </div>

        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp"/>
        <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp"/>

        <script src="<c:url value='/assets/js/cart.js'/>"></script>
        <script src="<c:url value='/assets/js/pay.js'/>"></script>
    </body>
</html>