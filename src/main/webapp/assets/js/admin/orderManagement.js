// Order Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize order management functionality
    initializeOrderManagement();
    initializeSidebar();
    initializeFilters();
    initializeTabs();
    initializePagination();
});

// Initialize main order management functionality
function initializeOrderManagement() {
    console.log('Order Management initialized');
    
    // Add click handlers for order actions
    const orderActionButtons = document.querySelectorAll('.order-actions .btn');
    orderActionButtons.forEach(button => {
        button.addEventListener('click', handleOrderAction);
    });
    
    // Add hover effects for order items
    const orderItems = document.querySelectorAll('.order-item');
    orderItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
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
    const applyButton = document.querySelector('.btn-primary');
    const resetButton = document.querySelector('.btn-outline-secondary');
    
    if (applyButton) {
        applyButton.addEventListener('click', applyFilters);
    }
    
    if (resetButton) {
        resetButton.addEventListener('click', resetFilters);
    }
    
    // Add real-time search functionality
    const searchInputs = document.querySelectorAll('#orderSearch, #customerSearch');
    searchInputs.forEach(input => {
        input.addEventListener('input', debounce(performSearch, 300));
    });
}

// Initialize tab functionality
function initializeTabs() {
    const tabButtons = document.querySelectorAll('.nav-tabs .nav-link');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabId = this.getAttribute('data-bs-target');
            loadOrdersByStatus(tabId);
        });
    });
}

// Handle order action clicks
function handleOrderAction(event) {
    event.preventDefault();
    const button = event.target;
    const orderItem = button.closest('.order-item');
    const orderId = orderItem.querySelector('.order-code').textContent;
    
    // Add loading state
    button.disabled = true;
    button.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang xử lý...';
    
    // Simulate API call
    setTimeout(() => {
        // Show order details modal or navigate to detail page
        showOrderDetails(orderId);
        
        // Reset button state
        button.disabled = false;
        button.innerHTML = 'Xem chi tiết';
    }, 1000);
}

// Apply filters
function applyFilters() {
    const orderSearch = document.getElementById('orderSearch').value;
    const customerSearch = document.getElementById('customerSearch').value;
    const orderStatus = document.getElementById('orderStatus').value;
    
    const filters = {
        orderCode: orderSearch,
        customerName: customerSearch,
        shippingUnit: orderStatus
    };
    
    console.log('Applying filters:', filters);
    
    // Show loading state
    showLoadingState();
    
    // Simulate API call
    setTimeout(() => {
        filterOrders(filters);
        hideLoadingState();
    }, 1000);
}

// Reset filters
function resetFilters() {
    document.getElementById('orderSearch').value = '';
    document.getElementById('customerSearch').value = '';
    document.getElementById('orderStatus').value = '';
    
    // Reload all orders
    loadAllOrders();
}

// Perform search
function performSearch() {
    const searchTerm = event.target.value.toLowerCase();
    const orderItems = document.querySelectorAll('.order-item');
    
    orderItems.forEach(item => {
        const orderCode = item.querySelector('.order-code').textContent.toLowerCase();
        const customerName = item.querySelector('.order-id').textContent.toLowerCase();
        const productName = item.querySelector('.product-name').textContent.toLowerCase();
        
        const isMatch = orderCode.includes(searchTerm) || 
                       customerName.includes(searchTerm) || 
                       productName.includes(searchTerm);
        
        if (isMatch) {
            item.style.display = 'block';
        } else {
            item.style.display = 'none';
        }
    });
    
    updateOrderCount();
}

// Load orders by status
function loadOrdersByStatus(tabId) {
    console.log('Loading orders for tab:', tabId);
    
    showLoadingState();
    
    // Simulate API call
    setTimeout(() => {
        // Filter orders based on status
        const orderItems = document.querySelectorAll('.order-item');
        orderItems.forEach(item => {
            // Show/hide based on status
            item.style.display = 'block';
        });
        
        hideLoadingState();
        updateOrderCount();
    }, 500);
}

// Show order details
function showOrderDetails(orderId) {
    console.log('Showing details for order:', orderId);

    // Extract order ID from the order code
    const orderCode = orderId.replace('Mã đơn hàng: ', '');

    // Navigate to order details page
    window.location.href = `orderDetails.html?id=${orderCode}`;
}

// Filter orders
function filterOrders(filters) {
    const orderItems = document.querySelectorAll('.order-item');
    let visibleCount = 0;
    
    orderItems.forEach(item => {
        let shouldShow = true;
        
        if (filters.orderCode) {
            const orderCode = item.querySelector('.order-code').textContent;
            if (!orderCode.toLowerCase().includes(filters.orderCode.toLowerCase())) {
                shouldShow = false;
            }
        }
        
        if (filters.customerName) {
            const customerName = item.querySelector('.order-id').textContent;
            if (!customerName.toLowerCase().includes(filters.customerName.toLowerCase())) {
                shouldShow = false;
            }
        }
        
        if (filters.shippingUnit) {
            const shippingInfo = item.querySelector('.shipping-info .text-muted').textContent;
            if (!shippingInfo.toLowerCase().includes(filters.shippingUnit.toLowerCase())) {
                shouldShow = false;
            }
        }
        
        if (shouldShow) {
            item.style.display = 'block';
            visibleCount++;
        } else {
            item.style.display = 'none';
        }
    });
    
    updateOrderCount(visibleCount);
}

