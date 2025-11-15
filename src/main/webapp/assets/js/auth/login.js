/* global bootstrap */
document.addEventListener("DOMContentLoaded", function () {
  // --- Element refs ---
  const btnLogin = document.querySelector("#form-login .button-three");
  const cbRemember = document.getElementById("rememberMe");
  const inputPass = document.getElementById("login-password");
  const loginModalEl = document.getElementById("loginModal");
  const inputEmail = document.getElementById("login-email");
  const formLogin = document.getElementById("form-login");

  const errorContainer = document.getElementById("login-error-container");
  const errorMessage = document.getElementById("login-error-message");

  const otpManager = window.createOtpManager({
    modalElement: loginModalEl,
  });

  /* Xóa tất cả message lỗi/thông báo trên form login. */
  function clearAllMessages() {
    [inputEmail, inputPass].forEach((i) =>
      otpManager.showMessageForInput(i, "", "")
    );

    if (errorContainer) {
      errorContainer.classList.add("d-none");
    }
  }

  // Show error in prominent container
  function showError(message, type = "normal") {
    if (errorContainer && errorMessage) {
      errorMessage.textContent = message;
      errorContainer.classList.remove("d-none");

      if (type === "critical") {
        errorContainer.className = "alert alert-danger mb-3";
      } else {
        errorContainer.className = "alert alert-warning mb-3";
      }

      errorContainer.scrollIntoView({ behavior: "smooth", block: "nearest" });
    } else {
      otpManager.showMessageForInput(inputPass, message, "failure");
    }
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

  // Khi gõ vào email/password -> xoá message lỗi & re-enable nút
  [inputEmail, inputPass].forEach((i) => {
    i &&
      i.addEventListener("input", () => {
        otpManager.showMessageForInput(i, "", "");
        if (errorContainer) {
          errorContainer.classList.add("d-none");
        }
        setBtnDisabled(false, "Đăng nhập");
      });
  });

  // Submit login khi bấm nút
  btnLogin.addEventListener("click", async function () {
    clearAllMessages();

    const formData = new FormData();
    formData.append("action", "loginLocal");
    formData.append("email", inputEmail.value.trim());
    formData.append("password", inputPass.value.trim());
    formData.append("rememberMe", cbRemember.checked);

    const oldText = btnLogin.innerHTML;
    setBtnDisabled(true, "Đang đăng nhập...");

    try {
      const res = await fetch("/auth", {
        method: "POST",
        body: formData,
      });

      let data = await res.json();

      if (data.success) {
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

          // Điều hướng: ưu tiên data.redirect
          window.location.href = data.redirect;
        }, 1200);
      } else {
        const errorMsg =
          data.message || "Đăng nhập thất bại. Vui lòng thử lại.";
        if (
          errorMsg.includes("khóa") ||
          errorMsg.includes("LOCKED") ||
          errorMsg.includes("locked")
        ) {
          showError(errorMsg, "critical");
        } else if (
          errorMsg.includes("không khả dụng") ||
          errorMsg.includes("Trạng thái")
        ) {
          showError(errorMsg, "warning");
        } else {
          otpManager.showMessageForInput(inputPass, errorMsg, "failure");
        }
        btnLogin.innerHTML = oldText;
        setBtnDisabled(false);
      }
    } catch (err) {
      console.error("❌ Login error:", err);
      showError(
        "Không thể kết nối máy chủ. Vui lòng kiểm tra kết nối và thử lại.",
        "critical"
      );
      btnLogin.innerHTML = oldText;
      setBtnDisabled(false);
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

  // Auto-hide error after 12 seconds
  if (errorContainer) {
    const observer = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        if (
          mutation.type === "attributes" &&
          mutation.attributeName === "class"
        ) {
          if (!errorContainer.classList.contains("d-none")) {
            // Error is now visible - set auto-hide timer
            setTimeout(() => {
              errorContainer.classList.add("d-none");
            }, 12000); // 12 seconds (longer for critical messages)
          }
        }
      });
    });
    observer.observe(errorContainer, { attributes: true });
  }
});
