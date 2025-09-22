/* global bootstrap */
document.addEventListener("DOMContentLoaded", function () {
    // --- Element refs ---
    const btnLogin = document.querySelector("#form-login .button-three");
    const cbRemember = document.getElementById("rememberMe");
    const inputPass = document.getElementById("login-password");
    const loginModalEl = document.getElementById("loginModal");
    const inputEmail = document.getElementById("login-email");
    const formLogin = document.getElementById("form-login");

    /**
     * Lấy element hiển thị message đi kèm 1 input (theo cấu trúc .form-group / .form-message).
     * @param {HTMLElement} inputElement - ô input cần tìm message element kèm theo.
     * @returns {HTMLElement|null} - element hiển thị message hoặc null nếu không tìm thấy.
     */
    function getMessageElementOf(inputElement) {
        if (!inputElement) return null;
        const grp =
            inputElement.closest(".form-group") || inputElement.parentElement;
        return grp ? grp.querySelector(".form-message") : null;
    }

    /**
     * Hiển thị message cho 1 input, kèm màu theo trạng thái.
     * @param {HTMLElement} inputElement - ô input được gắn message.
     * @param {string} message - nội dung thông báo.
     * @param {"success"|"failure"|""} [status=""] - trạng thái để tô màu (xanh/đỏ) hoặc bỏ trống.
     */
    function showMessageForInput(inputElement, message, status = "") {
        const msgEl = getMessageElementOf(inputElement);
        if (!msgEl) return;
        msgEl.textContent = message || "";
        msgEl.style.color =
            status === "success" ? "green" : status === "failure" ? "red" : "";
    }

    /** Xóa tất cả message lỗi/thông báo trên form login. */
    function clearAllMessages() {
        [inputEmail, inputPass].forEach((i) => showMessageForInput(i, "", ""));
    }

    /**
     * Bật/tắt nút đăng nhập và (tuỳ chọn) đổi text của nút.
     * @param {boolean} disabled - true: disable button; false: enable.
     * @param {string} [textWhenDisabled] - nếu truyền string, sẽ set innerHTML khi disable.
     */
    function setBtnDisabled(disabled, textWhenDisabled) {
        btnLogin.disabled = disabled;
        btnLogin.style.opacity = disabled ? "0.7" : "1";
        btnLogin.style.cursor = disabled ? "not-allowed" : "";
        if (typeof textWhenDisabled === "string")
            btnLogin.innerHTML = textWhenDisabled;
    }

    /** Đưa UI modal về trạng thái ban đầu (xoá lỗi, xoá input, reset nút). */
    function resetLoginUI() {
        clearAllMessages();
        if (inputEmail) inputEmail.value = "";
        if (inputPass) inputPass.value = "";
        btnLogin.classList.remove("btn-success");
        btnLogin.innerHTML = "Đăng nhập";
        setBtnDisabled(false);
    }

    /**
     * Trim chuỗi, null-safe.
     * @param {string} v
     * @returns {string}
     */
    function trim(v) {
        return (v || "").trim();
    }

    // Khi gõ vào email/password -> xoá message lỗi & re-enable nút
    [inputEmail, inputPass].forEach((i) => {
        i &&
            i.addEventListener("input", () => {
                showMessageForInput(i, "", "");
                setBtnDisabled(false, "Đăng nhập");
            });
    });

    // Submit login khi bấm nút
    btnLogin.addEventListener("click", async function () {
        clearAllMessages();

        // Payload gửi lên server (khớp LoginServlet: email, password, rememberMe)
        const payload = {
            email: trim(inputEmail.value),
            password: trim(inputPass.value),
            rememberMe: !!(cbRemember && cbRemember.checked),
        };

        const oldText = btnLogin.innerHTML;
        setBtnDisabled(true, "Đang đăng nhập...");

        try {
            // Gọi API đăng nhập LOCAL (POST JSON)
            const res = await fetch("/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            // Backend trả JSON { ok: boolean, redirect?: string, message?: string }
            let data = null;
            try {
                data = await res.json();
            } catch (_) {
                // Trường hợp response không parse được JSON -> để data = null
            }

            if (data && data.ok) {
                // UI feedback khi đăng nhập thành công
                btnLogin.classList.add("btn-success");
                btnLogin.innerHTML = "✅ Đăng nhập thành công";

                // Đợi 1 chút cho người dùng thấy feedback, sau đó đóng modal & redirect
                setTimeout(() => {
                    resetLoginUI();

                    // Đóng modal nếu đang mở (Bootstrap 5)
                    if (loginModalEl && window.bootstrap) {
                        const inst =
                            bootstrap.Modal.getInstance(loginModalEl) ||
                            new bootstrap.Modal(loginModalEl);
                        inst.hide();
                    }
                    // Điều hướng: ưu tiên data.redirect; fallback /home
                    const to =
                        data.redirect && typeof data.redirect === "string"
                            ? data.redirect
                            : "/home";
                    window.location.href = to.startsWith("/") ? to : "/" + to;
                }, 1200);
            } else {
                // Sai email/mật khẩu hoặc lỗi khác từ backend
                const msg =
                    data && data.message
                        ? data.message
                        : "Đăng nhập thất bại. Vui lòng thử lại.";
                showMessageForInput(inputPass, msg, "failure");
                btnLogin.innerHTML = oldText;
            }
        } catch (err) {
            // Lỗi mạng / server không phản hồi
            showMessageForInput(
                inputPass,
                "Không thể kết nối máy chủ. Vui lòng thử lại.",
                "failure"
            );
            btnLogin.innerHTML = oldText;
        }
    });

    // Cho phép submit bằng Enter trong form (ngăn submit mặc định, tái sử dụng handler click)
    if (formLogin) {
        formLogin.addEventListener("submit", (e) => {
            e.preventDefault();
            btnLogin.click();
        });
    }

    // Khi modal đóng -> reset UI (xóa lỗi, dọn input, reset nút)
    if (loginModalEl) {
        loginModalEl.addEventListener("hidden.bs.modal", resetLoginUI);
    }
});
