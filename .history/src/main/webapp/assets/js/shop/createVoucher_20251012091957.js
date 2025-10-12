// Promotion Create JavaScript
document.addEventListener("DOMContentLoaded", function () {
  // Initialize create voucher functionality
  initializeCreateVoucher();
  initializeSidebar();
  initializeFormValidation();
  initializePreview();
});

// Initialize main create voucher functionality
function initializeCreateVoucher() {
  console.log("Create Voucher initialized");

  // Set default dates
  setDefaultDates();

  // Initialize discount type change
  const discountType = document.getElementById("discountType");
  if (discountType) {
    discountType.addEventListener("change", handleDiscountTypeChange);
  }
}

// Initialize sidebar functionality
function initializeSidebar() {
  const sidebarToggle = document.getElementById("sidebarToggle");
  const layoutSidenav = document.getElementById("layoutSidenav");

  if (sidebarToggle && layoutSidenav) {
    sidebarToggle.addEventListener("click", function () {
      layoutSidenav.classList.toggle("sb-sidenav-toggled");

      // Store sidebar state in localStorage
      if (layoutSidenav.classList.contains("sb-sidenav-toggled")) {
        localStorage.setItem("sb|sidebar-toggle", "true");
      } else {
        localStorage.removeItem("sb|sidebar-toggle");
      }
    });

    // Restore sidebar state from localStorage
    if (localStorage.getItem("sb|sidebar-toggle") === "true") {
      layoutSidenav.classList.add("sb-sidenav-toggled");
    }
  }
}

// Initialize form validation
function initializeFormValidation() {
  const form = document.getElementById("createVoucherForm");
  const inputs = form.querySelectorAll("input, select, textarea");

  inputs.forEach((input) => {
    input.addEventListener("blur", validateField);
    input.addEventListener("input", clearValidation);
  });

  // Special validation for voucher code
  const voucherCode = document.getElementById("voucherCode");
  if (voucherCode) {
    voucherCode.addEventListener("input", function () {
      // Remove spaces and convert to uppercase
      this.value = this.value.replace(/\s/g, "").toUpperCase();
      validateVoucherCode();
    });
  }

  // Date validation
  const startDate = document.getElementById("startDate");
  const endDate = document.getElementById("endDate");

  if (startDate && endDate) {
    startDate.addEventListener("change", validateDates);
    endDate.addEventListener("change", validateDates);
  }
}

// Initialize preview functionality
function initializePreview() {
  const form = document.getElementById("createVoucherForm");
  const inputs = form.querySelectorAll("input, select, textarea");

  inputs.forEach((input) => {
    input.addEventListener("input", updatePreview);
    input.addEventListener("change", updatePreview);
  });

  // Initial preview update
  updatePreview();
}

// Set default dates
function setDefaultDates() {
  const now = new Date();
  const startDate = document.getElementById("startDate");
  const endDate = document.getElementById("endDate");

  if (startDate) {
    startDate.value = formatDateTimeLocal(now);
  }

  if (endDate) {
    const nextMonth = new Date(now);
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    endDate.value = formatDateTimeLocal(nextMonth);
  }
}

// Format date for datetime-local input
function formatDateTimeLocal(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");

  return `${year}-${month}-${day}T${hours}:${minutes}`;
}

// Handle discount type change
function handleDiscountTypeChange() {
  const discountType = document.getElementById("discountType").value;
  const maxDiscountField = document
    .getElementById("maxDiscount")
    .closest(".mb-3");
  const discountValueLabel = document.querySelector(
    'label[for="discountValue"]'
  );

  if (discountType === "percentage") {
    maxDiscountField.style.display = "block";
    discountValueLabel.textContent = "Giá trị giảm (%) ";
    document.getElementById("discountValue").placeholder = "0-100";
    document.getElementById("discountValue").max = "100";
  } else if (discountType === "fixed") {
    maxDiscountField.style.display = "none";
    discountValueLabel.textContent = "Giá trị giảm (VNĐ) ";
    document.getElementById("discountValue").placeholder = "0";
    document.getElementById("discountValue").removeAttribute("max");
  }

  updatePreview();
}

// Validate individual field
function validateField(event) {
  const field = event.target;
  const value = field.value.trim();

  clearValidation(event);

  switch (field.id) {
    case "voucherCode":
      validateVoucherCode();
      break;
    case "discountType":
      if (!value) {
        showFieldError(field, "Vui lòng chọn loại giảm giá");
      }
      break;
    case "discountValue":
      validateDiscountValue();
      break;
    case "startDate":
    case "endDate":
      validateDates();
      break;
  }
}

// Xóa thông báo cũ (cả valid/invalid feedback)
function clearFieldMessage(field) {
  field.classList.remove("is-invalid", "is-valid");

  const invalid = field.parentNode.querySelector(".invalid-feedback");
  if (invalid) invalid.remove();

  const valid = field.parentNode.querySelector(".valid-feedback");
  if (valid) valid.remove();
}

