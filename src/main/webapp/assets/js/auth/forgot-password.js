/* global bootstrap */
document.addEventListener("DOMContentLoaded", function () {
  const forgetModalEl = document.getElementById("forgetPasswordModal");
  const resetPassModalEl = document.getElementById("resetPasswordModal");
  const loginModalEl = document.getElementById("loginModal");

  const btnCheckMail = document.querySelector(
    "#form-forget-password .button-three"
  );
  const inputEmail = document.getElementById("forget-password-email");

  const btnUpdatePass = document.querySelector(
    "#form-create-password .button-three"
  );
  const inputOtp = document.getElementById("forgotPassword-otp");
  const inputResetPassword = document.getElementById("setupNewPassword");
  const inputConfirmPassword = document.getElementById(
    "setupNewPassword-confirmation"
  );

  const createDesc = resetPassModalEl?.querySelector(".auth-form-desc");
  const timerLabel = document.getElementById("otp-timer-forgot");

  // Khai báo biến state
  let workingEmail = null;
  let lastEmailAttempt = null;
  let lastResetAttempt = null;

  const otpConfig = {
    sendOtpBtn: document.getElementById("send-otp-forgot"),
    timerLabel: document.getElementById("otp-timer-forgot"),
    otpInput: inputOtp,
    emailInput: inputEmail,
    modalElement: resetPassModalEl,
    defaultPurpose: "forgot-password",
  };

  const otpManager = window.createOtpManager(otpConfig);

  /**
   * Ẩn modal cũ rồi mở modal mới
   */
  function swapModals(oldEl, newEl, afterHideCb) {
    const oldInst = oldEl ? bootstrap.Modal.getOrCreateInstance(oldEl) : null;
    const newInst = newEl ? bootstrap.Modal.getOrCreateInstance(newEl) : null;

    if (!oldInst && newInst) {
      newInst.show();
      return;
    }

    if (!oldInst) return;

    const handler = () => {
      oldEl.removeEventListener("hidden.bs.modal", handler);
      document.querySelectorAll(".modal-backdrop").forEach((bd, idx) => {
        if (idx > 0) bd.remove();
      });
      if (typeof afterHideCb === "function") afterHideCb();
      if (newInst) newInst.show();
    };

    oldEl.addEventListener("hidden.bs.modal", handler, { once: true });
    oldInst.hide();
  }

  /**
   * Đặt trạng thái nút (disable/enable) + đổi nhãn khi disable
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

  /** Dọn form nhập email (modal 1) */
  function clearForgetForm() {
    otpManager.showMessageForInput(inputEmail, "", "");
    lastEmailAttempt = null;
    updateEmailBtnState();
  }

  /** Dọn form nhập OTP & password (modal 2) */
  function clearResetForm() {
    [inputOtp, inputResetPassword, inputConfirmPassword].forEach(
      (i) => i && (i.value = "")
    );
    [inputOtp, inputResetPassword, inputConfirmPassword].forEach((i) =>
      otpManager.showMessageForInput(i, "", "")
    );
    lastResetAttempt = null;
    updateResetBtnState();
  }

  /** Enable/disable nút gửi email dựa trên tính hợp lệ và thay đổi input */
  function updateEmailBtnState() {
    const email = inputEmail.value.trim().toLowerCase();
    const valid = otpManager.isValidEmail(
      inputEmail.value.trim().toLowerCase()
    );
    const changed = hasChangedSince(email, lastEmailAttempt);
    setBtnState(btnCheckMail, !(valid && changed));
    if (!valid) otpManager.showMessageForInput(inputEmail, "", "");
  }

  /** Tạo snapshot khóa cho modal 2 để so sánh thay đổi (otp|pw1|pw2) */
  function buildResetKey() {
    const otp = inputOtp.value.trim();
    const resetPassword = inputResetPassword.value.trim();
    const confirmPassword = inputConfirmPassword.value.trim();
    return `${otp}|${resetPassword}|${confirmPassword}`;
  }

  /**
   * Validate input modal 2 ở mức cơ bản:
   *  - Có OTP
   *  - Mật khẩu >= 6 ký tự
   *  - Mật khẩu nhập lại khớp
   */
  function validateResetInputs() {
    const otp = inputOtp.value.trim();
    const resetPassword = inputResetPassword.value.trim();
    const confirmPassword = inputConfirmPassword.value.trim();
    if (!otp) return false;
    if (resetPassword.length < 8) return false;
    if (resetPassword !== confirmPassword) return false;
    return true;
  }

  /** Enable/disable nút "Xong" dựa trên tính hợp lệ và thay đổi input */
  function updateResetBtnState() {
    const key = buildResetKey();
    const valid = validateResetInputs();
    const changed = hasChangedSince(key, lastResetAttempt);
    setBtnState(btnUpdatePass, !(valid && changed));
    if (valid) {
      otpManager.showMessageForInput(inputResetPassword, "", "");
      otpManager.showMessageForInput(inputConfirmPassword, "", "");
    }
  }

  // Lắng nghe thay đổi để bật/tắt nút đúng thời điểm
  inputEmail?.addEventListener("input", updateEmailBtnState);
  [inputOtp, inputResetPassword, inputConfirmPassword].forEach((el) =>
    el?.addEventListener("input", updateResetBtnState)
  );

  // Khởi đầu: disable hai nút (chỉ bật khi dữ liệu hợp lệ và khác lần lỗi trước)
  setBtnState(btnCheckMail, true);
  setBtnState(btnUpdatePass, true);

  // ============= STEP 1: Gửi email để nhận OTP =============
  btnCheckMail?.addEventListener("click", async function () {
    const email = inputEmail.value.trim().toLowerCase();

    if (!email) {
      otpManager.showMessageForInput(
        inputEmail,
        "Vui lòng nhập email.",
        "failure"
      );
      inputEmail.focus();
      return;
    }

    if (!otpManager.isValidEmail(email)) {
      otpManager.showMessageForInput(
        inputEmail,
        "Email không hợp lệ.",
        "failure"
      );
      inputEmail.focus();
      return;
    }

    lastEmailAttempt = email;
    setBtnState(btnCheckMail, true, "Đang xử lý...");

    // ✅ Gọi sendOtp với purpose="forgot-password"
    const success = await otpManager.sendOtp();

    if (success) {
      workingEmail = email;
      if (createDesc) {
        createDesc.textContent = `Mã xác nhận đã gửi về email ${otpManager.maskEmail(
          email
        )}`;
      }
      clearForgetForm();
      swapModals(forgetModalEl, resetPassModalEl, () => {
        clearResetForm();
      });
    } else {
      setBtnState(btnUpdatePass, false);
    }
  });

  // ============= STEP 2: Hoàn tất đặt lại mật khẩu =============
  btnUpdatePass?.addEventListener("click", async (e) => {
    e.preventDefault();
    const otp = inputOtp.value.trim();
    const resetPassword = inputResetPassword.value.trim();
    const confirmPassword = inputConfirmPassword.value.trim();

    if (!otpManager.isOtpValid()) {
      const timerText = (timerLabel?.textContent || "").trim();
      if (timerText === "Hết hạn" || timerText === "0:00") {
        otpManager.showMessageForInput(
          inputOtp,
          "Mã OTP đã hết hạn, vui lòng bấm Gửi OTP để nhận mã mới.",
          "failure"
        );
      } else {
        otpManager.showMessageForInput(
          inputOtp,
          "Vui lòng xác thực OTP trước khi đăng ký.",
          "failure"
        );
      }
      return;
    }

    if (resetPassword.length < 8) {
      otpManager.showMessageForInput(
        inputResetPassword,
        "Mật khẩu tối thiểu 8 ký tự.",
        "failure"
      );
      inputResetPassword.focus();
      return;
    }

    if (resetPassword !== confirmPassword) {
      otpManager.showMessageForInput(
        inputConfirmPassword,
        "Mật khẩu xác nhận không khớp.",
        "failure"
      );
      inputConfirmPassword.focus();
      return;
    }

    // Ghi snapshot để chỉ re-enable khi user sửa bất kỳ trường nào
    lastResetAttempt = `${otp}|${resetPassword}|${confirmPassword}`;
    setBtnState(btnUpdatePass, true, "Đang cập nhật...");

    const formData = new FormData();
    formData.append("action", "forgotPassword");
    formData.append("email", workingEmail);
    formData.append("password", resetPassword);

    try {
      const res = await fetch("/auth", {
        method: "POST",
        body: formData,
      });

      const data = await res.json();

      if (data.success) {
        setBtnState(btnUpdatePass, true, data.message);

        const afterHide = () => {
          clearResetForm();
          workingEmail = null;
          if (loginModalEl) {
            const loginInst = bootstrap.Modal.getOrCreateInstance(loginModalEl);
            loginInst.show();
          }
        };

        setTimeout(() => {
          [
            inputEmail,
            inputOtp,
            inputResetPassword,
            inputConfirmPassword,
          ].forEach((i) => i && (i.value = ""));
          swapModals(resetPassModalEl, null, afterHide);
        }, 2000);
      } else {
        otpManager.showMessageForInput(
          inputConfirmPassword,
          data.message,
          "failure"
        );
      }
    } catch (_) {
      otpManager.showMessageForInput(
        inputConfirmPassword,
        "Không thể kết nối máy chủ. Vui lòng thử lại.",
        "failure"
      );
    }
  });

  // Khi modal 1 đóng thủ công → dọn trạng thái
  forgetModalEl?.addEventListener("hidden.bs.modal", () => {
    clearForgetForm();
  });

  // Khi modal 2 mở → cập nhật trạng thái nút theo dữ liệu hiện có (phòng cache)
  resetPassModalEl?.addEventListener("shown.bs.modal", () => {
    updateResetBtnState();
  });
});
