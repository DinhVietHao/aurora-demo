/* global bootstrap */
/**
 * Luồng "Quên mật khẩu" phía client (2 modal):
 *  - Modal 1 (forgetPasswordModal): nhập email, gửi yêu cầu START để nhận OTP.
 *  - Modal 2 (createPasswordModal): nhập OTP + mật khẩu mới, gửi COMPLETE để đặt lại.
 * Có hỗ trợ RESEND OTP.
 *
 * Lưu ý UI:
 *  - Disable nút khi request đang chạy hoặc khi form chưa thay đổi sau lần lỗi trước.
 *  - Khi chuyển modal: ẩn modal cũ, dọn state, rồi mới mở modal mới/đăng nhập.
 */
document.addEventListener("DOMContentLoaded", function () {
    // --------- Cache phần tử UI chính ---------
    const forgetModalEl = document.getElementById("forgetPasswordModal");
    const createModalEl = document.getElementById("createPasswordModal");
    const loginModalEl = document.getElementById("loginModal");

    const btnCheckMail = document.querySelector(
        "#form-forget-password .button-three"
    );
    const inputEmail = document.getElementById("forget-password-email");

    const btnUpdatePass = document.querySelector(
        "#form-create-password .button-three"
    );
    const inputOtp = document.getElementById("create-password-otp");
    const inputPw = document.getElementById("create-password-password");
    const inputPw2 = document.getElementById(
        "create-password-password-confirmation"
    );
    const resendLink = createModalEl?.querySelector(
        ".auth-form-have-account a"
    );
    const createDesc = createModalEl?.querySelector(".auth-form-desc");

    // Trạng thái đang thao tác
    let workingEmail = null; // email được xác nhận ở bước START
    let lastEmailAttempt = null; // snapshot input email lần gửi trước (để kiểm tra "đã thay đổi chưa")
    let lastResetAttempt = null; // snapshot tổ hợp (otp|pw1|pw2) lần gửi trước

    /**
     * Tìm phần tử hiển thị thông báo đi kèm input ('.form-group > .form-message')
     * @param {HTMLElement} inputElement - input cần tìm message element
     * @returns {HTMLElement|null}
     */
    function getMessageElementOf(inputElement) {
        if (!inputElement) return null;
        const container =
            inputElement.closest(".form-group") || inputElement.parentElement;
        return container?.querySelector(".form-message") || null;
    }

    /**
     * Hiển thị thông báo cho 1 input, kèm trạng thái để tô màu.
     * @param {HTMLElement} inputElement
     * @param {string} message
     * @param {"success"|"failure"|""} outcomes
     */
    function showMessageForInput(inputElement, message, outcomes) {
        const msgElement = getMessageElementOf(inputElement);
        if (!msgElement) return;
        msgElement.textContent = message || "";
        msgElement.style.color =
            outcomes === "success"
                ? "green"
                : outcomes === "failure"
                ? "red"
                : "";
    }

    /**
     * Kiểm tra email cơ bản (client-side)
     * @param {string} email
     * @returns {boolean}
     */
    function isValidEmail(email) {
        return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,})+$/.test(email);
    }

    /**
     * Mask email để hiển thị (giữ 1-2 ký tự đầu local-part)
     * @param {string} email
     * @returns {string}
     */
    function maskEmail(email) {
        if (!email || !email.includes("@")) return "*******";
        const [local, domain] = email.split("@");
        const head = local.length <= 2 ? local.slice(0, 1) : local.slice(0, 2);
        return head + "*****@" + domain;
    }

    /**
     * Đặt trạng thái nút (disable/enable) + đổi nhãn khi disable
     * @param {HTMLButtonElement} btn
     * @param {boolean} disabled
     * @param {string} [textWhenDisabled]
     */
    function setBtnState(btn, disabled, textWhenDisabled) {
        if (!btn) return;
        if (!btn.dataset.origText) btn.dataset.origText = btn.innerHTML;
        btn.disabled = !!disabled;
        btn.style.opacity = disabled ? "0.65" : "";
        btn.style.cursor = disabled ? "not-allowed" : "";
        btn.innerHTML =
            disabled && typeof textWhenDisabled === "string"
                ? textWhenDisabled
                : btn.dataset.origText;
    }

    /**
     * Kiểm tra 1 chuỗi có khác snapshot trước đó hay không (để chỉ enable khi user đã sửa)
     * @param {string} strKey - giá trị hiện tại
     * @param {string|null} lastKey - snapshot trước
     */
    function hasChangedSince(strKey, lastKey) {
        return strKey !== (lastKey ?? null);
    }

    /**
     * Ẩn modal cũ rồi (tuỳ chọn) mở modal mới. Đảm bảo backdrop không bị nhân đôi.
     * @param {HTMLElement|null} oldEl - modal đang mở
     * @param {HTMLElement|null} newEl - modal sẽ mở (có thể null nếu chỉ đóng)
     * @param {Function} [afterHideCb] - callback chạy sau khi oldEl ẩn xong
     */
    function swapModals(oldEl, newEl, afterHideCb) {
        const oldInst = oldEl
            ? bootstrap.Modal.getOrCreateInstance(oldEl)
            : null;
        const newInst = newEl
            ? bootstrap.Modal.getOrCreateInstance(newEl)
            : null;
        if (!oldInst && newInst) {
            newInst.show();
            return;
        }
        if (!oldInst) return;
        const handler = () => {
            oldEl.removeEventListener("hidden.bs.modal", handler);
            // Xoá backdrop dư (nếu có)
            document.querySelectorAll(".modal-backdrop").forEach((bd, idx) => {
                if (idx > 0) bd.remove();
            });
            if (typeof afterHideCb === "function") afterHideCb();
            if (newInst) newInst.show();
        };
        oldEl.addEventListener("hidden.bs.modal", handler, { once: true });
        oldInst.hide();
    }

    /** Dọn form nhập email (modal 1) */
    function clearForgetForm() {
        inputEmail && (inputEmail.value = "");
        showMessageForInput(inputEmail, "", "");
        lastEmailAttempt = null;
        updateEmailBtnState();
    }

    /** Dọn form nhập OTP & password (modal 2) */
    function clearCreateForm() {
        [inputOtp, inputPw, inputPw2].forEach((i) => i && (i.value = ""));
        [inputOtp, inputPw, inputPw2].forEach((i) =>
            showMessageForInput(i, "", "")
        );
        lastResetAttempt = null;
        updateResetBtnState();
    }

    /** Enable/disable nút gửi email dựa trên tính hợp lệ và thay đổi input */
    function updateEmailBtnState() {
        const email = (inputEmail.value || "").trim().toLowerCase();
        const valid = !!email && isValidEmail(email);
        const changed = hasChangedSince(email, lastEmailAttempt);
        setBtnState(btnCheckMail, !(valid && changed));
        if (!valid) showMessageForInput(inputEmail, "", "");
    }

    /** Tạo snapshot khóa cho modal 2 để so sánh thay đổi (otp|pw1|pw2) */
    function buildResetKey() {
        const otp = (inputOtp.value || "").trim();
        const pw1 = (inputPw.value || "").trim();
        const pw2 = (inputPw2.value || "").trim();
        return `${otp}|${pw1}|${pw2}`;
    }

    /**
     * Validate input modal 2 ở mức cơ bản:
     *  - Có OTP
     *  - Mật khẩu >= 6 ký tự
     *  - Mật khẩu nhập lại khớp
     */
    function validateResetInputs() {
        const otp = (inputOtp.value || "").trim();
        const pw1 = (inputPw.value || "").trim();
        const pw2 = (inputPw2.value || "").trim();
        if (!otp) return false;
        if (!pw1 || pw1.length < 6) return false;
        if (pw1 !== pw2) return false;
        return true;
    }

    /** Enable/disable nút "Xong" dựa trên tính hợp lệ và thay đổi input */
    function updateResetBtnState() {
        const key = buildResetKey();
        const valid = validateResetInputs();
        const changed = hasChangedSince(key, lastResetAttempt);
        setBtnState(btnUpdatePass, !(valid && changed));
        if (valid) {
            [inputOtp, inputPw, inputPw2].forEach((i) =>
                showMessageForInput(i, "", "")
            );
        }
    }

    // Lắng nghe thay đổi để bật/tắt nút đúng thời điểm
    inputEmail?.addEventListener("input", updateEmailBtnState);
    [inputOtp, inputPw, inputPw2].forEach((el) =>
        el?.addEventListener("input", updateResetBtnState)
    );

    // Khởi đầu: disable hai nút (chỉ bật khi dữ liệu hợp lệ và khác lần lỗi trước)
    setBtnState(btnCheckMail, true);
    setBtnState(btnUpdatePass, true);

    // ============= STEP 1: gửi email để nhận OTP (action=start) =============
    btnCheckMail?.addEventListener("click", async function () {
        const email = (inputEmail.value || "").trim().toLowerCase();
        if (!email) {
            showMessageForInput(inputEmail, "Vui lòng nhập email.", "failure");
            inputEmail.focus();
            return;
        }
        if (!isValidEmail(email)) {
            showMessageForInput(inputEmail, "Email không hợp lệ.", "failure");
            inputEmail.focus();
            return;
        }

        // Ghi snapshot để chỉ re-enable khi user sửa email
        lastEmailAttempt = email;
        setBtnState(btnCheckMail, true, "Đang xử lý...");

        try {
            // Gọi servlet gộp với action=start
            const res = await fetch("/auth/forgot", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ action: "start", email }),
            });
            const data = await res.json().catch(() => null);

            // Thất bại → hiện thông báo và giữ disable (đến khi user sửa input)
            if (!data || data.ok !== true) {
                const msg =
                    data && data.message
                        ? data.message
                        : "Không thể xử lý. Vui lòng thử lại.";
                showMessageForInput(inputEmail, msg, "failure");
                return;
            }

            // Thành công → lưu email, cập nhật mô tả, dọn modal 1 và chuyển sang modal 2
            workingEmail = email;
            if (createDesc) {
                const display = data.masked || maskEmail(email);
                createDesc.textContent = `Mã xác nhận đã gửi về email ${display}`;
            }
            clearForgetForm();
            swapModals(forgetModalEl, createModalEl, () => {
                clearCreateForm(); // đảm bảo modal-2 sạch
            });
        } catch (err) {
            showMessageForInput(
                inputEmail,
                "Không thể kết nối máy chủ. Vui lòng thử lại.",
                "failure"
            );
        }
        // Không enable lại ở đây: chỉ enable khi người dùng sửa input (updateEmailBtnState)
    });

    // ============= Gửi lại OTP (action=resend) =============
    resendLink?.addEventListener("click", async (e) => {
        e.preventDefault();
        if (!workingEmail) return; // chưa qua bước start
        try {
            const res = await fetch("/auth/forgot", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ action: "resend", email: workingEmail }),
            });
            const data = await res.json().catch(() => null);
            showMessageForInput(
                inputOtp,
                data?.ok
                    ? "Đã gửi lại OTP."
                    : data?.message || "Không thể gửi lại OTP.",
                data?.ok ? "success" : "failure"
            );
        } catch (_) {
            showMessageForInput(inputOtp, "Không thể gửi lại OTP.", "failure");
        }
    });

    // ============= STEP 2: hoàn tất đặt lại mật khẩu (action=complete) =============
    btnUpdatePass?.addEventListener("click", async (e) => {
        e.preventDefault();
        const otp = (inputOtp.value || "").trim();
        const pw1 = (inputPw.value || "").trim();
        const pw2 = (inputPw2.value || "").trim();

        // Validate client-side cơ bản
        if (!otp) {
            showMessageForInput(inputOtp, "Vui lòng nhập OTP.", "failure");
            inputOtp.focus();
            return;
        }
        if (!pw1 || pw1.length < 6) {
            showMessageForInput(
                inputPw,
                "Mật khẩu tối thiểu 6 ký tự.",
                "failure"
            );
            inputPw.focus();
            return;
        }
        if (pw1 !== pw2) {
            showMessageForInput(
                inputPw2,
                "Mật khẩu xác nhận không khớp.",
                "failure"
            );
            inputPw2.focus();
            return;
        }

        // Ghi snapshot để chỉ re-enable khi user sửa bất kỳ trường nào
        lastResetAttempt = `${otp}|${pw1}|${pw2}`;
        setBtnState(btnUpdatePass, true, "Đang cập nhật...");

        try {
            // Gọi servlet gộp với action=complete
            const res = await fetch("/auth/forgot", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "complete",
                    email: workingEmail,
                    otp,
                    password: pw1,
                }),
            });
            const data = await res.json().catch(() => null);

            if (data?.ok) {
                // Thông báo thành công ngắn → đóng modal 2 → mở modal đăng nhập
                showMessageForInput(
                    inputOtp,
                    "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.",
                    "success"
                );
                const afterHide = () => {
                    clearCreateForm();
                    workingEmail = null;
                    if (loginModalEl) {
                        const loginInst =
                            bootstrap.Modal.getOrCreateInstance(loginModalEl);
                        loginInst.show();
                    }
                };
                swapModals(createModalEl, null, afterHide);
            } else {
                showMessageForInput(
                    inputOtp,
                    data?.message || "Không thể đặt lại mật khẩu.",
                    "failure"
                );
            }
        } catch (_) {
            showMessageForInput(
                inputOtp,
                "Không thể kết nối máy chủ. Vui lòng thử lại.",
                "failure"
            );
        }
        // Không enable lại ở đây: chỉ enable khi người dùng sửa input (updateResetBtnState)
    });

    // Khi modal 1 đóng thủ công → dọn trạng thái
    forgetModalEl?.addEventListener("hidden.bs.modal", () => {
        clearForgetForm();
    });

    // Khi modal 2 mở → cập nhật trạng thái nút theo dữ liệu hiện có (phòng cache)
    createModalEl?.addEventListener("shown.bs.modal", () => {
        updateResetBtnState();
    });
});
