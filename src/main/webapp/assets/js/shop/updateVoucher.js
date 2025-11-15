// ==================== updateVoucher.js ====================

// Khi DOM load xong
document.addEventListener("DOMContentLoaded", function () {
  const voucherCodeInput = document.getElementById("voucherCode");

  if (voucherCodeInput) {
    window.originalVoucherCode = voucherCodeInput.value.trim();

    voucherCodeInput.addEventListener("input", function () {
      this.value = this.value.replace(/\s/g, "").toUpperCase();
      validateVoucherCode();
    });
  }

  initializeDiscountType();
  initializeDateValidation();

  const form = document.getElementById("updateVoucherForm");
  if (form) {
    form.addEventListener("submit", function (e) {
      if (!validateForm()) {
        e.preventDefault();
      }
    });
  }
});

// ==================== Kiểm tra mã voucher ====================
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
    showFieldError(field, "Mã voucher chỉ được chứa chữ cái và số");
    return;
  }

  if (value === window.originalVoucherCode) {
    clearFieldMessage(field);
    return;
  }

  try {
    const res = await fetch("/shop/voucher?action=checkVoucherCode", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: "voucherCode=" + encodeURIComponent(value),
    });

    const data = await res.json();

    if (data.success) {
      showFieldSuccess(field, "Mã voucher hợp lệ");
    } else {
      showFieldError(field, "Mã voucher đã tồn tại");
    }
  } catch (err) {
    showFieldError(field, "Không thể kết nối tới máy chủ");
    console.error(err);
  }
};

// ==================== Xử lý loại giảm giá ====================
function initializeDiscountType() {
  const discountTypeSelect = document.getElementById("discountType");
  const discountValueInput = document.getElementById("discountValue");
  const maxDiscountContainer = document
    .getElementById("maxDiscount")
    .closest(".mb-3");

  if (!discountTypeSelect || !discountValueInput) return;

  function toggleDiscountFields() {
    const selectedType = discountTypeSelect.value;

    if (selectedType === "PERCENT") {
      maxDiscountContainer.style.display = "block";
      discountValueInput.placeholder = "Nhập phần trăm giảm (1–100)";
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
    }
  }

  discountValueInput.addEventListener("input", function () {
    const selectedType = discountTypeSelect.value;
    const value = parseFloat(discountValueInput.value);

    if (selectedType === "PERCENT") {
      if (value > 100) discountValueInput.value = 100;
      else if (value < 1 && value !== 0) discountValueInput.value = 1;
    } else if (selectedType === "AMOUNT") {
      if (value < 0) discountValueInput.value = 0;
    }
  });

  discountTypeSelect.addEventListener("change", toggleDiscountFields);
  toggleDiscountFields();
}

// ==================== Kiểm tra ngày ====================
function initializeDateValidation() {
  const startDate = document.getElementById("startDate");
  const endDate = document.getElementById("endDate");

  if (!startDate || !endDate) return;

  startDate.addEventListener("change", validateDates);
  endDate.addEventListener("change", validateDates);
}

function validateDates() {
  const startDate = document.getElementById("startDate");
  const endDate = document.getElementById("endDate");
  const startValue = new Date(startDate.value);
  const endValue = new Date(endDate.value);
  const now = new Date();

  let isValid = true;
  clearFieldMessage(startDate);
  clearFieldMessage(endDate);

  // Nếu startDate readonly (voucher EXPIRED), bỏ qua validate
  if (!startDate.hasAttribute("readonly")) {
    if (!startDate.value) {
      showFieldError(startDate, "Vui lòng chọn ngày bắt đầu");
      isValid = false;
    } else if (startValue < now) {
      showFieldError(startDate, "Ngày bắt đầu không được trong quá khứ");
      isValid = false;
    } else {
      showFieldSuccess(startDate, "Ngày bắt đầu hợp lệ");
    }
  }

  if (!endDate.value) {
    showFieldError(endDate, "Vui lòng chọn ngày kết thúc");
    isValid = false;
  } else if (endValue <= startValue) {
    showFieldError(endDate, "Ngày kết thúc phải sau ngày bắt đầu");
    isValid = false;
  } else {
    showFieldSuccess(endDate, "Ngày kết thúc hợp lệ");
  }

  return isValid;
}

// ==================== Validate form ====================
function validateForm() {
  const requiredFields = [
    "voucherCode",
    "discountType",
    "discountValue",
    "endDate",
  ];

  let isValid = true;

  requiredFields.forEach((id) => {
    const field = document.getElementById(id);
    if (!field || !field.value.trim()) {
      showFieldError(field, "Trường này không được để trống");
      isValid = false;
    }
  });

  if (!validateDates()) isValid = false;

  return isValid;
}

// ==================== Helpers ====================
function showFieldError(field, message) {
  clearFieldMessage(field);
  field.classList.add("is-invalid");

  let feedback = document.createElement("div");
  feedback.className = "invalid-feedback";
  feedback.textContent = message;
  field.parentNode.appendChild(feedback);
}

function showFieldSuccess(field, message) {
  clearFieldMessage(field);
  field.classList.add("is-valid");

  let feedback = document.createElement("div");
  feedback.className = "valid-feedback";
  feedback.textContent = message;
  field.parentNode.appendChild(feedback);
}

function clearFieldMessage(field) {
  if (!field) return;
  field.classList.remove("is-invalid", "is-valid");

  const invalid = field.parentNode.querySelector(".invalid-feedback");
  if (invalid) invalid.remove();

  const valid = field.parentNode.querySelector(".valid-feedback");
  if (valid) valid.remove();
}

// ==================== CẬP NHẬT XEM TRƯỚC VOUCHER ====================
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
  if (discountType === "PERCENT") {
    typeElement.textContent = "%";
    typeElement.className = "voucher-type-preview bg-info";
  } else if (discountType === "AMOUNT") {
    typeElement.textContent = "VNĐ";
    typeElement.className = "voucher-type-preview bg-warning";
  } else {
    typeElement.textContent = "?";
    typeElement.className = "voucher-type-preview bg-secondary";
  }

  const discountElement = document.getElementById("previewDiscount");
  if (discountType === "PERCENT") {
    discountElement.textContent = `${discountValue}%`;
  } else if (discountType === "AMOUNT") {
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

document.addEventListener("DOMContentLoaded", function () {
  updatePreview();
  [
    "voucherCode",
    "voucherDescription",
    "discountType",
    "discountValue",
    "minOrderValue",
    "usageLimit",
    "startDate",
    "endDate",
  ].forEach((id) => {
    const el = document.getElementById(id);
    if (el) {
      el.addEventListener("input", updatePreview);
      el.addEventListener("change", updatePreview);
    }
  });
});