// Clear field validation
function clearValidation(event) {
  const field = event.target;
  field.classList.remove("is-invalid", "is-valid");

  const feedback = field.parentNode.querySelector(".invalid-feedback");
  if (feedback) {
    feedback.remove();
  }
}

// ✅ Validate voucher code (giữ lại phần gọi backend check trùng)
const validateVoucherCode = async () => {
  const field = document.getElementById("voucherCode");
  const value = field.value.trim();

  clearFieldMessage(field);

  if (!value) {
    showFieldError(field, "Mã voucher không được để trống");
    return;
  }

  if (value.length < 3) {
    showFieldError(field, "Mã voucher phải có ít nhất 3 ký tự");
    return;
  }

  if (!/^[A-Z0-9]+$/.test(value)) {
    showFieldError(
      field,
      "Mã voucher chỉ được chứa chữ cái và số, không có khoảng trắng"
    );
    return;
  }

  try {
    const res = await fetch("/shop/voucher?action=checkVoucherCode", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: "voucherCode=" + value,
    });

    const data = await res.json();

    if (data.success) {
      showFieldSuccess(field, "Mã voucher hợp lệ");
    } else {
      showFieldError(field, "Mã voucher đã tồn tại");
    }
  } catch (err) {
    showFieldError(field, "Lỗi kết nối đến máy chủ");
    console.error(err);
  }
};

// Validate discount value
function validateDiscountValue() {
  const discountType = document.getElementById("discountType").value;
  const field = document.getElementById("discountValue");
  const value = parseFloat(field.value);

  if (!field.value.trim()) {
    showFieldError(field, "Giá trị giảm không được để trống");
    return false;
  }

  if (isNaN(value) || value <= 0) {
    showFieldError(field, "Giá trị giảm phải là số dương");
    return false;
  }

  if (discountType === "percentage" && value > 100) {
    showFieldError(field, "Giá trị giảm theo % không được vượt quá 100%");
    return false;
  }

  showFieldSuccess(field, "Giá trị giảm hợp lệ");
  return true;
}

// Validate dates
function validateDates() {
  const startDate = document.getElementById("startDate");
  const endDate = document.getElementById("endDate");
  const startValue = new Date(startDate.value);
  const endValue = new Date(endDate.value);
  const now = new Date();

  let isValid = true;

  startDate.classList.remove("is-invalid", "is-valid");
  endDate.classList.remove("is-invalid", "is-valid");

  if (!startDate.value) {
    showFieldError(startDate, "Ngày bắt đầu không được để trống");
    isValid = false;
  } else if (startValue < now) {
    showFieldError(startDate, "Ngày bắt đầu không được trong quá khứ");
    isValid = false;
  } else {
    showFieldSuccess(startDate, "Ngày bắt đầu hợp lệ");
  }

  if (!endDate.value) {
    showFieldError(endDate, "Ngày kết thúc không được để trống");
    isValid = false;
  } else if (endValue <= startValue) {
    showFieldError(endDate, "Ngày kết thúc phải sau ngày bắt đầu");
    isValid = false;
  } else {
    showFieldSuccess(endDate, "Ngày kết thúc hợp lệ");
  }

  return isValid;
}

// Show field error
function showFieldError(field, message) {
  clearFieldMessage(field);
  field.classList.remove("is-valid");
  field.classList.add("is-invalid");

  let feedback = field.parentNode.querySelector(".invalid-feedback");
  if (!feedback) {
    feedback = document.createElement("div");
    feedback.className = "invalid-feedback";
    field.parentNode.appendChild(feedback);
  }
  feedback.textContent = message;
}

// Show field success
function showFieldSuccess(field, message) {
  clearFieldMessage(field);
  field.classList.remove("is-invalid");
  field.classList.add("is-valid");

  let feedback = field.parentNode.querySelector(".valid-feedback");
  if (!feedback) {
    feedback = document.createElement("div");
    feedback.className = "valid-feedback";
    field.parentNode.appendChild(feedback);
  }
  feedback.textContent = message;
}

// Validate entire form (client-side only)
function validateForm() {
  const requiredFields = [
    "voucherCode",
    "discountType",
    "discountValue",
    "startDate",
    "endDate",
  ];

  let isValid = true;

  requiredFields.forEach((fieldId) => {
    const field = document.getElementById(fieldId);
    if (!field.value.trim()) {
      showFieldError(field, "Trường này không được để trống");
      isValid = false;
    }
  });

  if (!validateDiscountValue()) isValid = false;
  if (!validateDates()) isValid = false;

  return isValid;
}

