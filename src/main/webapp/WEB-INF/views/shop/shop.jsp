<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
                <c:set var="pageTitle" value="Aurora" />
                <c:set var="ctx" value="${pageContext.request.contextPath}" />

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Quản lý Shop - Aurora Bookstore</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                        rel="stylesheet" />
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
                    <link rel="stylesheet"
                        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/common/globals.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/catalog/home.css" />
                    <link rel="stylesheet" href="${ctx}/assets/css/admin/adminPage.css?v=1.0.1" />
                </head>

                <body class="sb-nav-fixed">
                    <jsp:include page="/WEB-INF/views/layouts/_header.jsp" />
                    <div id="layoutSidenav">
                        <jsp:include page="/WEB-INF/views/layouts/_sidebarShop.jsp" />

                        <div id="layoutSidenav_content">
                            <main>
                                <div class="container-fluid px-4">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h1 class="mt-4 shop-management-title">Quản lý Shop</h1>
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item">
                                                    <a href="/home">Trang chủ</a>
                                                </li>
                                                <li class="breadcrumb-item">
                                                    <a href="adminDashboard.html">Dashboard</a>
                                                </li>
                                                <li class="breadcrumb-item active" aria-current="page">
                                                    Quản lý shop
                                                </li>
                                            </ol>
                                        </nav>
                                    </div>

                                    <div class="row mt-4">
                                        <div class="col-xl-8">
                                            <div class="card mb-4">
                                                <div class="card-header">
                                                    <i class="bi bi-info-circle me-1"></i>
                                                    Thông tin Shop
                                                </div>
                                                <div class="card-body">
                                                    <form id="shopInfoForm">
                                                        <div class="row mb-3">
                                                            <div class="col-md-6">
                                                                <label for="shopName" class="form-label">Tên Shop <span
                                                                        class="text-danger">*</span></label>
                                                                <input type="text" class="form-control" id="shopName"
                                                                    value="${shop.name}" required />
                                                            </div>
                                                            <div class="col-md-6">
                                                                <label for="shopPhone" class="form-label">Số điện thoại
                                                                    <span class="text-danger">*</span></label>
                                                                <input type="tel" class="form-control" id="shopPhone"
                                                                    value="${shop.pickupAddress.phone}" required />
                                                            </div>
                                                        </div>
                                                        <div class="row mb-3">
                                                            <div class="col-md-6">
                                                                <label for="shopEmail" class="form-label">Email Shop
                                                                    <span class="text-danger">*</span></label>
                                                                <input type="email" class="form-control" id="shopEmail"
                                                                    value="${shop.invoiceEmail}" required />
                                                            </div>
                                                        </div>

                                                        <div class="mb-3">
                                                            <label for="shopDescription" class="form-label">Mô tả
                                                                Shop</label>
                                                            <textarea class="form-control" id="shopDescription"
                                                                rows="3">${shop.description}</textarea>
                                                        </div>

                                                        <div class="row mb-3">
                                                            <div class="col-md-4">
                                                                <label for="provinceSelect">Tỉnh/Thành phố</label>
                                                                <select id="provinceSelect" class="form-control">
                                                                    <option value="">-- Chọn Tỉnh/Thành phố --</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <label for="districtSelect">Quận/Huyện</label>
                                                                <select id="districtSelect" class="form-control">
                                                                    <option value="">-- Chọn Quận/Huyện --</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-md-4">
                                                                <label for="wardSelect">Phường/Xã</label>
                                                                <select id="wardSelect" class="form-control">
                                                                    <option value="">-- Chọn Phường/Xã --</option>
                                                                </select>
                                                            </div>
                                                        </div>


                                                        <div class="mb-3">
                                                            <label for="shopAddress" class="form-label">Địa chỉ chi
                                                                tiết</label>
                                                            <input type="text" class="form-control" id="shopAddress"
                                                                value="${shop.pickupAddress.line}, ${shop.pickupAddress.ward}" />
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
                                                    Logo Shop
                                                </div>
                                                <div class="card-body text-center">
                                                    <div class="shop-logo-container mb-3">
                                                        <img src="${ctx}/assets/images/catalog/products/product-1.png"
                                                            alt="Shop Logo" name="shopLogo" class="shop-logo"
                                                            id="shopLogoPreview" />
                                                    </div>
                                                    <div class="mb-3">
                                                        <input type="file" class="form-control" id="shopLogoInput"
                                                            accept="image/*" />
                                                        <div class="form-text">
                                                            Kích thước tối đa: 2MB. Định dạng: JPG, PNG. WEBP
                                                        </div>
                                                    </div>
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



                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                    <script src="${ctx}/assets/js/shop/scripts.js"></script>
                    <script src="${ctx}/assets/js/shop/shopInfo.js"></script>
                </body>

                </html>