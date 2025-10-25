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
                <link rel="stylesheet" href="${ctx}/assets/css/customer/order/order.css?v=1.0.1">
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
                                        <div class="tab-content order-body">
                                            <div class="order-detail">
                                                <c:forEach var="orderItem" items="${orderItems}">
                                                    <div class="order-detail__item row align-items-center mb-3">
                                                        <div class="order-detail__image col-md-2 col-4">
                                                            <img src="${ctx}/assets/images/catalog/products/${orderItem.imageUrl}"
                                                                alt="${orderItem.productName}"
                                                                class="order-detail__img">
                                                        </div>

                                                        <div class="order-detail__info col-md-7 col-8">
                                                            <h6 class="order-detail__title">
                                                                ${orderItem.productName}
                                                            </h6>
                                                            <p class="order-detail__quantity text-muted mb-0">
                                                                x${orderItem.quantity}</p>
                                                        </div>

                                                        <div
                                                            class="order-detail__price col-md-3 text-md-end mt-3 mt-md-0">
                                                            <span
                                                                class="order-detail__old-price text-decoration-line-through">
                                                                <fmt:formatNumber value="${orderItem.originalPrice}"
                                                                    type="currency" currencySymbol="₫" />
                                                            </span>
                                                            <span class="order-detail__new-price">
                                                                <fmt:formatNumber value="${orderItem.salePrice}"
                                                                    type="currency" currencySymbol="₫" />
                                                            </span><br>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                                <div class="order-detail__summary row justify-content-end">
                                                    <div class="col-md-6 col-lg-5">
                                                        <table class="order-detail__table table table-borderless mb-0">
                                                            <tbody>
                                                                <tr>
                                                                    <td class="order-detail__label">Tổng tiền hàng</td>
                                                                    <td class="order-detail__value">

                                                                        <fmt:formatNumber
                                                                            value="${orderItems[0].subtotal}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </td>

                                                                </tr>
                                                                <tr>
                                                                    <td class="order-detail__label">Phí vận chuyển</td>
                                                                    <td class="order-detail__value">
                                                                        <fmt:formatNumber
                                                                            value="${orderItems[0].shopShippingFee}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td class="order-detail__label">Giảm giá phí vận
                                                                        chuyển</td>
                                                                    <td
                                                                        class="order-detail__value order-detail__value--discount">
                                                                        -
                                                                        <fmt:formatNumber
                                                                            value="${orderItems[0].systemShippingDiscount}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td class="order-detail__label">Voucher từ Shop</td>
                                                                    <td
                                                                        class="order-detail__value order-detail__value--discount">
                                                                        -
                                                                        <fmt:formatNumber
                                                                            value="${orderItems[0].shopDiscount}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </td>
                                                                </tr>
                                                                <tr>
                                                                    <td class="order-detail__label">Voucher từ Aurora
                                                                    </td>
                                                                    <td
                                                                        class="order-detail__value order-detail__value--discount">
                                                                        -
                                                                        <fmt:formatNumber
                                                                            value="${orderItems[0].systemVoucherDiscount}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </td>
                                                                </tr>

                                                                <tr class="order-detail__total border-top">
                                                                    <td class="order-detail__label "><span
                                                                            class="total">Thành tiền</span>
                                                                    </td>
                                                                    <td
                                                                        class="order-detail__value order-detail__value--total">
                                                                        <fmt:formatNumber
                                                                            value="${orderItems[0].shopFinalAmount}"
                                                                            type="currency" currencySymbol="₫" />
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
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

            </body>

            </html>