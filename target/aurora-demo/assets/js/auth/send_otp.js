document.addEventListener("DOMContentLoaded", function () {
    // ——— Cache các phần tử UI cần dùng ———
    const sendOtpBtn = document.getElementById("send-otp");
    const timerLabel = document.getElementById("otp-timer");
    const registerModalEl = document.getElementById("registerModal");
    const emailInput = document.getElementById("register-email");
    const otpInput = document.getElementById("register-otp");

    // ——— Trạng thái đếm ngược & xác thực OTP ———
    let timeLeft;
    let countdown;
    let otpExpired = false;
    let verifyDebounce;
    let verifyController = null;

    // Khoá/Mở nút "Gửi OTP" + chỉnh style cho rõ trạng thái
    function setBtnDisabled(disabled) {
        sendOtpBtn.disabled = disabled;
        sendOtpBtn.style.opacity = disabled ? "0.5" : "1";
        sendOtpBtn.style.pointerEvents = disabled ? "none" : "";
        sendOtpBtn.style.cursor = disabled ? "not-allowed" : "";
    }

    // Đánh dấu OTP hết hạn, dừng timer và cập nhật UI
    function setTimerExpired() {
        clearInterval(countdown);
        timeLeft = 0;
        otpExpired = true;

        timerLabel.textContent = "Hết hạn";
        timerLabel.style.color = "red";
        setBtnDisabled(false);
    }

    // Kiểm tra định dạng email cơ bản
    function isValidEmail(email) {
        return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email);
    }

    // Tìm phần tử hiển thị thông báo đi kèm 1 input (theo .form-group > .form-message)
    function getMessageElementOf(inputElement) {
        if (!inputElement) return null;
        const container =
            inputElement.closest(".form-group") || inputElement.parentElement;
        return container?.querySelector(".form-message") || null;
    }

    // Hiển thị thông báo cho 1 input, kèm trạng thái success/failure để đổi màu
    function showMessageForInput(inputElement, message, outcomes) {
        const msgElement = getMessageElementOf(inputElement);
        if (msgElement) {
            msgElement.textContent = message;
            switch (outcomes) {
                case "success":
                    msgElement.style.color = "green";
                    break;
                case "failure":
                    msgElement.style.color = "red";
                    break;
                default:
                    msgElement.style.color = "";
            }
        }
    }

    // Render mm:ss lên nhãn đếm ngược
    function updateDisplay(seconds) {
        let min = Math.floor(seconds / 60);
        let sec = seconds % 60;
        timerLabel.textContent = `${min}:${sec < 10 ? "0" + sec : sec}`;
    }

    // Bắt đầu đếm ngược cho OTP: khoá nút, cập nhật mỗi giây, hết hạn thì báo
    function startCountdown(duration) {
        timeLeft = duration;

        clearInterval(countdown);
        otpExpired = false;

        timerLabel.style.color = "";
        updateDisplay(timeLeft);
        setBtnDisabled(true);

        countdown = setInterval(() => {
            timeLeft--;
            updateDisplay(timeLeft);

            if (timeLeft <= 0) {
                setTimerExpired();
                showMessageForInput(
                    otpInput,
                    "Mã OTP đã hết hạn, vui lòng bấm Gửi OTP để nhận mã mới.",
                    "failure"
                );
                return;
            }
        }, 1000);
    }

    // Xoá timer + reset UI liên quan OTP
    function resetCountdown() {
        clearInterval(countdown);
        timerLabel.textContent = "";
        setBtnDisabled(false);
        showMessageForInput(otpInput, "", "");
        if (emailInput) showMessageForInput(emailInput, "", "");
    }

    // Verify OTP ngay khi người dùng nhập đủ 6 số (và chưa hết hạn)
    async function verifyOtpNow() {
        console.log(otpExpired);
        const otp = (otpInput?.value || "").trim();
        const email = (emailInput?.value || "").trim();
        if (!/^\d{6}$/.test(otp) || otpExpired) return; // chỉ kiểm khi đủ 6 số
        try {
            // Huỷ request verify trước đó (nếu còn) để tránh race-condition
            if (verifyController) verifyController.abort();
            verifyController = new AbortController();

            const res = await fetch("/auth/verify-otp", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ otp, email }),
                signal: verifyController.signal,
            });
            const data = await res.json().catch(() => null);

            // Server trả về { ok: true, valid: boolean } → hiện thông báo tương ứng
            if (data.valid === true) {
                showMessageForInput(otpInput, "Mã OTP chính xác ✅", "success");
            } else {
                showMessageForInput(
                    otpInput,
                    `Mã OTP không đúng. ${
                        timeLeft ? "Còn " + timeLeft + "s" : ""
                    }`,
                    "failure"
                );
            }
        } catch (err) {
            // Abort thì bỏ qua; lỗi mạng thì báo người dùng
            if (err?.name === "AbortError") return;
            showMessageForInput(
                otpInput,
                "Không thể kết nối máy chủ để xác thực OTP.",
                "failure"
            );
        } finally {
            verifyController = null;
        }
    }

    // Debounce 200ms khi gõ OTP để tránh spam API verify
    if (otpInput) {
        otpInput.addEventListener("input", () => {
            if (verifyDebounce) clearTimeout(verifyDebounce);
            verifyDebounce = setTimeout(verifyOtpNow, 200);
        });
    }

    // Click "Gửi OTP": validate email → gọi /auth/send-otp → nếu OK thì bắt đầu đếm ngược
    sendOtpBtn.addEventListener("click", async function () {
        const email = (emailInput?.value || "").trim();

        // Validate email cơ bản + focus vào input khi lỗi
        if (!email) {
            showMessageForInput(emailInput, "Vui lòng nhập email.", "failure");
            emailInput?.focus();
            return;
        }
        if (!isValidEmail(email)) {
            showMessageForInput(emailInput, "Email không hợp lệ.", "failure");
            emailInput?.focus();
            return;
        }

        // Clear trạng thái OTP cũ trên UI
        otpInput && (otpInput.value = "");
        showMessageForInput(otpInput, "", "");
        showMessageForInput(emailInput, "", "");

        // Trong lúc gửi, khoá nút để tránh click liên tục
        const oldText = sendOtpBtn.textContent;
        sendOtpBtn.textContent = "Đang gửi...";
        setBtnDisabled(true);

        try {
            console.log("Hello DinhVietHao");
            // Gọi servlet gửi OTP (AJAX, JSON)
            const res = await fetch("/auth/send-otp", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email }),
            });

            let data = null;
            try {
                data = await res.json();
            } catch (_) {}

            sendOtpBtn.textContent = oldText;

            // Thất bại: mở khoá nút + hiển thị lý do (nếu server trả message)
            if (!res.ok || !data || data.ok !== true) {
                const msg =
                    data && data.message
                        ? data.message
                        : "Gửi OTP thất bại. Vui lòng thử lại.";
                setBtnDisabled(false);
                showMessageForInput(otpInput, msg, "failure");
                return;
            }

            // Thành công → dùng expiresIn từ server để đếm ngược
            const expires = data.expiresIn;
            startCountdown(expires);
            showMessageForInput(
                otpInput,
                "Đã gửi mã OTP. Vui lòng kiểm tra email.",
                "success"
            );
        } catch (e) {
            // Lỗi mạng: khôi phục nút và báo lỗi chung
            sendOtpBtn.textContent = oldText;
            setBtnDisabled(false);
            showMessageForInput(
                otpInput,
                "Không thể kết nối máy chủ. Vui lòng thử lại.",
                "failure"
            );
        }
    });

    // Khi đóng modal: dọn trạng thái local và gọi server để vô hiệu OTP hiện tại
    if (registerModalEl) {
        registerModalEl.addEventListener("hidden.bs.modal", async () => {
            // Huỷ verify đang chờ (debounce/abort)
            if (verifyDebounce) clearTimeout(verifyDebounce);
            if (verifyController) {
                verifyController.abort();
                verifyController = null;
            }

            resetCountdown();
            otpInput && (otpInput.value = "");
            showMessageForInput(otpInput, "", "");
            try {
                await fetch("/auth/invalidate-otp", { method: "POST" });
            } catch (_) {}
        });
    }
});
