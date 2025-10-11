// Admin Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Sidebar toggle functionality
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.body.classList.toggle('sb-sidenav-toggled');
        });
    }

    // Initialize dashboard
    initializeDashboard();
    
    // Auto-refresh data every 5 minutes
    setInterval(refreshDashboardData, 300000);
});

// Initialize dashboard components
function initializeDashboard() {
    // Animate stats cards on load
    animateStatsCards();
    
    // Load recent activities
    loadRecentActivities();
    
    // Initialize chart placeholder
    initializeChartPlaceholder();
    
    // Update real-time data
    updateRealTimeData();
}

// Animate stats cards
function animateStatsCards() {
    const statsCards = document.querySelectorAll('.stats-card');
    
    statsCards.forEach((card, index) => {
        setTimeout(() => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(30px)';
            
            setTimeout(() => {
                card.style.transition = 'all 0.6s ease-out';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, 100);
        }, index * 100);
    });
}

// Load recent activities
function loadRecentActivities() {
    const activities = [
        {
            icon: 'bi-cart-plus',
            iconClass: 'activity-icon-success',
            title: 'Đơn hàng mới #DH000234',
            time: '2 phút trước'
        },
        {
            icon: 'bi-exclamation-triangle',
            iconClass: 'activity-icon-warning',
            title: 'Sách "Đắc nhân tâm" sắp hết hàng',
            time: '15 phút trước'
        },
        {
            icon: 'bi-star',
            iconClass: 'activity-icon-danger',
            title: 'Đánh giá "Tôi thấy hoa vàng trên cỏ xanh"',
            time: '1 giờ trước'
        },
        {
            icon: 'bi-person-plus',
            iconClass: 'activity-icon-info',
            title: 'Khách hàng mới đăng ký',
            time: '2 giờ trước'
        },
        {
            icon: 'bi-box-seam',
            iconClass: 'activity-icon-success',
            title: 'Nhập kho 50 cuốn "Sapiens"',
            time: '3 giờ trước'
        }
    ];

    const activityList = document.querySelector('.activity-list');
    if (activityList) {
        // Clear existing activities
        activityList.innerHTML = '';
        
        // Add new activities
        activities.forEach((activity, index) => {
            const activityItem = createActivityItem(activity);
            activityList.appendChild(activityItem);
            
            // Animate activity items
            setTimeout(() => {
                activityItem.style.opacity = '0';
                activityItem.style.transform = 'translateX(20px)';
                
                setTimeout(() => {
                    activityItem.style.transition = 'all 0.4s ease-out';
                    activityItem.style.opacity = '1';
                    activityItem.style.transform = 'translateX(0)';
                }, 50);
            }, index * 100);
        });
    }
}

// Create activity item element
function createActivityItem(activity) {
    const item = document.createElement('div');
    item.className = 'activity-item';
    item.innerHTML = `
        <div class="activity-icon ${activity.iconClass}">
            <i class="${activity.icon}"></i>
        </div>
        <div class="activity-content">
            <div class="activity-title">${activity.title}</div>
            <div class="activity-time">${activity.time}</div>
        </div>
    `;
    return item;
}

// Initialize chart placeholder
function initializeChartPlaceholder() {
    const chartPlaceholder = document.querySelector('.chart-placeholder');
    if (chartPlaceholder) {
        // Add click event to show chart info
        chartPlaceholder.addEventListener('click', function() {
            showAlert('Biểu đồ sẽ được tích hợp với thư viện Chart.js hoặc D3.js', 'info');
        });
        
        // Add hover effect
        chartPlaceholder.style.cursor = 'pointer';
        chartPlaceholder.addEventListener('mouseenter', function() {
            this.style.opacity = '0.8';
        });
        
        chartPlaceholder.addEventListener('mouseleave', function() {
            this.style.opacity = '1';
        });
    }
}

// Update real-time data
function updateRealTimeData() {
    // Simulate real-time updates
    const statsValues = document.querySelectorAll('.stats-value');
    
    statsValues.forEach(value => {
        const originalValue = value.textContent;
        
        // Add pulse effect for updates
        setInterval(() => {
            value.style.transform = 'scale(1.05)';
            setTimeout(() => {
                value.style.transform = 'scale(1)';
            }, 200);
        }, 30000); // Update every 30 seconds
    });
}

// Refresh dashboard data
function refreshDashboardData() {
    console.log('Refreshing dashboard data...');
    
    // Show loading indicator
    showLoadingIndicator();
    
    // Simulate API call
    setTimeout(() => {
        // Update stats (simulate new data)
        updateStatsValues();
        
        // Reload activities
        loadRecentActivities();
        
        // Hide loading indicator
        hideLoadingIndicator();
        
        // Show success message
        showAlert('Dữ liệu đã được cập nhật', 'success');
    }, 2000);
}

// Update stats values
function updateStatsValues() {
    const updates = [
        { selector: '.stats-card-primary .stats-value', value: formatCurrency(15420000 + Math.random() * 1000000) },
        { selector: '.stats-card-warning .stats-value', value: Math.floor(156 + Math.random() * 20) },
        { selector: '.stats-card-success .stats-value', value: Math.floor(89 + Math.random() * 10) },
        { selector: '.stats-card-info .stats-value', value: (4.8 + (Math.random() * 0.2 - 0.1)).toFixed(1) + '/5' }
    ];
    
    updates.forEach(update => {
        const element = document.querySelector(update.selector);
        if (element) {
            element.textContent = update.value;
        }
    });
}

// Show loading indicator
function showLoadingIndicator() {
    const indicator = document.createElement('div');
    indicator.id = 'loadingIndicator';
    indicator.className = 'position-fixed top-50 start-50 translate-middle';
    indicator.style.zIndex = '9999';
    indicator.innerHTML = `
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    `;
    document.body.appendChild(indicator);
}

// Hide loading indicator
function hideLoadingIndicator() {
    const indicator = document.getElementById('loadingIndicator');
    if (indicator) {
        indicator.remove();
    }
}

// Show alert message
function showAlert(message, type = 'info') {
    // Remove existing alerts
    const existingAlerts = document.querySelectorAll('.alert');
    existingAlerts.forEach(alert => alert.remove());

    // Create new alert
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.top = '20px';
    alertDiv.style.right = '20px';
    alertDiv.style.zIndex = '9999';
    alertDiv.style.minWidth = '300px';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    document.body.appendChild(alertDiv);

    // Auto dismiss after 3 seconds
    setTimeout(() => {
        if (alertDiv && alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 3000);
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Format number
function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}

// Handle stats card clicks
document.addEventListener('click', function(e) {
    if (e.target.closest('.stats-card-primary')) {
        showAlert('Chuyển đến báo cáo doanh thu chi tiết', 'info');
    } else if (e.target.closest('.stats-card-warning')) {
        showAlert('Chuyển đến quản lý đơn hàng', 'info');
    } else if (e.target.closest('.stats-card-success')) {
        showAlert('Chuyển đến quản lý sản phẩm', 'info');
    } else if (e.target.closest('.stats-card-info')) {
        showAlert('Chuyển đến quản lý đánh giá', 'info');
    }
});

// Initialize tooltips if Bootstrap is available
if (typeof bootstrap !== 'undefined') {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Export functions for external use
window.dashboardUtils = {
    refreshData: refreshDashboardData,
    showAlert: showAlert,
    formatCurrency: formatCurrency,
    formatNumber: formatNumber
};
