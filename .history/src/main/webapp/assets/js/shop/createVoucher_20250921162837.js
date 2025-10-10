// Promotion Create JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize create voucher functionality
    initializeCreateVoucher();
    initializeSidebar();
    initializeFormValidation();
    initializePreview();
});

// Initialize main create voucher functionality
function initializeCreateVoucher() {
    console.log('Create Voucher initialized');
    
    // Set default dates
    setDefaultDates();
    
    // Initialize form submission
    const form = document.getElementById('createVoucherForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
    
    // Initialize discount type change
    const discountType = document.getElementById('discountType');
    if (discountType) {
        discountType.addEventListener('change', handleDiscountTypeChange);
    }
}

// Initialize sidebar functionality
function initializeSidebar() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    const layoutSidenav = document.getElementById('layoutSidenav');
    
    if (sidebarToggle && layoutSidenav) {
        sidebarToggle.addEventListener('click', function() {
            layoutSidenav.classList.toggle('sb-sidenav-toggled');
            
            // Store sidebar state in localStorage
            if (layoutSidenav.classList.contains('sb-sidenav-toggled')) {
                localStorage.setItem('sb|sidebar-toggle', 'true');
            } else {
                localStorage.removeItem('sb|sidebar-toggle');
            }
        });
        
        // Restore sidebar state from localStorage
        if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
            layoutSidenav.classList.add('sb-sidenav-toggled');
        }
    }
}

// Initialize form validation
function initializeFormValidation() {
    const form = document.getElementById('createVoucherForm');
    const inputs = form.querySelectorAll('input, select, textarea');
    
    inputs.forEach(input => {
        input.addEventListener('blur', validateField);
        input.addEventListener('input', clearValidation);
    });
    
    // Special validation for voucher code
    const voucherCode = document.getElementById('voucherCode');
    if (voucherCode) {
        voucherCode.addEventListener('input', function() {
            // Remove spaces and convert to uppercase
            this.value = this.value.replace(/\s/g, '').toUpperCase();
            validateVoucherCode();
        });
    }
    
    // Date validation
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    
    if (startDate && endDate) {
        startDate.addEventListener('change', validateDates);
        endDate.addEventListener('change', validateDates);
    }
}

// Initialize preview functionality
function initializePreview() {
    const form = document.getElementById('createVoucherForm');
    const inputs = form.querySelectorAll('input, select, textarea');
    
    inputs.forEach(input => {
        input.addEventListener('input', updatePreview);
        input.addEventListener('change', updatePreview);
    });
    
    // Initial preview update
    updatePreview();
}

