<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- Modal Register -->
<div
    class="modal fade authFormModal"
    id="registerModal"
    tabindex="-1"
    aria-labelledby="registerModalLabel"
    aria-hidden="true"
>
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    aria-label="Close"
                ></button>
            </div>
            <div class="modal-body">
                <div class="row auth-form">
                    <div class="col-12 col-lg-6 auth-form-left">
                        <a href="./home.html">
                            <img
                                class="auth-form-logo"
                                src="<c:url value='/assets/images/branding/logo-header.png'/>"
                                alt="Aurora"
                            />
                            <h4 class="auth-form-title text-center">
                                Chào mừng bạn đến Aurora
                            </h4>
                            <p class="auth-form-desc text-center">
                                Bước vào thế giới sách – bắt đầu từ một cú click
                            </p>
                        </a>
                    </div>
                    <div class="col-12 col-lg-6 auth-form-right">
                        <form class="auth-form-form" id="form-register">
                            <div class="form-group mb-2">
                                <label
                                    for="register-fullname"
                                    class="form-label"
                                    >Họ và tên</label
                                >
                                <input
                                    id="register-fullname"
                                    type="text"
                                    class="form-control"
                                    placeholder="Nhập tên đầy đủ của bạn"
                                />
                                <span class="form-message"></span>
                            </div>

                            <div class="form-group mb-2">
                                <label for="register-email" class="form-label"
                                    >Email</label
                                >
                                <input
                                    id="register-email"
                                    type="email"
                                    class="form-control"
                                    placeholder="Nhập email của bạn"
                                />
                                <span class="form-message"></span>
                            </div>

                            <div class="form-group mb-2">
                                <label
                                    for="register-password"
                                    class="form-label"
                                    >Mật khẩu</label
                                >
                                <div class="password">
                                    <input
                                        id="register-password"
                                        type="password"
                                        class="form-control"
                                        placeholder="Nhập mật khẩu của bạn"
                                    />
                                    <i
                                        class="bi bi-eye-slash toggle-password"
                                    ></i>
                                </div>
                                <span class="form-message"></span>
                            </div>

                            <div class="form-group mb-2">
                                <label
                                    for="register-password-confirmation"
                                    class="form-label"
                                    >Xác nhận mật khẩu</label
                                >
                                <div class="password">
                                    <input
                                        id="register-password-confirmation"
                                        type="password"
                                        class="form-control"
                                        placeholder="Xác nhận mật khẩu của bạn"
                                    />
                                    <i
                                        class="bi bi-eye-slash toggle-password"
                                    ></i>
                                </div>
                                <span class="form-message"></span>
                            </div>
                            <div class="form-group mb-2">
                                <label for="register-otp" class="form-label"
                                    >Mã OTP</label
                                >
                                <label
                                    for="register-time"
                                    class="form-label"
                                    id="otp-timer"
                                ></label>
                                <div class="input-group">
                                    <input
                                        id="register-otp"
                                        type="number"
                                        class="form-control"
                                        placeholder="Nhập mã OTP"
                                    />
                                    <button
                                        type="button"
                                        class="button-two"
                                        id="send-otp"
                                    >
                                        Gửi OTP
                                    </button>
                                </div>
                                <span class="form-message"></span>
                            </div>

                            <button class="button-three mt-3">Đăng ký</button>

                            <div class="text-center auth-form-have-account">
                                <span
                                    >Đã có tài khoản?
                                    <a href="#" data-open="login">
                                        Đăng nhập
                                    </a>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--End Modal Register -->

<!-- Modal Login -->
<div
    class="modal fade authFormModal"
    id="loginModal"
    tabindex="-1"
    aria-labelledby="loginModalLabel"
    aria-hidden="true"
