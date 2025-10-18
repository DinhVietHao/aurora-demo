// ========= Change password of profile ==========
document
  .getElementById("formChangePassword")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    const currentPassword = this.currentPassword.value;
    const newPassword = this.newPassword.value;
    const confirmNewPassword = this.confirmNewPassword.value;
    const errorDiv = document.getElementById("changePasswordError");
    errorDiv.classList.add("d-none");
    errorDiv.textContent = "";
    // Kiểm tra mật khẩu mới và xác nhận
    if (newPassword.length < 8) {
      errorDiv.textContent = "Mật khẩu mới phải từ 8 ký tự trở lên.";
      errorDiv.classList.remove("d-none");
      return;
    }
    if (newPassword !== confirmNewPassword) {
      errorDiv.textContent = "Xác nhận mật khẩu mới không khớp.";
      errorDiv.classList.remove("d-none");
      return;
    }
    // Có thể kiểm tra thêm: có số, ký tự đặc biệt, ...
    // Gửi AJAX
    fetch("/profile", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `action=changePassword&currentPassword=${encodeURIComponent(
        currentPassword
      )}&newPassword=${encodeURIComponent(
        newPassword
      )}&confirmNewPassword=${encodeURIComponent(confirmNewPassword)}`,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          document.getElementById("formChangePassword").reset();

          var modalEl = document.getElementById("changePasswordModal");
          var modalInstance = bootstrap.Modal.getInstance(modalEl);
          if (modalInstance) {
            modalInstance.hide();
          }

          toast({
            title: "Thành công!",
            message: data.message,
            type: "success",
            duration: 3000,
          });
        } else {
          errorDiv.textContent = data.message;
          errorDiv.classList.remove("d-none");
        }
      });
  });

document.addEventListener("DOMContentLoaded", function () {
  var modal = document.getElementById("changePasswordModal");
  if (modal) {
    modal.addEventListener("shown.bs.modal", function () {
      modal.querySelectorAll(".toggle-password").forEach(function (eyeIcon) {
        if (!eyeIcon.dataset.bound) {
          eyeIcon.addEventListener("click", function () {
            const input = eyeIcon
              .closest(".input-group")
              .querySelector("input");

            if (input.type === "password") {
              input.type = "text";
              eyeIcon.classList.remove("bi-eye-slash");
              eyeIcon.classList.add("bi-eye");
            } else {
              input.type = "password";
              eyeIcon.classList.remove("bi-eye");
              eyeIcon.classList.add("bi-eye-slash");
            }
          });
          eyeIcon.dataset.bound = "true";
        }
      });
    });

    modal.addEventListener("hidden.bs.modal", function () {
      var form = document.getElementById("formChangePassword");
      if (form) form.reset();
      var errorDiv = document.getElementById("changePasswordError");

      if (errorDiv) {
        errorDiv.textContent = "";
        errorDiv.classList.add("d-none");
      }

      modal.querySelectorAll(".input-group input").forEach(function (input) {
        input.type = "password";
      });

      modal.querySelectorAll(".toggle-password").forEach(function (icon) {
        icon.classList.remove("bi-eye");
        icon.classList.add("bi-eye-slash");
      });
    });
  }
});

// ========= Upload avatar of profile ========
document.addEventListener("DOMContentLoaded", function () {
  const avatarInput = document.getElementById("avatarInput");
  const avatarPreview = document.getElementById("avatarPreview");
  const avatarSidebar = document.getElementById("avatarSidebar");

  if (avatarInput && avatarPreview) {
    avatarInput.addEventListener("change", function () {
      const file = avatarInput.files[0];
      if (!file) return;

      // Kiểm tra file là ảnh và không quá dung lượng (ví dụ 5MB)
      if (!file.type.startsWith("image/")) {
        alert("Vui lòng chọn file ảnh hợp lệ.");
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        alert("Ảnh vượt quá dung lượng cho phép (5MB).");
        return;
      }

      // Hiển thị preview ngay trên UI trước khi upload
      const reader = new FileReader();
      reader.onload = function (e) {
        avatarPreview.src = e.target.result;
        if (avatarSidebar) avatarSidebar.src = e.target.result;
      };
      reader.readAsDataURL(file);

      // Tạo form data để gửi file lên server
      const formData = new FormData();
      formData.append("action", "uploadAvatar");
      formData.append("avatarCustomer", file);

      fetch("/profile", {
        method: "POST",
        body: formData,
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            if (data.avatarUrl) {
              avatarPreview.src = data.avatarUrl;
              if (avatarSidebar) avatarSidebar.src = data.avatarUrl;
            }
            toast({
              title: "Thành công!",
              message: data.message,
              type: "success",
              duration: 3000,
            });
          } else {
            toast({
              title: "Thất bại!",
              message: data.message,
              type: "error",
              duration: 3000,
            });
          }
        })
        .catch(() => {
          toast({
            title: "Thất bại!",
            message: "Đã xảy ra lỗi khi tải ảnh đại diện.",
            type: "error",
            duration: 3000,
          });
        });
    });
  }
});