// Set default dates
function setDefaultDates() {
    const now = new Date();
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    
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
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

// Handle form submission
function handleFormSubmit(event) {
    event.preventDefault();
    
    if (validateForm()) {
        submitVoucher();
    }
}

// Handle discount type change
function handleDiscountTypeChange() {
    const discountType = document.getElementById('discountType').value;
    const maxDiscountField = document.getElementById('maxDiscount').closest('.mb-3');
    const discountValueLabel = document.querySelector('label[for="discountValue"]');
    
    if (discountType === 'percentage') {
        maxDiscountField.style.display = 'block';
        discountValueLabel.textContent = 'Giá trị giảm (%) ';
        document.getElementById('discountValue').placeholder = '0-100';
        document.getElementById('discountValue').max = '100';
    } else if (discountType === 'fixed') {
        maxDiscountField.style.display = 'none';
        discountValueLabel.textContent = 'Giá trị giảm (VNĐ) ';
        document.getElementById('discountValue').placeholder = '0';
        document.getElementById('discountValue').removeAttribute('max');
    }
    
    updatePreview();
}

// Validate individual field
function validateField(event) {
    const field = event.target;
    const value = field.value.trim();
    
    clearValidation(event);
    
    switch (field.id) {
        case 'voucherCode':
            validateVoucherCode();
            break;
        case 'voucherName':
            if (!value) {
                showFieldError(field, 'Tên voucher không được để trống');
            }
            break;
        case 'discountType':
            if (!value) {
                showFieldError(field, 'Vui lòng chọn loại giảm giá');
            }
            break;
        case 'discountValue':
            validateDiscountValue();
            break;
        case 'startDate':
        case 'endDate':
            validateDates();
            break;
    }
}

// Clear field validation
function clearValidation(event) {
    const field = event.target;
    field.classList.remove('is-invalid', 'is-valid');
    
    const feedback = field.parentNode.querySelector('.invalid-feedback');
    if (feedback) {
        feedback.remove();
    }
}

// Validate voucher code
function validateVoucherCode() {
    const field = document.getElementById('voucherCode');
    const value = field.value.trim();
    
    if (!value) {
        showFieldError(field, 'Mã voucher không được để trống');
        return false;
    }
    
    if (value.length < 3) {
        showFieldError(field, 'Mã voucher phải có ít nhất 3 ký tự');
        return false;
    }
    
    if (!/^[A-Z0-9]+$/.test(value)) {
        showFieldError(field, 'Mã voucher chỉ được chứa chữ cái và số, không có khoảng trắng');
        return false;
    }
    
    showFieldSuccess(field, 'Mã voucher hợp lệ');
    return true;
}

// Validate discount value
function validateDiscountValue() {
    const discountType = document.getElementById('discountType').value;
    const field = document.getElementById('discountValue');
    const value = parseFloat(field.value);
    
    if (!field.value.trim()) {
        showFieldError(field, 'Giá trị giảm không được để trống');
        return false;
    }
    
    if (isNaN(value) || value <= 0) {
        showFieldError(field, 'Giá trị giảm phải là số dương');
        return false;
    }
    
    if (discountType === 'percentage' && value > 100) {
        showFieldError(field, 'Giá trị giảm theo % không được vượt quá 100%');
        return false;
    }
    
    showFieldSuccess(field, 'Giá trị giảm hợp lệ');
    return true;
}

// Validate dates
function validateDates() {
    const startDate = document.getElementById('startDate');
    const endDate = document.getElementById('endDate');
    const startValue = new Date(startDate.value);
    const endValue = new Date(endDate.value);
    const now = new Date();
    
    let isValid = true;
    
    // Clear previous validations
    startDate.classList.remove('is-invalid', 'is-valid');
    endDate.classList.remove('is-invalid', 'is-valid');
    
    if (!startDate.value) {
        showFieldError(startDate, 'Ngày bắt đầu không được để trống');
        isValid = false;
    } else if (startValue < now) {
        showFieldError(startDate, 'Ngày bắt đầu không được trong quá khứ');
        isValid = false;
    } else {
        showFieldSuccess(startDate, 'Ngày bắt đầu hợp lệ');
    }
    
    if (!endDate.value) {
        showFieldError(endDate, 'Ngày kết thúc không được để trống');
        isValid = false;
    } else if (endValue <= startValue) {
        showFieldError(endDate, 'Ngày kết thúc phải sau ngày bắt đầu');
        isValid = false;
    } else {
        showFieldSuccess(endDate, 'Ngày kết thúc hợp lệ');
    }
    
    return isValid;
}

// Show field error
function showFieldError(field, message) {
    field.classList.remove('is-valid');
    field.classList.add('is-invalid');
    
    let feedback = field.parentNode.querySelector('.invalid-feedback');
    if (!feedback) {
        feedback = document.createElement('div');
        feedback.className = 'invalid-feedback';
        field.parentNode.appendChild(feedback);
    }
    feedback.textContent = message;
}

// Show field success
function showFieldSuccess(field, message) {
    field.classList.remove('is-invalid');
    field.classList.add('is-valid');
    
    let feedback = field.parentNode.querySelector('.valid-feedback');
    if (!feedback) {
        feedback = document.createElement('div');
        feedback.className = 'valid-feedback';
        field.parentNode.appendChild(feedback);
    }
    feedback.textContent = message;
}

// Validate entire form
function validateForm() {
    const requiredFields = ['voucherCode', 'voucherName', 'discountType', 'discountValue', 'startDate', 'endDate'];
    let isValid = true;
    
    requiredFields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (!field.value.trim()) {
            showFieldError(field, 'Trường này không được để trống');
            isValid = false;
        }
    });
    
    // Validate specific fields
    if (!validateVoucherCode()) isValid = false;
    if (!validateDiscountValue()) isValid = false;
    if (!validateDates()) isValid = false;
    
    return isValid;
}

