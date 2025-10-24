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
                <link rel="stylesheet" href="./assets/css/customer/profile/information_account.css">
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
                                                                        <h6 class="card-name">
                                                                            <strong>${address.recipientName}</strong>
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
                                                                <p class="mb-1"><strong>Địa chỉ:</strong>
                                                                    ${address.description},
                                                                    ${address.ward}, ${address.district},
                                                                    ${address.city}
                                                                </p>
                                                                <p class="mb-1">Việt Nam</p>
                                                                <p class="mb-3"><strong>Điện thoại:</strong>
                                                                    ${address.phone}</p>
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

                    const addProvince = document.getElementById("addProvince");
                    const addDistrict = document.getElementById("addDistrict");
                    const addWard = document.getElementById("addWard");

                    const addProvinceNameInput = document.getElementById("provinceNameInput");
                    const addDistrictNameInput = document.getElementById("districtNameInput");
                    const addWardNameInput = document.getElementById("wardNameInput");
                    const addProvinceIdInput = document.getElementById("provinceIdInput");
                    const addDistrictIdInput = document.getElementById("districtIdInput");
                    const addWardCodeInput = document.getElementById("wardCodeInput");

                    initAddressSelects(addProvince,
                        addDistrict,
                        addWard,
                        addProvinceNameInput,
                        addDistrictNameInput,
                        addWardNameInput,
                        addProvinceIdInput,
                        addDistrictIdInput,
                        addWardCodeInput
                    );
                </script>


                <script>
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
                    const provinceSelect = document.getElementById("updateProvince");
                    const districtSelect = document.getElementById("updateDistrict");
                    const wardSelect = document.getElementById("updateWard");

                    const provinceNameInput = document.getElementById("updateProvinceNameInput");
                    const districtNameInput = document.getElementById("updateDistrictNameInput");
                    const wardNameInput = document.getElementById("updateWardNameInput");
                    const provinceIdInput = document.getElementById("updateProvinceIdInput");
                    const districtIdInput = document.getElementById("updateDistrictIdInput");
                    const wardCodeInput = document.getElementById("updateWardCodeInput");
                    initAddressSelects(
                        provinceSelect,
                        districtSelect,
                        wardSelect,
                        provinceNameInput,
                        districtNameInput,
                        wardNameInput,
                        provinceIdInput,
                        districtIdInput,
                        wardCodeInput
                    );
                </script>
                <script src="./assets/js/customer/address/address.js?v=1.0.1"></script>

                <c:if test="${not empty sessionScope.toastMsg}">
                    <script>
                        toast({
                            title: "${sessionScope.toastType == 'success' ? 'Thành công' : 'Không thể cập nhật'}",
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