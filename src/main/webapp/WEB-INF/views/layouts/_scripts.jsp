<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Bootstrap (chỉ nạp 1 lần) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<!-- Handle loose and open of Login and registration -->
<script src="<c:url value='/assets/js/auth/auth_form.js'/>?v=1.0.1"></script>

<!-- Check Input Register  -->
<script src="<c:url value='/assets/js/common/validator.js'/>?v=1.0.1"></script>

<%-- Tạo redirect_uri động, đúng host/port/context hiện tại --%>
<c:set var="redirectUri"
       value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/auth/login" />

<%-- Tạo URL đăng nhập Google OAuth v2, đã encode tham số --%>
<c:url var="googleAuthUrl" value="https://accounts.google.com/o/oauth2/v2/auth">
    <c:param name="client_id" value="7987308149-ltdnariub7lp5vtbu8n0kg7q5fffg2dv.apps.googleusercontent.com"/>
    <c:param name="redirect_uri" value="${redirectUri}"/>
    <c:param name="response_type" value="code"/>
    <c:param name="scope" value="openid email profile"/>
    <c:param name="access_type" value="offline"/>   <%-- (tuỳ chọn) lấy refresh token --%>
    <c:param name="prompt" value="consent"/>        <%-- thay cho approval_prompt=force --%>
</c:url>

<script>
    document.getElementById('btn-login-google')
            ?.addEventListener('click', () => {
                window.location.href = '${googleAuthUrl}';
            });
</script>

<script>
    // Register
    if (document.querySelector('#form-register')) {
        Validator({
            form: '#form-register',
            formGroupSelector: '.form-group',
            errorSelector: '.form-message',
            rules: [
                Validator.isRequired('#register-fullname', 'Vui lòng nhập tên đầy đủ'),
                Validator.isRequired('#register-email', 'Vui lòng nhập email'),
                Validator.isEmail('#register-email', 'Email không hợp lệ'),
                Validator.isRequired('#register-password', 'Vui lòng nhập mật khẩu'),
                Validator.minLength('#register-password', 6),
                Validator.isRequired('#register-password-confirmation', 'Vui lòng nhập lại mật khẩu'),
                Validator.isConfirmed('#register-password-confirmation', function () {
                    return document.querySelector('#form-register #register-password').value;
                }, 'Mật khẩu nhập lại không chính xác')
            ]
        });
    }
</script>

<script>
    // Login
    if (document.querySelector('#form-login')) {
        Validator({
            form: '#form-login',
            formGroupSelector: '.form-group',
            errorSelector: '.form-message',
            rules: [
                Validator.isRequired('#login-email', 'Vui lòng nhập email'),
                Validator.isEmail('#login-email', 'Email không hợp lệ'),
                Validator.isRequired('#login-password', 'Vui lòng nhập mật khẩu')
            ]
        });
    }
</script>

<script>
    // Forget password
    if (document.querySelector('#form-forget-password')) {
        Validator({
            form: '#form-forget-password',
            formGroupSelector: '.form-group',
            errorSelector: '.form-message',
            rules: [
                Validator.isRequired('#forget-password-email', 'Vui lòng nhập email'),
                Validator.isEmail('#forget-password-email', 'Email không hợp lệ')
            ],
            onSubmit: function () {
                const forgetModal = bootstrap.Modal.getOrCreateInstance(
                        document.getElementById('forgetPasswordModal')
                        );
                const createModal = bootstrap.Modal.getOrCreateInstance(
                        document.getElementById('createPasswordModal')
                        );
                forgetModal.hide();
                createModal.show();
            }
        });
    }
</script>

<script>
    // Create password
    if (document.querySelector('#form-create-password')) {
        Validator({
            form: '#form-create-password',
            formGroupSelector: '.form-group',
            errorSelector: '.form-message',
            rules: [
                Validator.isRequired('#create-password-otp', 'Vui lòng nhập mã OTP'),
                Validator.isRequired('#create-password-password', 'Vui lòng nhập mật khẩu'),
                Validator.minLength('#create-password-password', 6),
                Validator.isRequired('#create-password-password-confirmation', 'Vui lòng nhập lại mật khẩu'),
                Validator.isConfirmed('#create-password-password-confirmation', function () {
                    return document.querySelector('#form-create-password #create-password-password').value;
                }, 'Mật khẩu nhập lại không chính xác')
            ]
        });
    }
</script>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const params = new URLSearchParams(window.location.search);
        if (params.get("login") === "1") {
            const el = document.getElementById("loginModal");
            if (el && window.bootstrap)
                (window.bootstrap.Modal.getOrCreateInstance(el)).show();
        }
    });
</script>

<!--Gửi mã OTP-->
<script src="<c:url value='/assets/js/auth/send_otp.js'/>?v=1.0.2" defer></script>
<!--Đăng ký-->
<script src="<c:url value='/assets/js/auth/register.js'/>?v=1.0.2" defer></script>
<!--Đăng nhập-->
<script src="<c:url value='/assets/js/auth/login.js'/>?v=1.0.2" defer></script>
<!--Quên mật khẩu-->
<script src="<c:url value='/assets/js/auth/forgot-password.js'/>?v=1.0.2" defer></script>


