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
