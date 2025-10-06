/* global bootstrap */
document.addEventListener("DOMContentLoaded", function () {
    // Cache các input/element cần dùng trong form đăng ký
    const btnSubmit = document.querySelector("#form-register .button-three");
    const inputFull = document.getElementById("register-fullname");
    const inputEmail = document.getElementById("register-email");
    const inputPass = document.getElementById("register-password");
    const inputConf = document.getElementById("register-password-confirmation");
    const inputOtp = document.getElementById("register-otp");
    const timerLabel = document.getElementById("otp-timer");

    // Tự lấy modal elements (không phụ thuộc file khác)
    const registerModalEl = document.getElementById("registerModal");
    const loginModalEl = document.getElementById("loginModal");

    // Tìm phần tử hiển thị message (theo convention .form-group > .form-message)
    function getMessageElementOf(inputElement) {
        if (!inputElement) return null;
        const formGroup =
            inputElement.closest(".form-group") || inputElement.parentElement;
        return formGroup ? formGroup.querySelector(".form-message") : null;
    }

    // Hiển thị message cho 1 input và đổi màu theo trạng thái
    function showMessageForInput(inputElement, message, status = "") {
        const messageElement = getMessageElementOf(inputElement);
        if (!messageElement) return;
        messageElement.textContent = message || "";
        messageElement.style.color =
            status === "success" ? "green" : status === "failure" ? "red" : "";
    }

    // Xoá toàn bộ message lỗi/thành công trên các trường cơ bản
    function clearAllMessages() {
        [inputFull, inputEmail, inputPass, inputConf].forEach((input) =>
            showMessageForInput(input, "", "")
        );
    }

    // Helper: trim an toàn với null/undefined
    function trim(v) {
        return (v || "").trim();
    }

    // Kiểm tra OTP đã được verify thành công hay chưa (dựa trên message do script verify đặt)
    function isOtpValid() {
        const msgEl = getMessageElementOf(inputOtp);
        return msgEl && msgEl.textContent.trim() === "Mã OTP chính xác ✅";
    }

    // Enable/disable nút Đăng ký theo trạng thái OTP
    function updateRegisterButton() {
        if (isOtpValid()) {
            btnSubmit.disabled = false;
            btnSubmit.style.opacity = "1";
            btnSubmit.style.cursor = "";
        } else {
            btnSubmit.disabled = true;
            btnSubmit.style.opacity = "0.5";
            btnSubmit.style.cursor = "not-allowed";
        }
    }

    // Theo dõi thay đổi message của OTP để tự cập nhật trạng thái nút Đăng ký
    (function observeOtpMessage() {
        const otpMsgEl = getMessageElementOf(inputOtp);
        if (!otpMsgEl) return;
        const observer = new MutationObserver(updateRegisterButton);
        observer.observe(otpMsgEl, {
            childList: true,
            subtree: true,
            characterData: true,
        });
    })();

    // Khi người dùng gõ vào các trường → xoá message cũ để tránh gây nhiễu
    [inputFull, inputEmail, inputPass, inputConf, inputOtp].forEach((i) => {
        i && i.addEventListener("input", () => showMessageForInput(i, "", ""));
    });

    // Khởi tạo trạng thái ban đầu của nút Đăng ký (thường là disabled)
    updateRegisterButton();

    // Ánh xạ thông báo lỗi từ server về đúng ô input liên quan (dựa vào từ khoá tiếng Việt)
    function routeServerErrorToField(message, extras = {}) {
        const msg = (message || "").toLowerCase();

        // Nếu server báo OTP hết hạn → đưa lỗi về trường OTP và cập nhật nút
        if (extras.otpExpired) {
            showMessageForInput(inputOtp, message, "failure");
            updateRegisterButton();
            return;
        }

        if (msg.includes("họ") || msg.includes("tên")) {
            showMessageForInput(inputFull, message, "failure");
        } else if (msg.includes("email")) {
            showMessageForInput(inputEmail, message, "failure");
        } else if (msg.includes("xác nhận mật khẩu")) {
            showMessageForInput(inputConf, message, "failure");
        } else if (msg.includes("mật khẩu")) {
            showMessageForInput(inputPass, message, "failure");
        } else if (msg.includes("otp")) {
            showMessageForInput(inputOtp, message, "failure");
        } else {
            // Mặc định đẩy ra khu vực OTP (khu vực thông báo chung)
            showMessageForInput(inputOtp, message, "failure");
        }
    }

    // Đưa UI form đăng ký về trạng thái mặc định (dùng khi mở lại/đóng xong)
    function resetRegisterUI() {
        clearAllMessages();

        if (inputFull) inputFull.value = "";
        if (inputEmail) inputEmail.value = "";
        if (inputPass) inputPass.value = "";
        if (inputConf) inputConf.value = "";
        if (inputOtp) inputOtp.value = "";

        if (timerLabel) timerLabel.textContent = "";

        if (btnSubmit) {
            btnSubmit.disabled = true;
            btnSubmit.classList.remove("btn-success");
            btnSubmit.style.opacity = "0.5";
            btnSubmit.style.cursor = "not-allowed";
            btnSubmit.innerHTML = "Đăng ký";
        }

        const otpMsgEl = getMessageElementOf(inputOtp);
        if (otpMsgEl) {
            otpMsgEl.style.color = "";
            otpMsgEl.textContent = "";
            if (otpMsgEl.dataset) delete otpMsgEl.dataset.state;
        }

        updateRegisterButton();
    }

    // Reset khi modal register mở/đóng để lần sau mở lại luôn sạch
    if (registerModalEl) {
        registerModalEl.addEventListener("show.bs.modal", resetRegisterUI);
        registerModalEl.addEventListener("hidden.bs.modal", resetRegisterUI);
    }

    // Submit đăng ký
    btnSubmit.addEventListener("click", async function () {
        clearAllMessages();

        // Chặn submit nếu OTP chưa được xác thực hoặc đã hết hạn
        if (!isOtpValid()) {
            const timerText = (timerLabel?.textContent || "").trim();
            if (timerText === "Hết hạn" || timerText === "0:00") {
                showMessageForInput(
                    inputOtp,
                    "Mã OTP đã hết hạn, vui lòng bấm Gửi OTP để nhận mã mới.",
                    "failure"
                );
            } else {
                showMessageForInput(
                    inputOtp,
                    "Vui lòng xác thực OTP trước khi đăng ký.",
                    "failure"
                );
            }
            updateRegisterButton();
            return;
        }

        // Gom dữ liệu gửi sang server (JSON)
        const payload = {
            fullName: trim(inputFull.value),
            email: trim(inputEmail.value),
            password: trim(inputPass.value),
            confirmPassword: trim(inputConf.value),
            otp: trim(inputOtp.value),
        };

        // Khoá nút + báo trạng thái đang xử lý
        const oldText = btnSubmit.innerHTML;
        btnSubmit.disabled = true;
        btnSubmit.innerHTML = "Đang tạo tài khoản...";
        btnSubmit.style.opacity = "0.7";

        try {
            // Gọi API /auth/register (trả JSON)
            const res = await fetch("/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            let data = null;
            try {
                data = await res.json();
            } catch (_) {}

            if (data && data.ok) {
                // Thành công: đổi trạng thái nút rồi chuyển modal (ẩn đăng ký → hiện đăng nhập)
                btnSubmit.classList.add("btn-success");
                btnSubmit.innerHTML = "🎉 Đăng ký thành công";

                setTimeout(() => {
                    if (registerModalEl) {
                        const reg =
                            bootstrap.Modal.getInstance?.(registerModalEl) ||
                            new bootstrap.Modal(registerModalEl);

                        // Sau khi ẩn xong register → mới show login (tránh chồng modal)
                        const showLoginOnce = () => {
                            registerModalEl.removeEventListener(
                                "hidden.bs.modal",
                                showLoginOnce
                            );
                            if (loginModalEl) {
                                const login =
                                    bootstrap.Modal.getInstance?.(
                                        loginModalEl
                                    ) || new bootstrap.Modal(loginModalEl);
                                login.show();
                            }
                        };
                        registerModalEl.addEventListener(
                            "hidden.bs.modal",
                            showLoginOnce,
                            { once: true }
                        );
                        reg.hide();
                    }
                }, 1200);
            } else {
                // Thất bại: định tuyến thông báo lỗi về đúng field
                const msg =
                    data && data.message
                        ? data.message
                        : "Đăng ký thất bại. Vui lòng thử lại.";
                routeServerErrorToField(msg, {
                    otpExpired: !!(data && data.otpExpired),
                });
                btnSubmit.innerHTML = oldText;
                updateRegisterButton();
            }
        } catch (err) {
            // Lỗi mạng / không gọi được server
            routeServerErrorToField(
                "Không thể kết nối máy chủ. Vui lòng thử lại."
            );
            btnSubmit.innerHTML = oldText;
            updateRegisterButton();
        }
    });
});
