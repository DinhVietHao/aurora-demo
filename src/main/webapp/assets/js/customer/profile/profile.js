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
