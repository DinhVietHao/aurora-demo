// Promotion Details JavaScript

document.addEventListener("DOMContentLoaded", function () {
  // Initialize promotion details functionality
  initializePromotionDetails();
  initializeSidebar();
  loadVoucherDetails();
});

// Initialize main promotion details functionality
function initializePromotionDetails() {
  console.log("Promotion Details initialized");

  // Add hover effects for cards
  const cards = document.querySelectorAll(".card");
  cards.forEach((card) => {
    card.addEventListener("mouseenter", function () {
      this.style.transform = "translateY(-2px)";
    });

    card.addEventListener("mouseleave", function () {
      this.style.transform = "translateY(0)";
    });
  });
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

// Load voucher details from URL parameter
function loadVoucherDetails() {
  const urlParams = new URLSearchParams(window.location.search);
  const voucherCode = urlParams.get("code");

  if (voucherCode) {
    // Simulate loading voucher data
    loadVoucherData(voucherCode);
  } else {
    // Default data if no code provided
    loadVoucherData("NEWUSER50");
  }
}

// Update preview card
function updatePreviewCard(data) {
  const previewElements = document.querySelectorAll(".voucher-preview-card");
  if (previewElements.length > 0) {
    const previewCard = previewElements[0];

    // Update code
    previewCard.querySelector(".voucher-code-preview").textContent = data.code;

    // Update type
    const typeElement = previewCard.querySelector(".voucher-type-preview");
    if (data.discountType === "percentage") {
      typeElement.textContent = "%";
    } else {
      typeElement.textContent = "VNĐ";
    }

    // Update name
    previewCard.querySelector(".voucher-name-preview").textContent = data.name;

    // Update discount
    const discountElement = previewCard.querySelector(
      ".voucher-discount-preview"
    );
    if (data.discountType === "percentage") {
      discountElement.textContent = `${data.discountValue}%`;
    } else {
      discountElement.textContent = `${data.discountValue.toLocaleString()} VNĐ`;
    }

    // Update condition
    previewCard.querySelector(
      ".voucher-condition-preview"
    ).textContent = `Đơn tối thiểu: ${data.minOrderValue.toLocaleString()} VNĐ`;

    // Update date
    const startDate = new Date(data.startDate).toLocaleDateString("vi-VN");
    const endDate = new Date(data.endDate).toLocaleDateString("vi-VN");
    previewCard.querySelector(
      ".voucher-date-preview"
    ).textContent = `${startDate} - ${endDate}`;

    // Update usage
    const remaining = data.usageLimit - data.usedCount;
    previewCard.querySelector(
      ".voucher-usage-preview"
    ).textContent = `Còn lại: ${remaining.toLocaleString()} lượt`;
  }
}

// Get status text
function getStatusText(status) {
  const statusMap = {
    active: "Hoạt động",
    pending: "Chờ kích hoạt",
    expired: "Hết hạn",
    inactive: "Ngừng hoạt động",
  };
  return statusMap[status] || "Không xác định";
}

// Get status class
function getStatusClass(status) {
  const classMap = {
    active: "bg-success",
    pending: "bg-warning",
    expired: "bg-danger",
    inactive: "bg-secondary",
  };
  return classMap[status] || "bg-secondary";
}

// Format date time
function formatDateTime(dateString) {
  const date = new Date(dateString);
  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

// Copy voucher code to clipboard
function copyVoucherCode() {
  const voucherCode = document.getElementById("voucherCode").textContent;

  // Use modern clipboard API if available
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard
      .writeText(voucherCode)
      .then(() => {
        showSuccessMessage("Đã sao chép mã voucher!");
      })
      .catch((err) => {
        console.error("Failed to copy: ", err);
        fallbackCopyTextToClipboard(voucherCode);
      });
  } else {
    // Fallback for older browsers
    fallbackCopyTextToClipboard(voucherCode);
  }
}

// Fallback copy function
function fallbackCopyTextToClipboard(text) {
  const textArea = document.createElement("textarea");
  textArea.value = text;
  document.body.appendChild(textArea);
  textArea.focus();
  textArea.select();

  try {
    document.execCommand("copy");
    showSuccessMessage("Đã sao chép mã voucher!");
  } catch (err) {
    console.error("Fallback: Oops, unable to copy", err);
    showErrorMessage("Không thể sao chép mã voucher!");
  }

  document.body.removeChild(textArea);
}

// Edit voucher
function editVoucher() {
  const voucherCode = document.getElementById("voucherCode").textContent;
  console.log("Editing voucher:", voucherCode);

  // Navigate to edit page
  window.location.href = `promotionEdit.html?code=${voucherCode}`;
}

// Delete voucher
function deleteVoucher() {
  const voucherCode = document.getElementById("voucherCode").textContent;
  console.log("Deleting voucher:", voucherCode);

  // Show confirmation modal
  showDeleteConfirmation(voucherCode);
}

// Show delete confirmation modal
function showDeleteConfirmation(voucherCode) {
  const modalHTML = `
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deleteConfirmModalLabel">Xác nhận xóa voucher</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="text-center">
                            <i class="bi bi-exclamation-triangle text-warning" style="font-size: 3rem;"></i>
                            <h5 class="mt-3">Bạn có chắc chắn muốn xóa voucher này?</h5>
                            <p class="text-muted">Mã voucher: <strong>${voucherCode}</strong></p>
                            <p class="text-danger">Hành động này không thể hoàn tác!</p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-danger" onclick="confirmDelete('${voucherCode}')">Xóa voucher</button>
                    </div>
                </div>
            </div>
        </div>
    `;

  // Add modal to page
  document.body.insertAdjacentHTML("beforeend", modalHTML);

  // Show modal
  const modal = new bootstrap.Modal(
    document.getElementById("deleteConfirmModal")
  );
  modal.show();

  // Remove modal from DOM when hidden
  document
    .getElementById("deleteConfirmModal")
    .addEventListener("hidden.bs.modal", function () {
      this.remove();
    });
}

// Confirm delete voucher
function confirmDelete(voucherCode) {
  console.log("Confirming delete for voucher:", voucherCode);

  // Show loading state
  const deleteButton = document.querySelector(
    "#deleteConfirmModal .btn-danger"
  );
  deleteButton.disabled = true;
  deleteButton.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang xóa...';

  // Simulate API call
  setTimeout(() => {
    // Close modal
    const modal = bootstrap.Modal.getInstance(
      document.getElementById("deleteConfirmModal")
    );
    modal.hide();

    // Show success message
    showSuccessMessage(`Đã xóa voucher ${voucherCode} thành công!`);

    // Redirect to promotion management
    setTimeout(() => {
      window.location.href = "promotionManagement.html";
    }, 2000);
  }, 1500);
}

// Show success message
function showSuccessMessage(message) {
  showToast(message, "success");
}

// Show error message
function showErrorMessage(message) {
  showToast(message, "danger");
}

// Show toast notification
function showToast(message, type = "success") {
  const toastHTML = `
        <div class="toast align-items-center text-white bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-${
                      type === "success"
                        ? "check-circle"
                        : "exclamation-triangle"
                    } me-2"></i>${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

  // Add toast container if it doesn't exist
  let toastContainer = document.querySelector(".toast-container");
  if (!toastContainer) {
    toastContainer = document.createElement("div");
    toastContainer.className = "toast-container position-fixed top-0 end-0 p-3";
    document.body.appendChild(toastContainer);
  }

  // Add toast to container
  toastContainer.insertAdjacentHTML("beforeend", toastHTML);

  // Show toast
  const toastElement = toastContainer.lastElementChild;
  const toast = new bootstrap.Toast(toastElement);
  toast.show();

  // Remove toast from DOM after it's hidden
  toastElement.addEventListener("hidden.bs.toast", function () {
    this.remove();
  });
}

// Export functions for external use
window.PromotionDetails = {
  copyVoucherCode,
  editVoucher,
  deleteVoucher,
  loadVoucherData,
};