>
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    aria-label="Close"
                ></button>
            </div>
            <div class="modal-body">
                <div class="row auth-form">
                    <div class="col-12 col-lg-6 auth-form-left">
                        <a href="./home.html">
                            <img
                                class="auth-form-logo"
                                src="<c:url value='/assets/images/branding/logo-header.png'/>"
                                alt="Aurora"
                            />
                            <h4 class="auth-form-title text-center">
                                Chào mừng bạn đến Aurora
                            </h4>
                            <p class="auth-form-desc text-center">
                                Bước vào thế giới sách – bắt đầu từ một cú click
                            </p>
                        </a>
                    </div>
                    <div class="col-12 col-lg-6 auth-form-right">
                        <form class="auth-form-form" id="form-login">
                            <div class="form-group mb-2">
                                <label for="login-email" class="form-label"
                                    >Email</label
                                >
                                <input
                                    id="login-email"
                                    type="email"
                                    class="form-control"
                                    placeholder="Nhập email của bạn"
                                />
                                <span class="form-message"></span>
                            </div>

                            <div class="form-group mb-2">
                                <label for="login-password" class="form-label"
                                    >Mật khẩu</label
                                >
                                <div class="password">
                                    <input
                                        id="login-password"
                                        type="password"
                                        class="form-control"
                                        placeholder="Nhập mật khẩu của bạn"
                                    />
                                    <i
                                        class="bi bi-eye-slash toggle-password"
                                    ></i>
                                </div>
                                <span class="form-message"></span>
                            </div>
                            <div class="row">
                                <div class="col-6">
                                    <div class="form-check">
                                        <input
                                            tabindex="-1"
                                            class="form-check-input"
                                            type="checkbox"
                                            id="rememberMe"
                                        />
                                        <label
                                            tabindex="-1"
                                            class="form-check-label"
                                            for="rememberMe"
                                        >
                                            Ghi nhớ đăng nhập
                                        </label>
                                    </div>
                                </div>
                                <div class="col-6 text-end">
                                    <a
                                        href="#"
                                        tabindex="-1"
                                        data-open="forget-password"
                                        >Quên mật khẩu?</a
                                    >
                                </div>
                            </div>
                            <button class="button-three mt-3">Đăng nhập</button>

                            <div class="text-center mb-3 divider">
                                Hoặc tiếp tục với
                            </div>
                            <div class="d-grid mb-3">
                                <button
                                    id="btn-login-google"
                                    type="button"
                                    class="button-two"
                                >
                                    <img
                                        src="https://www.svgrepo.com/show/355037/google.svg"
                                        alt="Google"
                                        width="20"
                                        class="me-2"
                                    />
                                    Google
                                </button>
                            </div>

                            <div class="text-center auth-form-have-account">
                                <span
                                    >Chưa có tài khoản?
                                    <a href="#" data-open="register">
                                        Đăng ký
                                    </a></span
                                >
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--End Modal Login -->

<!-- Modal Forget Password -->
<div
    class="modal fade authFormModal"
    id="forgetPasswordModal"
    tabindex="-1"
    aria-labelledby="forgetPasswordModalLabel"
    aria-hidden="true"
>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    aria-label="Close"
                ></button>
            </div>
            <div class="modal-body">
                <div class="row auth-form">
                    <div
                        class="col-12 col-lg-12 auth-form-right forget-password"
                    >
                        <h4 class="auth-form-title text-center">
                            Quên mật khẩu
                        </h4>
                        <p class="auth-form-desc text-center">
                            Vui lòng nhập email để tiếp tục
                        </p>
                        <form class="auth-form-form" id="form-forget-password">
                            <div class="form-group mb-2">
                                <label
                                    for="forget-password-email"
                                    class="form-label"
                                    >Email</label
                                >
                                <input
                                    id="forget-password-email"
                                    name="email"
                                    type="email"
                                    class="form-control"
                                    placeholder="Nhập email của bạn"
                                />
                                <span class="form-message"></span>
                            </div>

                            <button class="button-three mt-3">Tiếp tục</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--End Modal Forget Password -->

<!-- Modal Create Password -->
<div
    class="modal fade authFormModal"
    id="createPasswordModal"
    tabindex="-1"
    aria-labelledby="createPasswordModalLabel"
    aria-hidden="true"
>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    aria-label="Close"
                ></button>
            </div>
            <div class="modal-body">
                <div class="row auth-form">
                    <div
                        class="col-12 col-lg-12 auth-form-right forget-password"
                    >
                        <h4 class="auth-form-title text-center">
                            Tạo mật khẩu mới
                        </h4>
                        <p class="auth-form-desc text-center">
                            Mã xác nhận đã gửi về email *******fc28@mail.copm
                        </p>
                        <form class="auth-form-form" id="form-create-password">
                            <div class="form-group mb-2">
                                <label
                                    for="create-password-otp"
                                    class="form-label"
                                    >Mã OTP</label
                                >
                                <input
                                    id="create-password-otp"
                                    type="number"
                                    class="form-control"
                                    placeholder="Nhập mã OTP"
                                />
                                <span class="form-message"></span>
                            </div>
                            <div class="form-group mb-2">
                                <label
                                    for="create-password-password"
                                    class="form-label"
                                    >Mật khẩu</label
                                >
                                <div class="password">
                                    <input
                                        id="create-password-password"
                                        type="password"
                                        class="form-control"
                                        placeholder="Nhập mật khẩu của bạn"
                                    />
                                    <i
                                        class="bi bi-eye-slash toggle-password"
                                    ></i>
                                </div>
                                <span class="form-message"></span>
                            </div>
                            <div class="form-group mb-2">
                                <label
                                    for="create-password-password-confirmation"
                                    class="form-label"
                                    >Xác nhận mật khẩu</label
                                >
                                <div class="password">
                                    <input
                                        id="create-password-password-confirmation"
                                        type="password"
                                        class="form-control"
                                        placeholder="Xác nhận mật khẩu của bạn"
                                    />
                                    <i
                                        class="bi bi-eye-slash toggle-password"
                                    ></i>
                                </div>
                                <span class="form-message"></span>
                            </div>
                            <div class="text-center auth-form-have-account">
                                <span
                                    >Bạn chưa có OTP?
                                    <a href="#"> Gửi lại </a></span
                                >
                            </div>
                            <button class="button-three mt-3">Xong</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--End Modal Create Password -->
