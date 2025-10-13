// Product Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Sidebar toggle functionality
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.body.classList.toggle('sb-sidenav-toggled');
        });
    }
    // Form elements
    const addProductForm = document.getElementById('addProductForm');
    const addProductModal = document.getElementById('addProductModal');
    const productImagesInput = document.getElementById('productImages');
    const imagePreview = document.getElementById('imagePreview');
    const categoryFilter = document.getElementById('categoryFilter');
    const statusFilter = document.getElementById('statusFilter');
    const searchProduct = document.getElementById('searchProduct');

    // Image preview functionality
    if (productImagesInput) {
        productImagesInput.addEventListener('change', function(e) {
            handleImagePreview(e.target.files);
        });
    }

    // Add product form submission
    if (addProductForm) {
        addProductForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            if (!validateProductForm()) {
                return;
            }

            // Show loading state
            const submitBtn = document.querySelector('#addProductModal .btn-success');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-1"></i>Đang lưu...';
            submitBtn.disabled = true;

            // Simulate API call
            setTimeout(() => {
                // Reset button
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
                
                // Close modal
                const modal = bootstrap.Modal.getInstance(addProductModal);
                modal.hide();
                
                // Reset form
                addProductForm.reset();
                imagePreview.innerHTML = '';
                
                // Show success message
                showAlert('Sản phẩm đã được thêm thành công!', 'success');
                
                // Refresh table (in real app, you would reload data)
                // refreshProductTable();
            }, 2000);
        });
    }

    // Filter functionality
    if (categoryFilter) {
        categoryFilter.addEventListener('change', function() {
            filterProducts();
        });
    }

    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            filterProducts();
        });
    }

    if (searchProduct) {
        let searchTimeout;
        searchProduct.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                filterProducts();
            }, 300);
        });
    }

    // Handle edit and delete buttons
    document.addEventListener('click', function(e) {
        if (e.target.closest('.btn-outline-primary')) {
            const productRow = e.target.closest('tr');
            handleEditProduct(productRow);
        }
        
        if (e.target.closest('.btn-outline-danger')) {
            const productRow = e.target.closest('tr');
            handleDeleteProduct(productRow);
        }
    });

    // Image preview handler
    function handleImagePreview(files) {
        imagePreview.innerHTML = '';
        
        if (files.length > 5) {
            showAlert('Chỉ được chọn tối đa 5 hình ảnh', 'warning');
            return;
        }

        Array.from(files).forEach((file, index) => {
            if (file.size > 2 * 1024 * 1024) {
                showAlert(`File ${file.name} vượt quá 2MB`, 'warning');
                return;
            }

            if (!file.type.startsWith('image/')) {
                showAlert(`File ${file.name} không phải là hình ảnh`, 'warning');
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                const col = document.createElement('div');
                col.className = 'col-md-3 col-sm-4 col-6';
                col.innerHTML = `
                    <div class="image-preview-item">
                        <img src="${e.target.result}" alt="Preview ${index + 1}">
                        <button type="button" class="remove-image" onclick="removeImagePreview(this)">
                            <i class="bi bi-x"></i>
                        </button>
                    </div>
                `;
                imagePreview.appendChild(col);
            };
            reader.readAsDataURL(file);
        });
    }

    // Form validation
    function validateProductForm() {
        let isValid = true;
        const requiredFields = ['productName', 'productPublisher', 'productCategory', 'productPrice', 'productStock'];
        
        requiredFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field && !field.value.trim()) {
                showFieldError(field, 'Trường này là bắt buộc');
                isValid = false;
            } else if (field) {
                clearFieldError(field);
            }
        });

        // Validate price
        const priceField = document.getElementById('productPrice');
        if (priceField && priceField.value.trim()) {
            const price = parseFloat(priceField.value);
            if (price <= 0) {
                showFieldError(priceField, 'Giá bán phải lớn hơn 0');
                isValid = false;
            }
        }

        // Validate stock
        const stockField = document.getElementById('productStock');
        if (stockField && stockField.value.trim()) {
            const stock = parseInt(stockField.value);
            if (stock < 0) {
                showFieldError(stockField, 'Số lượng tồn kho không được âm');
                isValid = false;
            }
        }

        return isValid;
    }

    // Filter products
    function filterProducts() {
        const category = categoryFilter.value;
        const status = statusFilter.value;
        const search = searchProduct.value.toLowerCase();
        
        const rows = document.querySelectorAll('#datatablesSimple tbody tr');
        
        rows.forEach(row => {
            const productName = row.querySelector('.fw-bold').textContent.toLowerCase();
            const productCategory = row.cells[1].textContent.toLowerCase();
            const productStatus = row.querySelector('.badge').textContent.toLowerCase();
            
            let showRow = true;
            
            // Filter by category
            if (category && !productCategory.includes(category.toLowerCase())) {
                showRow = false;
            }
            
            // Filter by status
            if (status) {
                const statusMap = {
                    'active': 'đang bán',
                    'inactive': 'ngừng bán',
                    'out-of-stock': 'hết hàng'
                };
                if (!productStatus.includes(statusMap[status])) {
                    showRow = false;
                }
            }
            
            // Filter by search
            if (search && !productName.includes(search)) {
                showRow = false;
            }
            
            row.style.display = showRow ? '' : 'none';
        });
    }

    // Handle edit product
    function handleEditProduct(row) {
        const productName = row.querySelector('.fw-bold').textContent;
        const author = row.querySelector('.text-muted').textContent;
        const category = row.cells[1].textContent;
        const price = row.cells[2].textContent;
        const stock = row.cells[3].textContent;
        
        // In a real application, you would open an edit modal with pre-filled data
        console.log('Edit product:', { productName, author, category, price, stock });
        showAlert(`Chỉnh sửa sản phẩm: ${productName}`, 'info');
    }

    // Handle delete product
    function handleDeleteProduct(row) {
        const productName = row.querySelector('.fw-bold').textContent;
        
        if (confirm(`Bạn có chắc chắn muốn xóa sản phẩm "${productName}"?`)) {
            // Show loading state
            row.style.opacity = '0.5';
            
            // Simulate API call
            setTimeout(() => {
                row.remove();
                showAlert(`Đã xóa sản phẩm "${productName}"`, 'success');
            }, 1000);
        }
    }

    // Show field error
    function showFieldError(field, message) {
        field.classList.add('is-invalid');
        
        const existingError = field.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }

        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        field.parentNode.appendChild(errorDiv);
    }

    // Clear field error
    function clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    // Show alert message
    function showAlert(message, type = 'info') {
        const existingAlerts = document.querySelectorAll('.alert');
        existingAlerts.forEach(alert => alert.remove());

        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        const mainContent = document.querySelector('main .container-fluid');
        if (mainContent) {
            mainContent.insertBefore(alertDiv, mainContent.firstChild);
        }

        setTimeout(() => {
            if (alertDiv && alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }

    // Real-time form validation
    const formInputs = document.querySelectorAll('#addProductForm input, #addProductForm textarea, #addProductForm select');
    formInputs.forEach(input => {
        input.addEventListener('blur', function() {
            if (this.hasAttribute('required') && !this.value.trim()) {
                showFieldError(this, 'Trường này là bắt buộc');
            } else {
                clearFieldError(this);
            }
        });

        input.addEventListener('input', function() {
            if (this.classList.contains('is-invalid') && this.value.trim()) {
                clearFieldError(this);
            }
        });
    });
});

// Global function to remove image preview
function removeImagePreview(button) {
    const imageItem = button.closest('.col-md-3, .col-sm-4, .col-6');
    if (imageItem) {
        imageItem.remove();
    }
}

// Utility function to format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Utility function to format number
function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}