// Update preview (animation)
function updatePreview() {
  const voucherCode =
    document.getElementById("voucherCode").value || "VOUCHER_CODE";
  const discountType = document.getElementById("discountType").value;
  const discountValue = document.getElementById("discountValue").value || "0";
  const minOrderValue = document.getElementById("minOrderValue").value || "0";
  const usageLimit = document.getElementById("usageLimit").value;
  const startDate = document.getElementById("startDate").value;
  const endDate = document.getElementById("endDate").value;
  const description =
    document.getElementById("voucherDescription").value || "Mô tả voucher";

  document.getElementById("previewCode").textContent = voucherCode;
  document.getElementById("previewName").textContent = description;

  const typeElement = document.getElementById("previewType");
  if (discountType === "percentage") {
    typeElement.textContent = "%";
    typeElement.className = "voucher-type-preview bg-info";
  } else if (discountType === "fixed") {
    typeElement.textContent = "VNĐ";
    typeElement.className = "voucher-type-preview bg-warning";
  } else {
    typeElement.textContent = "?";
    typeElement.className = "voucher-type-preview bg-secondary";
  }

  const discountElement = document.getElementById("previewDiscount");
  if (discountType === "percentage") {
    discountElement.textContent = `${discountValue}%`;
  } else if (discountType === "fixed") {
    discountElement.textContent = `${parseInt(
      discountValue
    ).toLocaleString()} VNĐ`;
  } else {
    discountElement.textContent = "0%";
  }

  const conditionElement = document.getElementById("previewCondition");
  conditionElement.textContent = `Đơn tối thiểu: ${parseInt(
    minOrderValue
  ).toLocaleString()} VNĐ`;

  const dateElement = document.getElementById("previewDate");
  if (startDate && endDate) {
    const start = new Date(startDate).toLocaleDateString("vi-VN");
    const end = new Date(endDate).toLocaleDateString("vi-VN");
    dateElement.textContent = `${start} - ${end}`;
  } else {
    dateElement.textContent = "Chưa có thời hạn";
  }

  const usageElement = document.getElementById("previewUsage");
  if (usageLimit) {
    usageElement.textContent = `Giới hạn: ${usageLimit} lượt`;
  } else {
    usageElement.textContent = "Không giới hạn";
  }

  const previewCard = document.querySelector(".voucher-preview-card");
  if (previewCard) {
    previewCard.classList.add("updating");
    setTimeout(() => {
      previewCard.classList.remove("updating");
    }, 300);
  }
}

// Export functions
window.PromotionCreate = {
  validateForm,
  updatePreview,
};

document.addEventListener("DOMContentLoaded", function () {
  const discountTypeSelect = document.getElementById("discountType");
  const discountValueInput = document.getElementById("discountValue");
  const maxDiscountContainer = document
    .getElementById("maxDiscount")
    .closest(".mb-3");
  const form = document.getElementById("createVoucherForm");

  function toggleDiscountFields() {
    const selectedType = discountTypeSelect.value;

    if (selectedType === "PERCENT") {
      maxDiscountContainer.style.display = "block";
      discountValueInput.placeholder = "Nhập phần trăm giảm (1–100)";
      discountValueInput.removeAttribute("min");
      discountValueInput.removeAttribute("max");
      discountValueInput.setAttribute("min", "1");
      discountValueInput.setAttribute("max", "100");
    } else if (selectedType === "AMOUNT") {
      maxDiscountContainer.style.display = "none";
      discountValueInput.placeholder = "Nhập số tiền giảm (VNĐ)";
      discountValueInput.removeAttribute("max");
      discountValueInput.removeAttribute("min");
    } else {
      maxDiscountContainer.style.display = "none";
      discountValueInput.placeholder = "0";
      discountValueInput.removeAttribute("max");
      discountValueInput.removeAttribute("min");
    }
  }

  // Validate khi người dùng nhập giá trị giảm
  discountValueInput.addEventListener("input", function () {
    const selectedType = discountTypeSelect.value;
    const value = parseFloat(discountValueInput.value);

    if (selectedType === "PERCENT") {
      if (value > 100) {
        discountValueInput.value = 100;
      } else if (value < 1 && value !== 0) {
        discountValueInput.value = 1;
      }
    } else if (selectedType === "AMOUNT") {
      if (value < 0) {
        discountValueInput.value = 0;
      }
    }
  });

  // Validate khi submit form
  form.addEventListener("submit", function (e) {
    const selectedType = discountTypeSelect.value;
    const value = parseFloat(discountValueInput.value);

    if (selectedType === "PERCENT" && (value < 1 || value > 100)) {
      e.preventDefault();
      alert("Giá trị giảm theo phần trăm phải nằm trong khoảng 1% - 100%");
      discountValueInput.focus();
    }
  });

  // Gọi khi load trang và khi thay đổi loại giảm giá
  toggleDiscountFields();
  discountTypeSelect.addEventListener("change", toggleDiscountFields);
});
