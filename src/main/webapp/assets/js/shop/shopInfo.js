document.addEventListener("DOMContentLoaded", function () {
  // Sidebar toggle functionality
  const sidebarToggle = document.getElementById("sidebarToggle");
  if (sidebarToggle) {
    sidebarToggle.addEventListener("click", function () {
      document.body.classList.toggle("sb-sidenav-toggled");
    });
  }
  // Form elements
  const shopInfoForm = document.getElementById("shopInfoForm");
  const shopLogoInput = document.getElementById("shopLogoInput");
  const shopLogoPreview = document.getElementById("shopLogoPreview");
  const uploadLogoBtn = document.getElementById("uploadLogoBtn");
  const provinceSelect = document.getElementById("shopProvince");
  const districtSelect = document.getElementById("shopDistrict");

  // Logo upload functionality
  if (uploadLogoBtn) {
    uploadLogoBtn.addEventListener("click", function () {
      shopLogoInput.click();
    });
  }

  // Handle logo file selection
  if (shopLogoInput) {
    shopLogoInput.addEventListener("change", function (e) {
      const file = e.target.files[0];
      if (file) {
        // Validate file size (2MB max)
        if (file.size > 2 * 1024 * 1024) {
          showAlert("Kích thước file không được vượt quá 2MB", "danger");
          return;
        }

        // Validate file type
        if (!file.type.startsWith("image/")) {
          showAlert("Vui lòng chọn file hình ảnh", "danger");
          return;
        }

        // Preview image
        const reader = new FileReader();
        reader.onload = function (e) {
          shopLogoPreview.src = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    });
  }

  // Province/District cascade
  if (provinceSelect) {
    provinceSelect.addEventListener("change", function () {
      const selectedProvince = this.value;
      updateDistricts(selectedProvince);
    });
  }

  // Form submission
  if (shopInfoForm) {
    shopInfoForm.addEventListener("submit", function (e) {
      e.preventDefault();

      // Validate form
      if (!validateShopForm()) {
        return;
      }

      // Show loading state
      const submitBtn = this.querySelector('button[type="submit"]');
      const originalText = submitBtn.innerHTML;
      submitBtn.innerHTML =
        '<i class="bi bi-hourglass-split me-1"></i>Đang lưu...';
      submitBtn.disabled = true;

      // Simulate API call
      setTimeout(() => {
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;

        // Show success message
        showAlert("Thông tin shop đã được cập nhật thành công!", "success");

        // Scroll to top to show message
        window.scrollTo({ top: 0, behavior: "smooth" });
      }, 2000);
    });
  }

  // Form validation
  function validateShopForm() {
    let isValid = true;
    const requiredFields = ["shopName", "shopPhone"];

    requiredFields.forEach((fieldId) => {
      const field = document.getElementById(fieldId);
      if (field && !field.value.trim()) {
        showFieldError(field, "Trường này là bắt buộc");
        isValid = false;
      } else if (field) {
        clearFieldError(field);
      }
    });

    // Validate phone number
    const phoneField = document.getElementById("shopPhone");
    if (phoneField && phoneField.value.trim()) {
      const phoneRegex = /^[0-9]{10,11}$/;
      if (!phoneRegex.test(phoneField.value.trim())) {
        showFieldError(phoneField, "Số điện thoại không hợp lệ");
        isValid = false;
      }
    }

    return isValid;
  }

  // Show field error
  function showFieldError(field, message) {
    field.classList.add("is-invalid");

    // Remove existing error message
    const existingError = field.parentNode.querySelector(".invalid-feedback");
    if (existingError) {
      existingError.remove();
    }

    // Add new error message
    const errorDiv = document.createElement("div");
    errorDiv.className = "invalid-feedback";
    errorDiv.textContent = message;
    field.parentNode.appendChild(errorDiv);
  }

  // Clear field error
  function clearFieldError(field) {
    field.classList.remove("is-invalid");
    const errorDiv = field.parentNode.querySelector(".invalid-feedback");
    if (errorDiv) {
      errorDiv.remove();
    }
  }

  // Update districts based on province
  function updateDistricts(provinceValue) {
    const districts = {
      "ho-chi-minh": [
        { value: "quan-1", text: "Quận 1" },
        { value: "quan-3", text: "Quận 3" },
        { value: "quan-5", text: "Quận 5" },
        { value: "quan-7", text: "Quận 7" },
        { value: "quan-10", text: "Quận 10" },
      ],
      "ha-noi": [
        { value: "ba-dinh", text: "Ba Đình" },
        { value: "hoan-kiem", text: "Hoàn Kiếm" },
        { value: "dong-da", text: "Đống Đa" },
        { value: "hai-ba-trung", text: "Hai Bà Trưng" },
      ],
      "da-nang": [
        { value: "hai-chau", text: "Hải Châu" },
        { value: "thanh-khe", text: "Thanh Khê" },
        { value: "son-tra", text: "Sơn Trà" },
        { value: "ngu-hanh-son", text: "Ngũ Hành Sơn" },
      ],
    };

    // Clear current options
    districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';

    // Add new options
    if (districts[provinceValue]) {
      districts[provinceValue].forEach((district) => {
        const option = document.createElement("option");
        option.value = district.value;
        option.textContent = district.text;
        districtSelect.appendChild(option);
      });
    }
  }

  // Show alert message
  function showAlert(message, type = "info") {
    // Remove existing alerts
    const existingAlerts = document.querySelectorAll(".alert");
    existingAlerts.forEach((alert) => alert.remove());

    // Create new alert
    const alertDiv = document.createElement("div");
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

    // Insert at top of main content
    const mainContent = document.querySelector("main .container-fluid");
    if (mainContent) {
      mainContent.insertBefore(alertDiv, mainContent.firstChild);
    }

    // Auto dismiss after 5 seconds
    setTimeout(() => {
      if (alertDiv && alertDiv.parentNode) {
        alertDiv.remove();
      }
    }, 5000);
  }

  // Handle switch changes
  const switches = document.querySelectorAll(
    '.form-check-input[type="checkbox"]'
  );
  switches.forEach((switchEl) => {
    switchEl.addEventListener("change", function () {
      const label = this.nextElementSibling.textContent;
      const status = this.checked ? "bật" : "tắt";
      console.log(`${label} đã được ${status}`);

      // You can add API calls here to save switch states
      // saveSettingToAPI(this.id, this.checked);
    });
  });

  // Real-time form validation
  const formInputs = document.querySelectorAll(
    "#shopInfoForm input, #shopInfoForm textarea, #shopInfoForm select"
  );
  formInputs.forEach((input) => {
    input.addEventListener("blur", function () {
      if (this.hasAttribute("required") && !this.value.trim()) {
        showFieldError(this, "Trường này là bắt buộc");
      } else {
        clearFieldError(this);
      }
    });

    input.addEventListener("input", function () {
      if (this.classList.contains("is-invalid") && this.value.trim()) {
        clearFieldError(this);
      }
    });
  });

  // Initialize tooltips if Bootstrap is available
  if (typeof bootstrap !== "undefined") {
    const tooltipTriggerList = [].slice.call(
      document.querySelectorAll('[data-bs-toggle="tooltip"]')
    );
    tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl);
    });
  }
});

// Utility function to format phone number
function formatPhoneNumber(phoneNumber) {
  const cleaned = phoneNumber.replace(/\D/g, "");
  const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
  if (match) {
    return match[1] + "-" + match[2] + "-" + match[3];
  }
  return phoneNumber;
}

// Utility function to validate email
function isValidEmail(email) {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}