// ======== Change email of profile =========
/* global bootstrap */
document.addEventListener("DOMContentLoaded", function () {
  const $ = (selector, parent = document) => parent.querySelector(selector);

  // ===== Modal Elements =====
  const verifyOldEmailModalEl = $("#verifyOldEmailModal");
  const enterNewEmailModalEl = $("#enterNewEmailModal");
  const verifyNewEmailModalEl = $("#verifyNewEmailModal");

  const verifyOldEmailModal = new bootstrap.Modal(verifyOldEmailModalEl);

  // ===== UI Elements =====
  const maskedOldEmail = $("#maskedOldEmail");
  const maskedNewEmail = $("#maskedNewEmail");

  const oldEmailHidden = $("#oldEmailHidden");
  const inputOldEmailOtp = $("#oldEmailOtp");
  const inputNewEmail = $("#newEmail");
  const inputNewEmailOtp = $("#newEmailOtp");

  const btnChangeCurrentEmail = $('a[href="#"][data-action="changeEmail"]');
  const btnVerifyOldEmail = $("#btnVerifyOldEmail");
  const btnSubmitNewEmail = $("#btnSubmitNewEmail");
  const btnConfirmChangeEmail = $("#btnConfirmChangeEmail");

  // Phần mật khẩu xác nhận
  const inputCurrentPassword = $("#currentPasswordVerify");
  const btnVerifyPassword = $("#btnVerifyPassword");
  const passwordVerifyMsg = $("#passwordVerifyMessage");
  const passwordSection = $("#passwordVerificationSection");
  const newEmailSection = $("#newEmailSection");

  // ===== Lấy email hiện tại từ giao diện =====
  let currentEmail = $(".me-2.text-truncate").textContent.trim();
  if (oldEmailHidden) oldEmailHidden.value = currentEmail;

  // ===== OTP Manager =====
  const oldEmailOtpManager = window.createOtpManager({
    sendOtpBtn: $("#resendOldEmailOtp"),
    timerLabel: $("#oldEmailOtpTimer"),
    otpInput: inputOldEmailOtp,
    emailInput: oldEmailHidden,
    modalElement: verifyOldEmailModalEl,
    defaultPurpose: "change-email-verify-old",
  });

  const newEmailOtpManager = window.createOtpManager({
    sendOtpBtn: $("#resendNewEmailOtp"),
    timerLabel: $("#newEmailOtpTimer"),
    otpInput: inputNewEmailOtp,
    emailInput: inputNewEmail,
    modalElement: verifyNewEmailModalEl,
    defaultPurpose: "change-email-verify-new",
  });

  // ===== Helper UI =====
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

  // ===== Bước 1: Gửi OTP tới email hiện tại =====
  btnChangeCurrentEmail?.addEventListener("click", async (e) => {
    e.preventDefault();

    setBtnState(btnChangeCurrentEmail, true, "Đang xử lý...");

    if (!currentEmail) {
      toast({
        title: "Không xác định email hiện tại",
        message: "Không thể lấy email người dùng từ giao diện.",
        type: "error",
        duration: 3000,
      });
      setBtnState(btnChangeCurrentEmail, false);
      return;
    }

    const success = await oldEmailOtpManager.sendOtp();

    if (!success) {
      toast({
        title: "Có lỗi xảy ra!",
        message: "Không thể gửi mã OTP tới email hiện tại.",
        type: "error",
        duration: 3000,
      });
      setBtnState(btnChangeCurrentEmail, false);
      return;
    }

    maskedOldEmail.textContent = oldEmailOtpManager.maskEmail(currentEmail);
    verifyOldEmailModal.show();
    setBtnState(btnVerifyOldEmail, false);
    setBtnState(btnChangeCurrentEmail, false);
  });

  // ===== Bước 2: Xác thực OTP email cũ =====
  btnVerifyOldEmail?.addEventListener("click", async (e) => {
    e.preventDefault();

    if (!oldEmailOtpManager.isOtpValid()) {
      oldEmailOtpManager.showMessageForInput(
        inputOldEmailOtp,
        "Vui lòng nhập đúng mã OTP để tiếp tục.",
        "failure"
      );
      return;
    }

    setBtnState(btnVerifyOldEmail, true);
    swapModals(verifyOldEmailModalEl, enterNewEmailModalEl, () => {
      passwordSection.style.display = "";
      newEmailSection.style.display = "none";
      inputCurrentPassword.value = "";
      passwordVerifyMsg.textContent = "";
      btnSubmitNewEmail.disabled = true;
    });
  });

  // ===== Bước 3.1: Kiểm tra mật khẩu trước khi nhập email mới =====
  btnVerifyPassword?.addEventListener("click", async () => {
    const password = inputCurrentPassword.value.trim();
    if (!password || password.length < 8) {
      passwordVerifyMsg.textContent =
        "Vui lòng nhập mật khẩu có ít nhất 8 ký tự.";
      passwordVerifyMsg.style.color = "red";
      return;
    }

    setBtnState(btnVerifyPassword, true, "Đang kiểm tra...");

    const formData = new FormData();
    formData.append("action", "verifyPassword");
    formData.append("password", password);

    try {
      const res = await fetch("/profile", {
        method: "POST",
        body: formData,
      });
      const data = await res.json();

      if (data.success) {
        passwordVerifyMsg.textContent = "Mật khẩu chính xác ✅";
        passwordVerifyMsg.style.color = "green";

        // Ẩn phần nhập mật khẩu, hiển thị phần nhập email mới
        passwordSection.style.display = "none";
        newEmailSection.style.display = "";
        btnSubmitNewEmail.disabled = false;
      } else {
        passwordVerifyMsg.textContent = "Mật khẩu không đúng ❌";
        passwordVerifyMsg.style.color = "red";
      }
    } catch (error) {
      passwordVerifyMsg.textContent = "Lỗi kết nối đến máy chủ.";
      passwordVerifyMsg.style.color = "red";
    } finally {
      setBtnState(btnVerifyPassword, false);
    }
  });

  // ===== Bước 3.2: Gửi otp tới email mới =====
  btnSubmitNewEmail?.addEventListener("click", async (e) => {
    e.preventDefault();

    setBtnState(btnSubmitNewEmail, true, "Đang xử lý...");

    const newEmail = inputNewEmail.value.trim();
    if (!newEmailOtpManager.isValidEmail(newEmail)) {
      newEmailOtpManager.showMessageForInput(
        inputNewEmail,
        "Email không hợp lệ.",
        "failure"
      );
      setBtnState(btnSubmitNewEmail, false);
      return;
    }

    if (newEmail.toLowerCase() === currentEmail.toLowerCase()) {
      newEmailOtpManager.showMessageForInput(
        inputNewEmail,
        "Email mới không được trùng với email hiện tại.",
        "failure"
      );
      setBtnState(btnSubmitNewEmail, false);
      return;
    }

    const success = await newEmailOtpManager.sendOtp();
    if (success) {
      maskedNewEmail.textContent = newEmailOtpManager.maskEmail(newEmail);
      swapModals(enterNewEmailModalEl, verifyNewEmailModalEl);
      setBtnState(btnConfirmChangeEmail, false);
    }

    setBtnState(btnSubmitNewEmail, false);
  });

  // ===== Bước 4: Xác nhận OTP email mới và cập nhật email =====
  btnConfirmChangeEmail?.addEventListener("click", async (e) => {
    e.preventDefault();

    if (!newEmailOtpManager.isOtpValid()) {
      newEmailOtpManager.showMessageForInput(
        inputNewEmailOtp,
        "Vui lòng nhập đúng mã OTP để xác nhận.",
        "failure"
      );
      return;
    }

    const newEmail = inputNewEmail.value.trim();
    setBtnState(btnConfirmChangeEmail, true, "Đang xử lý...");

    const formData = new FormData();
    formData.append("action", "changeEmail");
    formData.append("oldEmail", currentEmail);
    formData.append("newEmail", newEmail);

    try {
      const res = await fetch("/profile", {
        method: "POST",
        body: formData,
      });
      const data = await res.json();

      if (data.success) {
        toast({
          title: "Thành công!",
          message: data.message,
          type: "success",
          duration: 3000,
        });

        swapModals(verifyNewEmailModalEl, null);
        setTimeout(() => {
          window.location.href = "/home";
        }, 3000);
      } else {
        newEmailOtpManager.showMessageForInput(
          inputNewEmailOtp,
          data.message || "Không thể đổi email. Vui lòng thử lại.",
          "failure"
        );
        setBtnState(btnConfirmChangeEmail, false);
      }
    } catch (err) {
      newEmailOtpManager.showMessageForInput(
        inputNewEmailOtp,
        "Lỗi kết nối tới máy chủ.",
        "failure"
      );
      setBtnState(btnConfirmChangeEmail, false);
    }
  });
});

