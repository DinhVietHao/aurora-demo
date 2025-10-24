<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <jsp:include page="/WEB-INF/views/layouts/_head.jsp" />
                    <link rel="stylesheet" href="${ctx}/assets/css/shop/shopInfo.css?v=1.0.1" />
                </head>

                <body class="sb-nav-fixed" data-page="shop-profile">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />
                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 shop-management-title">Thông tin cửa hàng</h1>
                                    </div>

                                    <div class="row mt-4">
                                        <div class="col-xl-8">
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <i class="bi bi-info-circle me-1"></i>
                                                    Thông tin
                                                </div>
                                                <div class="card-body">
                                                    <form id="shopInfoForm">
                                                        <div class="row mb-3">
                                                            <div class="col-md-6 form-group">
                                                                <label for="shopName" class="form-label">Tên
                                                                    <span class="text-danger">*</span></label>
                                                                <input type="text" class="form-control" id="shopName"
                                                                    value="${shop.name}" required />
                                                                <span class="form-message"></span>
                                                            </div>
                                                            <div class="col-md-6 form-group">
                                                                <label for="shopPhone" class="form-label">Số điện thoại
                                                                    <span class="text-danger">*</span></label>
                                                                <input type="tel" class="form-control" id="shopPhone"
                                                                    value="${shop.pickupAddress.phone}" required />
                                                                <span class="form-message"></span>
                                                            </div>
                                                        </div>
                                                        <div class="row mb-3">
                                                            <div class="col-md-6 form-group">
                                                                <label for="shopEmail" class="form-label">Email
                                                                    <span class="text-danger">*</span></label>
                                                                <input type="email" class="form-control" id="shopEmail"
                                                                    value="${shop.invoiceEmail}" required />
                                                                <span class="form-message"></span>
                                                            </div>
                                                        </div>

                                                        <div class="mb-3 form-group">
                                                            <label for="shopDescription" class="form-label">Mô tả
                                                                <span class="text-danger">*</span></label>
                                                            <textarea class="form-control" id="shopDescription"
                                                                rows="3">${shop.description}</textarea>
                                                            <span class="form-message"></span>
                                                        </div>

                                                        <div class="row mb-3">
                                                            <!-- Province Select -->
                                                            <div class="col-md-4 form-group">
                                                                <label for="updateProvince" class="form-label">
                                                                    Tỉnh/Thành phố <span class="text-danger">*</span>
                                                                </label>
                                                                <select class="form-select" id="updateProvince"
                                                                    name="city">
                                                                    <!-- <option value="">Chọn Tỉnh/Thành phố</option> -->
                                                                    <option value="${shop.pickupAddress.provinceId}">
                                                                        ${shop.pickupAddress.city}</option>
                                                                </select>
                                                                <span class="form-message"></span>
                                                            </div>

                                                            <!-- District Select -->
                                                            <div class="col-md-4 form-group">
                                                                <label for="updateDistrict" class="form-label">
                                                                    Quận/Huyện <span class="text-danger">*</span>
                                                                </label>
                                                                <select id="updateDistrict" class="form-select"
                                                                    name="district" disabled>
                                                                    <!-- <option value="">-- Chọn Quận/Huyện --</option> -->
                                                                    <option value="${shop.pickupAddress.districtId}">
                                                                        ${shop.pickupAddress.district}</option>
                                                                </select>
                                                                <span class="form-message"></span>
                                                            </div>

                                                            <!-- Ward Select -->
                                                            <div class="col-md-4 form-group">
                                                                <label for="updateWard" class="form-label">
                                                                    Phường/Xã <span class="text-danger">*</span>
                                                                </label>
                                                                <select class="form-select" id="updateWard" name="ward"
                                                                    disabled>
                                                                    <!-- <option value="">-- Chọn Phường/Xã --</option> -->
                                                                    <option value="${shop.pickupAddress.wardCode}">
                                                                        ${shop.pickupAddress.ward}</option>
                                                                </select>
                                                                <span class="form-message"></span>
                                                            </div>

                                                            <input type="hidden" id="updateProvinceNameInput"
                                                                name="cityName">
                                                            <input type="hidden" id="updateDistrictNameInput"
                                                                name="districtName">
                                                            <input type="hidden" id="updateWardNameInput"
                                                                name="wardName">
                                                            <input type="hidden" id="updateProvinceIdInput"
                                                                name="provinceId">
                                                            <input type="hidden" id="updateDistrictIdInput"
                                                                name="districtId">
                                                            <input type="hidden" id="updateWardCodeInput"
                                                                name="wardCode">
                                                        </div>

                                                        <div class="mb-3 form-group">
                                                            <label for="shopAddress" class="form-label">Địa chỉ chi
                                                                tiết <span class="text-danger">*</span></label>
                                                            <input type="text" class="form-control" id="shopAddress"
                                                                value="${shop.pickupAddress.description}" />
                                                            <span class="form-message"></span>
                                                        </div>

                                                        <div class="d-flex justify-content-end">
                                                            <button type="submit" class="btn btn-success">
                                                                <i class="bi bi-check-circle me-1"></i>
                                                                Lưu thay đổi
                                                            </button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-xl-4">
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <i class="bi bi-image me-1"></i>
                                                    Logo
                                                </div>

                                                <div class="card-body text-center">
                                                    <div class="shop-logo-container mb-3">
                                                        <c:choose>
                                                            <c:when test="${not empty shop.avatarUrl}">
                                                                <img src="${ctx}/assets/images/shops/${shop.avatarUrl}"
                                                                    alt="Shop Logo" class="shop-logo"
                                                                    id="shopLogoPreview"
                                                                    onerror="this.src='${ctx}/assets/images/shops/default-shop.png'" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${ctx}/assets/images/shops/default-shop.png"
                                                                    alt="Default Shop Logo" class="shop-logo"
                                                                    id="shopLogoPreview" />
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>

                                                    <div class="mb-3">
                                                        <div class="form-text mt-2">
                                                            <i class="bi bi-info-circle text-primary"></i>
                                                            Kích thước tối đa: 5MB<br>
                                                            Định dạng: JPG, PNG, WEBP<br>
                                                            Khuyến nghị: 500x500px (tỷ lệ 1:1)
                                                        </div>
                                                    </div>

                                                    <input type="file" id="shopLogoInput" accept="image/*"
                                                        style="display: none;" />

                                                    <button type="button" class="btn btn-outline-primary btn-sm"
                                                        id="uploadLogoBtn">
                                                        <i class="bi bi-upload me-1"></i>
                                                        Tải lên logo mới
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </main>
                            <jsp:include page="/WEB-INF/views/layouts/_footer.jsp?v=1.0.1" />
                        </div>
                    </div>

                    <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                    <script>
                        Validator({
                            form: '#shopInfoForm',
                            formGroupSelector: '.form-group',
                            errorSelector: '.form-message',
                            rules: [
                                Validator.isRequired('#shopName', 'Vui lòng nhập tên shop'),
                                Validator.isRequired('#shopPhone', 'Vui lòng nhập số điện thoại'),
                                Validator.isRequired('#shopEmail', 'Vui lòng nhập email'),
                                Validator.isEmail('#shopEmail', 'Email không hợp lệ'),
                                Validator.isRequired('#updateProvince', 'Vui lòng chọn Tỉnh/Thành phố'),
                                Validator.isRequired('#updateDistrict', 'Vui lòng chọn Quận/Huyện'),
                                Validator.isRequired('#updateWard', 'Vui lòng chọn Phường/Xã'),
                                Validator.isRequired('#shopAddress', 'Vui lòng nhập địa chỉ chi tiết')
                            ]
                        });

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

                        document.addEventListener("DOMContentLoaded", function () {
                            const uploadBtn = document.getElementById("uploadLogoBtn");
                            const fileInput = document.getElementById("shopLogoInput");

                            if (uploadBtn && fileInput) {
                                uploadBtn.addEventListener("click", function () {
                                    fileInput.click();
                                });
                            }

                            const shopAvatarUploader = new AvatarUploader({
                                inputId: "shopLogoInput",
                                previewId: "shopLogoPreview",
                                uploadUrl: "${ctx}/shop",
                                fileParamName: "shopLogo",
                                action: "uploadAvatar",
                                onSuccess: function (data) {
                                    toast({
                                        title: "Thành công!",
                                        message: data.message,
                                        type: "success",
                                        duration: 3000,
                                    });

                                    if (data.avatarUrl) {
                                        document.getElementById("shopLogoPreview").src = data.avatarUrl;
                                    }
                                },
                                onError: function (message) {
                                    toast({
                                        title: "Lỗi!",
                                        message: message,
                                        type: "error",
                                        duration: 3000,
                                    });
                                },
                            });
                        });
                    </script>
                </body>

                </html>