/* global bootstrap */
document.addEventListener("DOMContentLoaded", function () {
  // --- Element refs ---
  const btnLogin = document.querySelector("#form-login .button-three");
  const cbRemember = document.getElementById("rememberMe");
  const inputPass = document.getElementById("login-password");
  const loginModalEl = document.getElementById("loginModal");
  const inputEmail = document.getElementById("login-email");
  const formLogin = document.getElementById("form-login");

  const otpManager = window.createOtpManager({
    modalElement: loginModalEl,
  });

  /** Xóa tất cả message lỗi/thông báo trên form login. */
  function clearAllMessages() {
    [inputEmail, inputPass].forEach((i) =>
      otpManager.showMessageForInput(i, "", "")
    );
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
        otpManager.showMessageForInput(inputPass, data.message, "failure");
        btnLogin.innerHTML = oldText;
      }
    } catch (err) {
      otpManager.showMessageForInput(
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
