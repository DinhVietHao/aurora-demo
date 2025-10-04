// Order Details JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize order details functionality
    initializeOrderDetails();
    initializeSidebar();
    initializeActions();
});

// Initialize main order details functionality
function initializeOrderDetails() {
    console.log('Order Details initialized');
    
    // Get order ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('id');
    
    if (orderId) {
        loadOrderDetails(orderId);
    }
    
    // Initialize tooltips
    initializeTooltips();
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

// Initialize action buttons
function initializeActions() {
    const printButton = document.querySelector('.btn-outline-primary');
    const updateStatusButton = document.querySelector('.btn-primary');
    
    if (printButton) {
        printButton.addEventListener('click', handlePrintOrder);
    }
    
    if (updateStatusButton) {
        updateStatusButton.addEventListener('click', handleUpdateStatus);
    }
    
    // Initialize tracking code copy functionality
    const trackingCode = document.querySelector('.tracking-code');
    if (trackingCode) {
        trackingCode.addEventListener('click', copyTrackingCode);
        trackingCode.style.cursor = 'pointer';
        trackingCode.title = 'Click to copy tracking code';
    }
}

// Initialize tooltips
function initializeTooltips() {
    // Initialize Bootstrap tooltips if available
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
}

// Load order details
function loadOrderDetails(orderId) {
    console.log('Loading order details for:', orderId);
    
    // Show loading state
    showLoadingState();
    
    // Simulate API call
    setTimeout(() => {
        // In a real application, you would fetch order details from API
        const orderData = {
            id: orderId,
            code: '250912440CPRXU',
            status: 'shipping',
            customer: {
                name: 'Gemini2019',
                email: 'gemini2019@email.com',
                phone: '0123 456 789',
                address: '123 Đường ABC, Phường XYZ, Quận 1, TP.HCM'
            },
            shipping: {
                carrier: 'SPX Express',
                trackingCode: 'SP1A3B2C3D4E5F6',
                type: 'Nhanh',
                fee: 25000
            },
            items: [
                {
                    name: 'Cây Thúc Thần Kỳ, Cây Cầm Đồng Trong Tín Điều, và Thú Handmade: Nghệ Thuật Thủ Công Việt Tân Hành',
                    sku: 'BK001234',
                    price: 618400,
                    quantity: 1,
                    total: 618400
                }
            ],
            payment: {
                subtotal: 618400,
                shipping: 25000,
                discount: 0,
                total: 643400,
                method: 'online',
                status: 'paid'
            }
        };
        
        updateOrderDisplay(orderData);
        hideLoadingState();
    }, 1000);
}

// Update order display with data
function updateOrderDisplay(orderData) {
    // Update order code
    const orderCodeElement = document.querySelector('.order-code');
    if (orderCodeElement) {
        orderCodeElement.textContent = `Đơn hàng #${orderData.code}`;
    }
    
    // Update customer info
    updateCustomerInfo(orderData.customer);
    
    // Update shipping info
    updateShippingInfo(orderData.shipping);
    
    // Update payment summary
    updatePaymentSummary(orderData.payment);
    
    console.log('Order details updated successfully');
}

// Update customer information
function updateCustomerInfo(customer) {
    const customerInfoRows = document.querySelectorAll('.customer-info .info-row');
    
    if (customerInfoRows.length >= 4) {
        customerInfoRows[0].querySelector('span').textContent = customer.name;
        customerInfoRows[1].querySelector('span').textContent = customer.email;
        customerInfoRows[2].querySelector('span').textContent = customer.phone;
        customerInfoRows[3].querySelector('span').textContent = customer.address;
    }
}

// Update shipping information
function updateShippingInfo(shipping) {
    const shippingInfoRows = document.querySelectorAll('.shipping-info .info-row');
    
    if (shippingInfoRows.length >= 4) {
        shippingInfoRows[0].querySelector('span').textContent = shipping.carrier;
        shippingInfoRows[1].querySelector('span').textContent = shipping.trackingCode;
        shippingInfoRows[2].querySelector('span').textContent = shipping.type;
        shippingInfoRows[3].querySelector('span').textContent = `${shipping.fee.toLocaleString()} VNĐ`;
    }
}

// Update payment summary
function updatePaymentSummary(payment) {
    const summaryRows = document.querySelectorAll('.summary-row');
    
    if (summaryRows.length >= 4) {
        summaryRows[0].querySelector('span:last-child').textContent = `${payment.subtotal.toLocaleString()} VNĐ`;
        summaryRows[1].querySelector('span:last-child').textContent = `${payment.shipping.toLocaleString()} VNĐ`;
        summaryRows[2].querySelector('span:last-child').textContent = `${payment.discount.toLocaleString()} VNĐ`;
        summaryRows[3].querySelector('strong:last-child').textContent = `${payment.total.toLocaleString()} VNĐ`;
    }
}

// Handle print order
function handlePrintOrder() {
    console.log('Printing order...');
    
    // Add print-specific styles
    document.body.classList.add('printing');
    
    // Print the page
    window.print();
    
    // Remove print-specific styles after printing
    setTimeout(() => {
        document.body.classList.remove('printing');
    }, 1000);
}

// Handle update status
function handleUpdateStatus() {
    console.log('Updating order status...');
    
    // Create status update modal or dropdown
    showStatusUpdateModal();
}

// Show status update modal
function showStatusUpdateModal() {
    // Create modal HTML
    const modalHTML = `
        <div class="modal fade" id="statusUpdateModal" tabindex="-1" aria-labelledby="statusUpdateModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="statusUpdateModalLabel">Cập nhật trạng thái đơn hàng</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="statusUpdateForm">
                            <div class="mb-3">
                                <label for="orderStatus" class="form-label">Trạng thái mới</label>
                                <select class="form-select" id="orderStatus" required>
                                    <option value="">Chọn trạng thái</option>
                                    <option value="confirmed">Đã xác nhận</option>
                                    <option value="preparing">Đang chuẩn bị</option>
                                    <option value="shipping">Đang giao hàng</option>
                                    <option value="delivered">Đã giao hàng</option>
                                    <option value="cancelled">Đã hủy</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="statusNote" class="form-label">Ghi chú (tùy chọn)</label>
                                <textarea class="form-control" id="statusNote" rows="3" placeholder="Nhập ghi chú về việc cập nhật trạng thái..."></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" onclick="submitStatusUpdate()">Cập nhật</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to page
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('statusUpdateModal'));
    modal.show();
    
    // Remove modal from DOM when hidden
    document.getElementById('statusUpdateModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

// Submit status update
function submitStatusUpdate() {
    const status = document.getElementById('orderStatus').value;
    const note = document.getElementById('statusNote').value;
    
    if (!status) {
        alert('Vui lòng chọn trạng thái mới');
        return;
    }
    
    console.log('Updating status to:', status, 'with note:', note);
    
    // Show loading state
    const updateButton = document.querySelector('#statusUpdateModal .btn-primary');
    updateButton.disabled = true;
    updateButton.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang cập nhật...';
    
    // Simulate API call
    setTimeout(() => {
        // Update status in UI
        updateOrderStatus(status);
        
        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('statusUpdateModal'));
        modal.hide();
        
        // Show success message
        showSuccessMessage('Cập nhật trạng thái đơn hàng thành công!');
    }, 1500);
}

// Update order status in UI
function updateOrderStatus(newStatus) {
    const statusBadge = document.querySelector('.order-status');
    const statusMap = {
        'confirmed': { text: 'Đã xác nhận', class: 'bg-info' },
        'preparing': { text: 'Đang chuẩn bị', class: 'bg-warning' },
        'shipping': { text: 'Đang giao hàng', class: 'bg-primary' },
        'delivered': { text: 'Đã giao hàng', class: 'bg-success' },
        'cancelled': { text: 'Đã hủy', class: 'bg-danger' }
    };
    
    if (statusBadge && statusMap[newStatus]) {
        statusBadge.className = `badge order-status ${statusMap[newStatus].class}`;
        statusBadge.textContent = statusMap[newStatus].text;
    }
}

// Copy tracking code to clipboard
function copyTrackingCode() {
    const trackingCode = this.textContent;
    
    navigator.clipboard.writeText(trackingCode).then(() => {
        showSuccessMessage('Đã sao chép mã vận đơn!');
    }).catch(() => {
        // Fallback for older browsers
        const textArea = document.createElement('textarea');
        textArea.value = trackingCode;
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
        showSuccessMessage('Đã sao chép mã vận đơn!');
    });
}

// Show loading state
function showLoadingState() {
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.classList.add('loading');
    });
}

// Hide loading state
function hideLoadingState() {
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.classList.remove('loading');
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

// Export functions for external use
window.OrderDetails = {
    loadOrderDetails,
    handlePrintOrder,
    handleUpdateStatus,
    copyTrackingCode
};
