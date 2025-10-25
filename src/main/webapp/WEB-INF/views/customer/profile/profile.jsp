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
                <link rel="stylesheet" href="./assets/css/customer/profile/information_account.css">
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
                                    <a class="nav-link text-dark" href="${ctx}/profile?action=notification">
                                        <i class="bi bi-bell me-2"></i> Thông báo
                                        <c:if test="${unreadNotificationCount > 0}">
                                            <span class="badge bg-danger ms-2">${unreadNotificationCount}</span>
                                        </c:if>
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
                                <!-- Thông báo
                                <div class="tab-pane fade" id="notify" role="tabpanel" aria-labelledby="notify-tab">
                                    <div class="text-center mt-5">
                                        <img src="./assets/images/mascot_fail.svg" alt="">
                                        <p class="text-muted mt-3">Chưa có thông báo</p>
                                    </div>
                                </div> -->

                                <!-- Hồ sơ -->
                                <div class="tab-pane fade show active" id="profile" role="tabpanel"
                                    aria-labelledby="profile-tab">
                                    <div class="profile">
                                        <div class="d-flex justify-content-between">
                                            <h5 class="mb-3 profile-title">Hồ Sơ Của Tôi</h5>
                                        </div>
                                        <p class="profile-des">Quản lý thông tin hồ sơ để bảo mật tài khoản</p>

                                        <div class="row">
                                            <div class="col-md-8">
                                                <form>
                                                    <div class="mb-3 row align-items-center">
                                                        <label class="col-sm-3 col-form-label">Tên:</label>
                                                        <div class="col-sm-9">
                                                            <input type="text" class="form-control"
                                                                value="${user.fullName}">
                                                        </div>
                                                    </div>

                                                    <c:if test="${user.authProvider != 'GOOGLE'}">
                                                        <div class="mb-3 row align-items-center">
                                                            <label class="col-sm-3 col-form-label">Email:</label>
                                                            <div class="col-sm-9 d-flex align-items-center">
                                                                <span class="me-2 text-truncate">${user.email}</span>
                                                                <div class="d-flex align-items-center gap-2">
                                                                    <c:if test="${emailChangeLocked}">
                                                                        <span class="text-muted"
                                                                            style="cursor: not-allowed;">Thay Đổi</span>
                                                                        <span class="badge bg-secondary"
                                                                            style="font-size: 0.7rem; padding: 0.25rem 0.5rem; min-width: 70px;"
                                                                            data-bs-toggle="tooltip" data-bs-html="true"
                                                                            title="<div class='text-center'>Thời gian còn lại:<br><strong id='tooltipCountdown'></strong></div>">
                                                                            <i class="bi bi-lock-fill me-1"></i>
                                                                            <span id="emailChangeDaysLeft"
                                                                                data-unlock-time="${emailChangeRemainingMs}"></span>
                                                                        </span>
                                                                    </c:if>
                                                                    <c:if test="${!emailChangeLocked}">
                                                                        <a href="#"
                                                                            class="text-primary change-password-link"
                                                                            data-action="changeEmail">
                                                                            Thay Đổi
                                                                        </a>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="mb-3 row align-items-center">
                                                            <label class="col-sm-3 col-form-label">Mật khẩu:</label>
                                                            <div class="col-sm-9 d-flex align-items-center">
                                                                <span class="me-2 text-truncate">**********</span>
                                                                <div class="d-flex align-items-center gap-2">
                                                                    <c:if test="${passwordChangeLocked}">
                                                                        <span class="text-muted"
                                                                            style="cursor: not-allowed;">Thay
                                                                            Đổi</span>
                                                                        <span class="badge bg-danger"
                                                                            style="font-size: 0.7rem; padding: 0.25rem 0.5rem; min-width: 70px;"
                                                                            data-bs-toggle="tooltip" data-bs-html="true"
                                                                            title="<div class='text-center'>Bị khóa do nhập sai nhiều lần<br>Thời gian còn lại:<br><strong id='pwdTooltipCountdown'></strong></div>">
                                                                            <i class="bi bi-lock-fill me-1"></i>
                                                                            <span id="passwordChangeLockLeft"
                                                                                data-unlock-time="${passwordChangeRemainingMs}"></span>
                                                                        </span>
                                                                    </c:if>
                                                                    <c:if test="${!passwordChangeLocked}">
                                                                        <a href="#"
                                                                            class="text-primary change-password-link"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#changePasswordModal">Thay
                                                                            Đổi</a>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:if>

                                                    <div class="row">
                                                        <div class="col-sm-9">
                                                            <button type="submit" class="button-four">Lưu</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>

                                            <div class="col-md-4 text-center profile-update__img">
                                                <div class="mb-3">
                                                    <c:choose>
                                                        <c:when
                                                            test="${user.avatarUrl != null && !user.avatarUrl.isEmpty()}">
                                                            <c:set var="avatarPreviewPath"
                                                                value="http://localhost:8080/assets/images/avatars/${user.avatarUrl}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="avatarPreviewPath"
                                                                value="http://localhost:8080/assets/images/common/avatar.png" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <img id="avatarPreview" src="${avatarPreviewPath}"
                                                        class="profile-img" alt="Avatar">
                                                    <div class="mt-3">
                                                        <input type="file" class="d-none" id="avatarInput"
                                                            accept="image/*">
                                                        <label for="avatarInput" class="button-five"
                                                            style="cursor:pointer;">Chọn Ảnh</label>
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

                <!-- Modal Đổi mật khẩu -->
                <div class="modal fade" id="changePasswordModal" tabindex="-1">
                    <div class="modal-dialog custom-modal-top">
                        <form id="formChangePassword">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Đổi mật khẩu</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body">
                                    <div id="changePasswordError" class="alert alert-danger d-none"></div>
                                    <div class="form-group mb-2">
                                        <label>Mật khẩu hiện tại</label>
                                        <div class="input-group">
                                            <input type="password" class="form-control" name="currentPassword" required>
                                            <span class="input-group-text" style="cursor:pointer;">
                                                <i class="bi bi-eye-slash toggle-password"></i>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="form-group mb-2">
                                        <label>Mật khẩu mới</label>
                                        <div class="input-group">
                                            <input type="password" class="form-control" name="newPassword" required>
                                            <span class="input-group-text" style="cursor:pointer;">
                                                <i class="bi bi-eye-slash toggle-password"></i>
                                            </span>
                                        </div>
                                    </div>
                                    <div class="form-group mb-2">
                                        <label>Xác nhận mật khẩu mới</label>
                                        <div class="input-group">
                                            <input type="password" class="form-control" name="confirmNewPassword"
                                                required>
                                            <span class="input-group-text" style="cursor:pointer;">
                                                <i class="bi bi-eye-slash toggle-password"></i>
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Modal 1: Xác thực email cũ -->
                <div class="modal fade" id="verifyOldEmailModal" tabindex="-1">
                    <div class="modal-dialog custom-modal-top">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Xác thực email hiện tại</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <p class="text-center mb-3">
                                    Chúng tôi đã gửi mã OTP đến:<br>
                                    <strong id="maskedOldEmail"></strong>
                                </p>
                                <input type="hidden" id="oldEmailHidden" />
                                <div class="form-group mb-2">
                                    <label for="oldEmailOtp" class="form-label">Mã OTP</label>
                                    <label class="form-label float-end" id="oldEmailOtpTimer"></label>
                                    <div class="input-group">
                                        <input id="oldEmailOtp" type="number" class="form-control"
                                            placeholder="Nhập mã OTP" maxlength="6">
                                        <button type="button" class="btn btn-outline-secondary" id="resendOldEmailOtp">
                                            Gửi lại
                                        </button>
                                    </div>
                                    <span class="form-message"></span>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="button" class="btn btn-primary" id="btnVerifyOldEmail" disabled>
                                    Tiếp tục
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Modal 2: Nhập email mới + Xác thực mật khẩu -->
                <div class="modal fade" id="enterNewEmailModal" tabindex="-1">
                    <div class="modal-dialog custom-modal-top">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Nhập email mới</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle me-2"></i>
                                    Email mới sẽ được sử dụng để đăng nhập và nhận thông báo.
                                </div>

                                <!-- Bước 1: Xác thực mật khẩu -->
                                <div id="passwordVerificationSection">
                                    <div class="form-group mb-3">
                                        <label for="currentPasswordVerify" class="form-label">
                                            Mật khẩu hiện tại <span class="text-danger">*</span>
                                        </label>
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="position-relative flex-grow-1">
                                                <input id="currentPasswordVerify" type="password"
                                                    class="form-control pe-5"
                                                    placeholder="Nhập mật khẩu hiện tại để xác nhận"
                                                    autocomplete="current-password" />
                                                <i class="bi bi-eye-slash toggle-password position-absolute top-50 end-0 translate-middle-y me-3"
                                                    style="cursor: pointer;"></i>
                                            </div>
                                            <button type="button" class="btn btn-outline-primary"
                                                id="btnVerifyPassword">
                                                Kiểm tra
                                            </button>
                                        </div>
                                        <span class="form-message" id="passwordVerifyMessage"></span>
                                    </div>
                                </div>

                                <!-- Bước 2: Nhập email mới (ẩn cho đến khi mật khẩu đúng) -->
                                <div id="newEmailSection" style="display: none;">
                                    <div class="alert alert-success d-flex align-items-center mb-3">
                                        <i class="bi bi-check-circle-fill me-2"></i>
                                        <span>Mật khẩu đã được xác thực. Vui lòng nhập email mới.</span>
                                    </div>

                                    <div class="form-group mb-2">
                                        <label for="newEmail" class="form-label">
                                            Email mới <span class="text-danger">*</span>
                                        </label>
                                        <input id="newEmail" type="email" class="form-control"
                                            placeholder="Nhập email mới của bạn" autocomplete="email">
                                        <span class="form-message"></span>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="button" class="btn btn-primary" id="btnSubmitNewEmail" disabled>
                                    Tiếp tục
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Modal 3: Xác thực email mới -->
                <div class="modal fade" id="verifyNewEmailModal" tabindex="-1">
                    <div class="modal-dialog custom-modal-top">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">Xác thực email mới</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <p class="text-center mb-3">
                                    Chúng tôi đã gửi mã OTP đến:<br>
                                    <strong id="maskedNewEmail"></strong>
                                </p>

                                <div class="form-group mb-2">
                                    <label for="newEmailOtp" class="form-label">Mã OTP</label>
                                    <label class="form-label float-end" id="newEmailOtpTimer"></label>
                                    <div class="input-group">
                                        <input id="newEmailOtp" type="number" class="form-control"
                                            placeholder="Nhập mã OTP" maxlength="6">
                                        <button type="button" class="btn btn-outline-secondary" id="resendNewEmailOtp">
                                            Gửi lại
                                        </button>
                                    </div>
                                    <span class="form-message"></span>
                                </div>

                                <div class="alert alert-warning">
                                    <i class="bi bi-exclamation-triangle me-2"></i>
                                    Sau khi xác nhận, bạn có thể đổi email lại sau <strong>7 ngày</strong>.
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="button" class="btn btn-primary" id="btnConfirmChangeEmail" disabled>
                                    Xác nhận thay đổi
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Footer & scripts chung -->
                <jsp:include page="/WEB-INF/views/layouts/_footer.jsp" />
                <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp" />

                <script src="<c:url value='/assets/js/customer/profile/profile.js'/>?v=1.0.1"></script>
            </body>

            </html>