// ======== Đếm ngược thời gian thay đổi email tài khoản =========
document.addEventListener("DOMContentLoaded", function () {
  const countdownEl = document.getElementById("emailChangeDaysLeft");

  if (countdownEl && countdownEl.dataset.unlockTime) {
    let remainingMs = parseInt(countdownEl.dataset.unlockTime, 10);

    // Khởi tạo tooltip
    const badge = countdownEl.closest(".badge");
    let tooltip = null;

    if (badge && badge.hasAttribute("data-bs-toggle")) {
      tooltip = new bootstrap.Tooltip(badge, {
        html: true,
        delay: { show: 200, hide: 100 },
      });
    }

    // Hàm format thời gian
    function formatTime(ms) {
      const seconds = Math.floor(ms / 1000);
      const days = Math.floor(seconds / 86400);
      const hours = Math.floor((seconds % 86400) / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;

      // Format ngắn cho badge
      if (days > 0) {
        return `${days}ngày ${hours}giờ`;
      } else if (hours > 0) {
        return `${hours}giờ ${minutes}phút`;
      } else if (minutes > 0) {
        return `${minutes}phút ${secs}giây`;
      } else {
        return `${secs}giây`;
      }
    }

    // Hàm format đầy đủ cho tooltip
    function formatFullTime(ms) {
      const seconds = Math.floor(ms / 1000);
      const days = Math.floor(seconds / 86400);
      const hours = Math.floor((seconds % 86400) / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;

      let result = [];
      if (days > 0) result.push(`${days} ngày`);
      if (hours > 0) result.push(`${hours} giờ`);
      if (minutes > 0) result.push(`${minutes} phút`);
      if (secs > 0 || result.length === 0) result.push(`${secs}s`);

      return result.join(" ");
    }

    // Cập nhật hiển thị
    function updateDisplay() {
      if (remainingMs <= 0) {
        clearInterval(timer);
        countdownEl.textContent = "0s";

        // Reload trang để hiện nút "Thay Đổi"
        setTimeout(() => {
          location.reload();
        }, 1000);
        return;
      }

      // Cập nhật badge
      countdownEl.textContent = formatTime(remainingMs);

      // Cập nhật tooltip (nếu đang hiện)
      const tooltipCountdown = document.getElementById("tooltipCountdown");
      if (tooltipCountdown) {
        tooltipCountdown.textContent = formatFullTime(remainingMs);
      }

      // Giảm 1 giây
      remainingMs -= 1000;
    }

    // Cập nhật ngay lần đầu
    updateDisplay();

    // Cập nhật mỗi giây
    const timer = setInterval(updateDisplay, 1000);

    // Cập nhật tooltip khi hover
    if (badge) {
      badge.addEventListener("show.bs.tooltip", function () {
        setTimeout(() => {
          const tooltipCountdown = document.getElementById("tooltipCountdown");
          if (tooltipCountdown) {
            tooltipCountdown.textContent = formatFullTime(remainingMs);
          }
        }, 50);
      });
    }
  }
});
