// Promotion Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize promotion management functionality
    initializePromotionManagement();
    initializeSidebar();
    initializeFilters();
    initializeActions();
});

// Initialize main promotion management functionality
function initializePromotionManagement() {
    console.log('Promotion Management initialized');
    
    // Add hover effects for stats cards
    const statsCards = document.querySelectorAll('.stats-card');
    statsCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(-5px)';
        });
    });
    
    // Initialize table row hover effects
    const tableRows = document.querySelectorAll('.voucher-table tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('click', function() {
            const voucherCode = this.querySelector('.voucher-code strong').textContent;
            viewVoucher(voucherCode);
        });
    });
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

// Initialize filter functionality
function initializeFilters() {
    const searchInput = document.getElementById('searchVoucher');
    const statusFilter = document.getElementById('statusFilter');
    
    if (searchInput) {
        searchInput.addEventListener('input', debounce(performSearch, 300));
    }
    
    if (statusFilter) {
        statusFilter.addEventListener('change', filterByStatus);
    }
}

// Initialize action buttons
function initializeActions() {
    const createVoucherBtn = document.getElementById('createVoucherBtn');
    
    if (createVoucherBtn) {
        createVoucherBtn.addEventListener('click', createNewVoucher);
    }
}

// Create new voucher
function createNewVoucher() {
    console.log('Creating new voucher...');
    
    // Navigate to create voucher page or show modal
    window.location.href = 'promotionCreate.html';
}

// View voucher details
function viewVoucher(voucherCode) {
    console.log('Viewing voucher:', voucherCode);
    
    // Navigate to voucher details page
    window.location.href = `promotionDetails.html?code=${voucherCode}`;
}

// Edit voucher
function editVoucher(voucherCode) {
    console.log('Editing voucher:', voucherCode);
    
    // Navigate to edit voucher page
    window.location.href = `promotionEdit.html?code=${voucherCode}`;
}

// Delete voucher
function deleteVoucher(voucherCode) {
    console.log('Deleting voucher:', voucherCode);
    
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
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    modal.show();
    
    // Remove modal from DOM when hidden
    document.getElementById('deleteConfirmModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

// Confirm delete voucher
function confirmDelete(voucherCode) {
    console.log('Confirming delete for voucher:', voucherCode);
    
    // Show loading state
    const deleteButton = document.querySelector('#deleteConfirmModal .btn-danger');
    deleteButton.disabled = true;
    deleteButton.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang xóa...';
    
    // Simulate API call
    setTimeout(() => {
        // Remove voucher from table
        removeVoucherFromTable(voucherCode);
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
        modal.hide();
        
        // Show success message
        showSuccessMessage(`Đã xóa voucher ${voucherCode} thành công!`);
        
        // Update statistics
        updateStatistics();
    }, 1500);
}

// Remove voucher from table
function removeVoucherFromTable(voucherCode) {
    const rows = document.querySelectorAll('.voucher-table tbody tr');
    rows.forEach(row => {
        const code = row.querySelector('.voucher-code strong').textContent;
        if (code === voucherCode) {
            row.style.animation = 'fadeOut 0.5s ease-out';
            setTimeout(() => {
                row.remove();
                updateVoucherCount();
            }, 500);
        }
    });
}

// Update voucher count in header
function updateVoucherCount() {
    const remainingRows = document.querySelectorAll('.voucher-table tbody tr').length;
    const headerTitle = document.querySelector('.card-title');
    if (headerTitle) {
        headerTitle.innerHTML = `<i class="bi bi-list-ul me-2"></i>Danh sách voucher (${remainingRows})`;
    }
}

// Perform search
function performSearch() {
    const searchTerm = document.getElementById('searchVoucher').value.toLowerCase();
    const rows = document.querySelectorAll('.voucher-table tbody tr');
    
    rows.forEach(row => {
        const voucherCode = row.querySelector('.voucher-code strong').textContent.toLowerCase();
        const description = row.querySelector('.voucher-code small').textContent.toLowerCase();
        
        const isMatch = voucherCode.includes(searchTerm) || description.includes(searchTerm);
        
        if (isMatch) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
    
    updateVisibleCount();
}

// Filter by status
function filterByStatus() {
    const selectedStatus = document.getElementById('statusFilter').value;
    const rows = document.querySelectorAll('.voucher-table tbody tr');
    
    rows.forEach(row => {
        const statusBadge = row.querySelector('.status-badge');
        const statusText = statusBadge.textContent.toLowerCase();
        
        let shouldShow = true;
        
        if (selectedStatus) {
            switch (selectedStatus) {
                case 'active':
                    shouldShow = statusText.includes('hoạt động');
                    break;
                case 'pending':
                    shouldShow = statusText.includes('chờ') || statusText.includes('nhập');
                    break;
                case 'expired':
                    shouldShow = statusText.includes('hết hạn');
                    break;
                case 'inactive':
                    shouldShow = statusText.includes('ngừng');
                    break;
            }
        }
        
        if (shouldShow) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
    
    updateVisibleCount();
}

// Update visible count
function updateVisibleCount() {
    const visibleRows = document.querySelectorAll('.voucher-table tbody tr:not([style*="display: none"])');
    const headerTitle = document.querySelector('.card-title');
    if (headerTitle) {
        headerTitle.innerHTML = `<i class="bi bi-list-ul me-2"></i>Danh sách voucher (${visibleRows.length})`;
    }
}

// Update statistics
function updateStatistics() {
    // This would typically fetch updated stats from API
    console.log('Updating statistics...');
    
    // Simulate stats update
    const statsCards = document.querySelectorAll('.stats-number');
    statsCards.forEach(card => {
        card.style.animation = 'pulse 0.5s ease-in-out';
    });
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

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Export functions for external use
window.PromotionManagement = {
    createNewVoucher,
    viewVoucher,
    editVoucher,
    deleteVoucher,
    performSearch,
    filterByStatus
};