// Load all orders
function loadAllOrders() {
    const orderItems = document.querySelectorAll('.order-item');
    orderItems.forEach(item => {
        item.style.display = 'block';
    });
    
    updateOrderCount();
}

// Show loading state
function showLoadingState() {
    const orderItems = document.querySelectorAll('.order-item');
    orderItems.forEach(item => {
        item.classList.add('loading');
    });
}

// Hide loading state
function hideLoadingState() {
    const orderItems = document.querySelectorAll('.order-item');
    orderItems.forEach(item => {
        item.classList.remove('loading');
    });
}

// Update order count
function updateOrderCount(count = null) {
    const countElement = document.querySelector('h5.mb-0');
    if (countElement) {
        if (count === null) {
            const visibleOrders = document.querySelectorAll('.order-item:not([style*="display: none"])');
            count = visibleOrders.length;
        }
        countElement.textContent = `${count} Đơn hàng`;
    }
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

// Initialize pagination
function initializePagination() {
    const paginationLinks = document.querySelectorAll('.pagination .page-link');

    paginationLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const pageItem = this.closest('.page-item');

            // Don't handle disabled or active items
            if (pageItem.classList.contains('disabled') || pageItem.classList.contains('active')) {
                return;
            }

            // Handle ellipsis
            if (this.textContent === '...') {
                return;
            }

            const pageNumber = this.textContent;
            loadPage(pageNumber);
        });
    });
}

// Load specific page
function loadPage(pageNumber) {
    console.log('Loading page:', pageNumber);

    // Update active state
    const paginationItems = document.querySelectorAll('.pagination .page-item');
    paginationItems.forEach(item => {
        item.classList.remove('active');
        if (item.querySelector('.page-link').textContent === pageNumber) {
            item.classList.add('active');
        }
    });

    // Show loading state
    showLoadingState();

    // Simulate API call
    setTimeout(() => {
        // In a real application, you would fetch data for the specific page
        hideLoadingState();

        // Scroll to top of order list
        const orderList = document.querySelector('.order-list');
        if (orderList) {
            orderList.scrollIntoView({ behavior: 'smooth' });
        }
    }, 500);
}

// Update pagination based on current data
function updatePagination(currentPage = 1, totalPages = 95, totalItems = 2373) {
    const pagination = document.querySelector('.pagination');
    if (!pagination) return;

    // Clear existing pagination
    pagination.innerHTML = '';

    // Previous button
    const prevItem = document.createElement('li');
    prevItem.className = currentPage === 1 ? 'page-item disabled' : 'page-item';
    prevItem.innerHTML = `<a class="page-link" href="#" tabindex="${currentPage === 1 ? '-1' : '0'}" ${currentPage === 1 ? 'aria-disabled="true"' : ''}>Trước</a>`;
    pagination.appendChild(prevItem);

    // Page numbers
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    // First page
    if (startPage > 1) {
        const firstItem = document.createElement('li');
        firstItem.className = 'page-item';
        firstItem.innerHTML = '<a class="page-link" href="#">1</a>';
        pagination.appendChild(firstItem);

        if (startPage > 2) {
            const ellipsisItem = document.createElement('li');
            ellipsisItem.className = 'page-item';
            ellipsisItem.innerHTML = '<span class="page-link">...</span>';
            pagination.appendChild(ellipsisItem);
        }
    }

    // Current page range
    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = i === currentPage ? 'page-item active' : 'page-item';
        if (i === currentPage) {
            pageItem.setAttribute('aria-current', 'page');
        }
        pageItem.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        pagination.appendChild(pageItem);
    }

    // Last page
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsisItem = document.createElement('li');
            ellipsisItem.className = 'page-item';
            ellipsisItem.innerHTML = '<span class="page-link">...</span>';
            pagination.appendChild(ellipsisItem);
        }

        const lastItem = document.createElement('li');
        lastItem.className = 'page-item';
        lastItem.innerHTML = `<a class="page-link" href="#">${totalPages}</a>`;
        pagination.appendChild(lastItem);
    }

    // Next button
    const nextItem = document.createElement('li');
    nextItem.className = currentPage === totalPages ? 'page-item disabled' : 'page-item';
    nextItem.innerHTML = `<a class="page-link" href="#" ${currentPage === totalPages ? 'aria-disabled="true"' : ''}>Sau</a>`;
    pagination.appendChild(nextItem);

    // Re-initialize pagination event listeners
    initializePagination();
}

// Export functions for external use
window.OrderManagement = {
    applyFilters,
    resetFilters,
    showOrderDetails,
    loadOrdersByStatus,
    loadPage,
    updatePagination
};
