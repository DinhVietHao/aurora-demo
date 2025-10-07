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
                <link rel="stylesheet" href="./assets/css/customer/address/address.css?v=1.0.1">
            </head>

            <body>

                <!-- Header + các modal auth dùng chung -->
                <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />

                <div class="container mt-3 information-account">
                    <div class="row ">
                        <div class="col-3 col-md-2 information-account__sidebar">

                            <div class="text-center mb-4">
                                <img src="./assets/images/common/avatar.png" alt="avatar"
                                    class="information-account__image">
                                <p class="mt-2 fw-bold mb-0">Leminhkha220</p>
                            </div>


                            <!-- sidebar profile -->
                            <ul class="nav mb-3 " id="profileTabs" role="tablist">
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" id="notify-tab" data-bs-toggle="tab" href="#notify"
                                        role="tab">
                                        <i class="bi bi-bell me-2"></i> Thông báo
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark " href="/profile">
                                        <i class="bi bi-person me-2"></i> Hồ sơ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark active" href="/address">
                                        <i class="bi bi-geo-alt me-2"></i> Địa Chỉ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark" href="/order">
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
                                        <img src="" alt="">
                                        <p class="text-muted mt-3">Chưa có thông báo</p>
                                    </div>
                                </div>
                                <!-- Địa chỉ -->
                                <div class="tab-pane fade show active" id="address" role="tabpanel"
                                    aria-labelledby="address-tab">
                                    <div class="address">

                                        <div class="d-flex justify-content-between address-header">
                                            <h5 class="user-address-title">Địa chỉ giao hàng</h5>
                                            <a href="#" class="button-four" data-bs-toggle="modal"
                                                data-bs-target="#addAddressModal">+
                                                Thêm địa
                                                chỉ mới</a>
                                        </div>
                                        <c:choose>
                                            <c:when test="${empty addresses}">
                                                <div class="text-center mt-5">
                                                    <img src="./assets/images/common/addressEmpty.png" alt="">
                                                    <p class="text-muted mt-3">Bạn chưa có địa chỉ nào.</p>
                                                </div>
                                            </c:when>
                                            <c:otherwise>

                                                <div class="row address-empty">
                                                    <c:forEach var="address" items="${addresses}">
                                                        <div class="col-md-12" id="addressId${address.addressId}">
                                                            <div class="address-card">
                                                                <div class="d-flex justify-content-between">
                                                                    <div class="d-flex">
                                                                        <h6 class="card-name">${address.recipientName}
                                                                        </h6>
                                                                        <c:if
                                                                            test="${address.userAddress.defaultAddress}">
                                                                            <span
                                                                                class="card-default d-flex align-items-center mx-2">Mặc
                                                                                định</span>
                                                                        </c:if>
                                                                    </div>
                                                                    <div>
                                                                        <button class="button-five mx-1 update-address"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#updateAddressModal"
                                                                            data-addressid="${address.addressId}">Cập
                                                                            nhật</button>
                                                                        <c:if
                                                                            test="${!address.userAddress.defaultAddress}">
                                                                            <button class="button-six delete-address"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#deleteAddressModal"
                                                                                data-addressid="${address.addressId}">Xóa</button>
                                                                        </c:if>

                                                                    </div>
                                                                </div>
                                                                <p class="mb-1">Địa chỉ: ${address.description},
                                                                    ${address.ward}, ${address.city}
                                                                </p>
                                                                <p class="mb-1">Việt Nam</p>
                                                                <p class="mb-3">Điện thoại: ${address.phone}</p>
                                                                <c:if test="${!address.userAddress.defaultAddress}">
                                                                    <form action="/address/set-default" method="post">
                                                                        <input type="hidden" name="addressId"
                                                                            value="${address.addressId}">
                                                                        <button type="submit" class="button-four">Thiết
                                                                            lập mặc định</button>
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
                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <!--Modal Add Address -->
                <jsp:include page="/WEB-INF/views/customer/address/partials/_address_add_modal.jsp" />
                <!--End Modal Add Address -->

                <!--Modal Update Address -->
                <jsp:include page="/WEB-INF/views/customer/address/partials/_address_update_modal.jsp" />
                <!--End Modal Update Address -->

                <!-- Modal Delete Address -->
                <jsp:include page="/WEB-INF/views/customer/address/partials/_address_delete_modal.jsp" />
                <!--End Modal Delete Cart -->

                <!-- Link javascript of Shipping Address -->
                <script src="./assets/js/customer/address/address.js"></script>


                <!-- Link javascript of Validator -->
                <script src="./assets/js/common/validator.js"></script>

                <!-- Check Input create address user  -->
                <script>
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

                </script>
            </body>

            </html>