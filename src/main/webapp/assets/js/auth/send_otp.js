/**
 * OTP Manager - Module tổng quát để quản lý gửi và verify OTP
 * Verify OTP bằng cách so sánh client-side với OTP nhận từ server
 */
function createOtpManager(config) {
  const { sendOtpBtn, timerLabel, otpInput, emailInput, modalElement } = config;

  // ——— State quản lý OTP ———
  let timeLeft = 0;
  let countdown = null;
  let otpExpired = false;
  let verifyDebounce = null;
  let correctOtp = null; // Lưu OTP đúng từ server

  /**
   * Tìm element hiển thị message của input
   */
  function getMessageElementOf(inputElement) {
    if (!inputElement) return null;
    const container =
      inputElement.closest(".form-group") || inputElement.parentElement;
    return container?.querySelector(".form-message") || null;
  }

  /**
   * Hiển thị message cho input với trạng thái
   */
  function showMessageForInput(inputElement, message, status) {
    const msgElement = getMessageElementOf(inputElement);
    if (msgElement) {
      msgElement.textContent = message;
      switch (status) {
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

  /**
   * Validate email (có thể custom hoặc dùng default)
   */
  function isValidEmail(email) {
    return /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email);
  }

  /**
   * Enable/Disable nút "Gửi OTP"
   */
  function setBtnDisabled(disabled) {
    sendOtpBtn.disabled = disabled;
    sendOtpBtn.style.opacity = disabled ? "0.5" : "1";
    sendOtpBtn.style.pointerEvents = disabled ? "none" : "";
    sendOtpBtn.style.cursor = disabled ? "not-allowed" : "";
  }

  /**
   * Render mm:ss lên timer label
   */
  function updateDisplay(seconds) {
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    timerLabel.textContent = `${min}:${sec < 10 ? "0" + sec : sec}`;
  }

  /**
   * Đánh dấu OTP hết hạn
   */
  function setTimerExpired() {
    clearInterval(countdown);
    timeLeft = 0;
    otpExpired = true;
    correctOtp = null; // Xoá OTP khi hết hạn

    timerLabel.textContent = "Hết hạn";
    timerLabel.style.color = "red";
    setBtnDisabled(false);

    showMessageForInput(
      otpInput,
      "Mã OTP đã hết hạn, vui lòng bấm Gửi OTP để nhận mã mới.",
      "failure"
    );
  }

  /**
   * Bắt đầu đếm ngược OTP
   */
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
      }
    }, 1000);
  }

  /**
   * Reset countdown và UI
   */
  function resetCountdown() {
    clearInterval(countdown);
    timeLeft = 0;
    otpExpired = false;
    correctOtp = null;
    timerLabel.textContent = "";
    setBtnDisabled(false);
    showMessageForInput(otpInput, "", "");
    showMessageForInput(emailInput, "", "");
  }

  /**
   * Verify OTP client-side (so sánh với correctOtp)
   */
  function verifyOtpNow() {
    const userOtp = (otpInput?.value || "").trim();

    // Chỉ verify khi nhập đủ 6 số và chưa hết hạn
    if (!/^\d{6}$/.test(userOtp) || otpExpired || !correctOtp) {
      return;
    }

    // So sánh OTP người dùng nhập với OTP từ server
    if (userOtp === correctOtp) {
      showMessageForInput(otpInput, "Mã OTP chính xác ✅", "success");
    } else {
      showMessageForInput(
        otpInput,
        `Mã OTP không đúng. ${timeLeft ? "Còn " + timeLeft + "s" : ""}`,
        "failure"
      );
    }
  }

  /**
   * Gửi OTP qua email
   */
  async function sendOtp() {
    const email = (emailInput?.value || "").trim();

    // Validate email
    if (!email) {
      showMessageForInput(emailInput, "Vui lòng nhập email.", "failure");
      return false;
    }

    if (!isValidEmail(email)) {
      showMessageForInput(emailInput, "Email không hợp lệ.", "failure");
      return false;
    }

    // Clear OTP input và message cũ
    if (otpInput) otpInput.value = "";
    showMessageForInput(otpInput, "", "");
    showMessageForInput(emailInput, "", "");
    correctOtp = null; // Reset OTP cũ

    // Khoá nút trong lúc gửi
    const oldText = sendOtpBtn.textContent;
    sendOtpBtn.textContent = "Đang gửi...";
    setBtnDisabled(true);

    const formData = new FormData();
    formData.append("action", "send-otp");
    formData.append("email", email);

    try {
      const res = await fetch("/auth", {
        method: "POST",
        body: formData,
      });

      let data = await res.json();

      sendOtpBtn.textContent = oldText;

      // Thất bại
      if (!data.success) {
        setBtnDisabled(false);
        let msg = data.message;
        if (msg.includes("Email") || msg.includes("không hợp lệ")) {
          showMessageForInput(emailInput, data.message, "failure");
        } else {
          showMessageForInput(otpInput, data.message, "failure");
        }
        return false;
      }

      // Thành công → lưu OTP từ server và bắt đầu đếm ngược
      correctOtp = data.otp; // Lưu OTP để verify
      const expiresIn = Number.parseInt(data.expiresIn);
      startCountdown(expiresIn);
      showMessageForInput(otpInput, data.message, "success");
      return true;
    } catch (e) {
      sendOtpBtn.textContent = oldText;
      setBtnDisabled(false);
      showMessageForInput(
        otpInput,
        "Không thể kết nối máy chủ. Vui lòng thử lại.",
        "failure"
      );
      return false;
    }
  }

  /**
   * Check OTP đã valid chưa (dựa vào message)
   */
  function isOtpValid() {
    const msgEl = getMessageElementOf(otpInput);
    return msgEl && msgEl.textContent.trim() === "Mã OTP chính xác ✅";
  }

  /**
   * Invalidate OTP hiện tại
   */
  function invalidateOtp() {
    if (verifyDebounce) clearTimeout(verifyDebounce);
    resetCountdown();
    if (otpInput) otpInput.value = "";
    showMessageForInput(otpInput, "", "");
    correctOtp = null;
  }

  // ——— Setup event listeners ———

  // Debounce verify khi gõ OTP
  if (otpInput) {
    otpInput.addEventListener("input", () => {
      if (verifyDebounce) clearTimeout(verifyDebounce);
      verifyDebounce = setTimeout(verifyOtpNow, 200);
    });
  }

  // Click "Gửi OTP"
  if (sendOtpBtn) {
    sendOtpBtn.addEventListener("click", sendOtp);
  }

  // Khi đóng modal → invalidate OTP
  if (modalElement) {
    modalElement.addEventListener("hidden.bs.modal", invalidateOtp);
  }

  // ——— Public API ———
  return {
    sendOtp,
    isOtpValid,
    verifyOtpNow,
    invalidateOtp,
    resetCountdown,
    showMessageForInput,
    getMessageElementOf,
    getTimeLeft: () => timeLeft,
    isExpired: () => otpExpired,
  };
}

// Attach ra global để các file khác sử dụng
window.createOtpManager = createOtpManager;
