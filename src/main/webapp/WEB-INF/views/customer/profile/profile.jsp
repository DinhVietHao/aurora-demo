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

                <!-- CSS riêng trang Profile -->
                <link rel="stylesheet" href="./assets/css/customer/profile/information_account.css?v=1.0.1">
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
                                    <a class="nav-link text-dark active" href="/profile">
                                        <i class="bi bi-person me-2"></i> Hồ sơ
                                    </a>
                                </li>
                                <li class="nav-item mb-2">
                                    <a class="nav-link text-dark " href="/address">
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
                                        <img src="./assets/images/mascot_fail.svg" alt="">
                                        <p class="text-muted mt-3">Chưa có thông báo</p>
                                    </div>
                                </div>


                                <!-- Hồ sơ -->
                                <div class="tab-pane fade show active" id="profile" role="tabpanel"
                                    aria-labelledby="profile-tab">
                                    <div class="profile">
                                        <div class="d-flex justify-content-between">
                                            <h5 class="mb-3 profile-title">Hồ Sơ Của Tôi</h5>
                                            <button type="button" class="profile-remove">Gửi yêu cầu xóa tài khoản</button>
                                        </div>
                                        <p class="profile-des">Quản lý thông tin hồ sơ để bảo mật tài khoản</p>

                                        <div class="row">
                                            <div class="col-md-8">
                                                <form>
                                                    <div class="mb-3 row align-items-center">
                                                        <label class="col-sm-3 col-form-label">Tên:</label>
                                                        <div class="col-sm-9">
                                                            <input type="text" class="form-control" value="${user.fullName}">
                                                        </div>
                                                    </div>
                                                    
                                                    <div class="mb-3 row align-items-center">
                                                        <label class="col-sm-3 col-form-label">Email:</label>
                                                        <div class="col-sm-9 d-flex align-items-center">
                                                            <span
                                                                class="me-2 text-truncate">${user.email}</span>
                                                            <a href="#" class="btn btn-link btn-sm p-0">Thay Đổi</a>
                                                        </div>
                                                    </div>

                                                    <div class="mb-3 row align-items-center">
                                                        <label class="col-sm-3 col-form-label">Mật khẩu:</label>
                                                        <div class="col-sm-9 d-flex align-items-center">
                                                            <span
                                                                class="me-2 text-truncate">**********</span>
                                                            <a href="#" class="btn btn-link btn-sm p-0">Thay Đổi</a>
                                                        </div>
                                                    </div>

                                                    <div class="row">
                                                        <div class="col-sm-9">
                                                            <button type="submit" class="button-four">Lưu</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>


                                            <div class="col-md-4 text-center profile-update__img">
                                                <div class="mb-3">
                                                    <img src="./assets/images/common/avatar.png" name="avatarCustomer" class="profile-img"
                                                        alt="Avatar">
                                                    <div class="mt-3">
                                                        <input type="file" class="d-none" id="avatarInput">
                                                        <label for="avatarInput" class="button-five">Chọn
                                                            Ảnh</label>
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