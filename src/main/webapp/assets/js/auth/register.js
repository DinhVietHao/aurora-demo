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

  const otpConfig = {
    sendOtpBtn: document.getElementById("send-otp"),
    timerLabel: timerLabel,
    otpInput: inputOtp,
    emailInput: inputEmail,
    modalElement: registerModalEl,
    defaultPurpose: "register",
  };

  const otpManager = window.createOtpManager(otpConfig);

  // Xoá toàn bộ message lỗi/thành công trên các trường cơ bản
  function clearAllMessages() {
    [inputFull, inputEmail, inputPass, inputConf].forEach((input) =>
      otpManager.showMessageForInput(input, "", "")
    );
  }

  // Enable/disable nút Đăng ký theo trạng thái OTP
  function updateRegisterButton() {
    if (otpManager.isOtpValid()) {
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
    const otpMsgEl = otpManager.getMessageElementOf(inputOtp);
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
    i &&
      i.addEventListener("input", () =>
        otpManager.showMessageForInput(i, "", "")
      );
  });

  // Khởi tạo trạng thái ban đầu của nút Đăng ký (thường là disabled)
  updateRegisterButton();

  // Ánh xạ thông báo lỗi từ server về đúng ô input liên quan (dựa vào từ khoá tiếng Việt)
  function routeServerErrorToField(message) {
    const msg = (message || "").toLowerCase(); // Thêm: khai báo msg
    if (msg.includes("họ") || msg.includes("tên")) {
      otpManager.showMessageForInput(inputFull, message, "failure");
    } else if (msg.includes("email")) {
      otpManager.showMessageForInput(inputEmail, message, "failure");
    } else if (msg.includes("xác nhận mật khẩu")) {
      otpManager.showMessageForInput(inputConf, message, "failure");
    } else if (msg.includes("mật khẩu")) {
      otpManager.showMessageForInput(inputPass, message, "failure");
    } else {
      // Mặc định đẩy ra khu vực OTP (khu vực thông báo chung)
      otpManager.showMessageForInput(inputOtp, message, "failure");
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

    const otpMsgEl = otpManager.getMessageElementOf(inputOtp);
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
  btnSubmit.addEventListener("click", async function (e) {
    e.preventDefault();

    clearAllMessages();

    // Chặn submit nếu OTP chưa được xác thực hoặc đã hết hạn
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
      return; // Không disable nút
    }

    // Bây giờ form và OTP đều valid → disable nút và gửi request
    const oldText = btnSubmit.innerHTML;
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = "Đang tạo tài khoản...";
    btnSubmit.style.opacity = "0.7";

    const formData = new FormData();
    formData.append("action", "register");
    formData.append("fullName", inputFull.value.trim());
    formData.append("email", inputEmail.value.trim());
    formData.append("password", inputPass.value.trim());
    formData.append("confirmPassword", inputConf.value.trim());

    try {
      const res = await fetch("/auth", {
        method: "POST",
        body: formData,
      });

      let data = await res.json();

      if (data.success) {
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
                  bootstrap.Modal.getInstance?.(loginModalEl) ||
                  new bootstrap.Modal(loginModalEl);
                login.show();
              }
            };
            registerModalEl.addEventListener("hidden.bs.modal", showLoginOnce, {
              once: true,
            });
            reg.hide();
          }
        }, 1200);
      } else {
        // Thất bại: định tuyến thông báo lỗi về đúng field
        routeServerErrorToField(data.message);
        btnSubmit.innerHTML = oldText;
        updateRegisterButton(); // Enable lại nút nếu fail
      }
    } catch (err) {
      // Lỗi mạng / không gọi được server
      routeServerErrorToField("Không thể kết nối máy chủ. Vui lòng thử lại.");
      btnSubmit.innerHTML = oldText;
      updateRegisterButton(); // Enable lại nút nếu error
    }
  });
});