// Update preview
function updatePreview() {
    const voucherCode = document.getElementById('voucherCode').value || 'VOUCHER_CODE';
    const voucherName = document.getElementById('voucherName').value || 'Tên voucher';
    const discountType = document.getElementById('discountType').value;
    const discountValue = document.getElementById('discountValue').value || '0';
    const minOrderValue = document.getElementById('minOrderValue').value || '0';
    const usageLimit = document.getElementById('usageLimit').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    // Update preview elements
    document.getElementById('previewCode').textContent = voucherCode;
    document.getElementById('previewName').textContent = voucherName;
    
    // Update discount type badge
    const typeElement = document.getElementById('previewType');
    if (discountType === 'percentage') {
        typeElement.textContent = '%';
        typeElement.className = 'voucher-type-preview bg-info';
    } else if (discountType === 'fixed') {
        typeElement.textContent = 'VNĐ';
        typeElement.className = 'voucher-type-preview bg-warning';
    } else {
        typeElement.textContent = '?';
        typeElement.className = 'voucher-type-preview bg-secondary';
    }
    
    // Update discount value
    const discountElement = document.getElementById('previewDiscount');
    if (discountType === 'percentage') {
        discountElement.textContent = `${discountValue}%`;
    } else if (discountType === 'fixed') {
        discountElement.textContent = `${parseInt(discountValue).toLocaleString()} VNĐ`;
    } else {
        discountElement.textContent = '0%';
    }
    
    // Update condition
    const conditionElement = document.getElementById('previewCondition');
    conditionElement.textContent = `Đơn tối thiểu: ${parseInt(minOrderValue).toLocaleString()} VNĐ`;
    
    // Update date range
    const dateElement = document.getElementById('previewDate');
    if (startDate && endDate) {
        const start = new Date(startDate).toLocaleDateString('vi-VN');
        const end = new Date(endDate).toLocaleDateString('vi-VN');
        dateElement.textContent = `${start} - ${end}`;
    } else {
        dateElement.textContent = 'Chưa có thời hạn';
    }
    
    // Update usage limit
    const usageElement = document.getElementById('previewUsage');
    if (usageLimit) {
        usageElement.textContent = `Giới hạn: ${usageLimit} lượt`;
    } else {
        usageElement.textContent = 'Không giới hạn';
    }
    
    // Add update animation
    const previewCard = document.querySelector('.voucher-preview-card');
    previewCard.classList.add('updating');
    setTimeout(() => {
        previewCard.classList.remove('updating');
    }, 300);
}

// Submit voucher
function submitVoucher() {
    const submitButton = document.querySelector('button[type="submit"]');
    
    // Show loading state
    submitButton.disabled = true;
    submitButton.classList.add('loading');
    submitButton.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Đang tạo voucher...';
    
    // Collect form data
    const formData = {
        code: document.getElementById('voucherCode').value,
        name: document.getElementById('voucherName').value,
        description: document.getElementById('voucherDescription').value,
        discountType: document.getElementById('discountType').value,
        discountValue: parseFloat(document.getElementById('discountValue').value),
        maxDiscount: parseFloat(document.getElementById('maxDiscount').value) || null,
        minOrderValue: parseFloat(document.getElementById('minOrderValue').value) || 0,
        usageLimit: parseInt(document.getElementById('usageLimit').value) || null,
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value,
        isActive: document.getElementById('isActive').checked
    };
    
    console.log('Creating voucher with data:', formData);
    
    // Simulate API call
    setTimeout(() => {
        // Reset button state
        submitButton.disabled = false;
        submitButton.classList.remove('loading');
        submitButton.innerHTML = '<i class="bi bi-check-circle me-2"></i>Tạo voucher';
        
        // Show success message
        showSuccessMessage('Tạo voucher thành công!');
        
        // Redirect to promotion management
        setTimeout(() => {
            window.location.href = 'promotionManagement.html';
        }, 2000);
    }, 2000);
}

// Show success message
function showSuccessMessage(message) {
    // Create toast notification
    const toastHTML = `
        <div class="toast align-items-center text-white bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-check-circle me-2"></i>${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    
    // Add toast container if it doesn't exist
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }
    
    // Add toast to container
    toastContainer.insertAdjacentHTML('beforeend', toastHTML);
    
    // Show toast
    const toastElement = toastContainer.lastElementChild;
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
    
    // Remove toast from DOM after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Export functions for external use
window.PromotionCreate = {
    validateForm,
    submitVoucher,
    updatePreview
};
