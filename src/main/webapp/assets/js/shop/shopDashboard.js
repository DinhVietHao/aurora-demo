// Shop Dashboard JavaScript

document.addEventListener("DOMContentLoaded", function () {
  // Toggle sidebar
  const sidebarToggle = document.getElementById("sidebarToggle");
  if (sidebarToggle) {
    sidebarToggle.addEventListener("click", () => {
      document.body.classList.toggle("sb-sidenav-toggled");
    });
  }

  // Initialize dashboard
  initializeDashboard();
});

// Initialize dashboard
function initializeDashboard() {
  animateStatsCards();
}

// Simple card animation
function animateStatsCards() {
  const cards = document.querySelectorAll(".stats-card");
  cards.forEach((card, i) => {
    card.style.opacity = 0;
    card.style.transform = "translateY(20px)";
    setTimeout(() => {
      card.style.transition = "all 0.4s ease";
      card.style.opacity = 1;
      card.style.transform = "translateY(0)";
    }, i * 100);
  });
}

// Refresh dashboard data

// Cập nhật giá trị thống kê
function updateStatsValues(data) {
  // Ví dụ data = { totalRevenue: 12000000, totalOrders: 156, totalProducts: 89, avgRating: 4.8 }
  document.querySelector(".stats-card-primary .stats-value").textContent =
    formatCurrency(data.totalRevenue || 0);
  document.querySelector(".stats-card-warning .stats-value").textContent =
    formatNumber(data.totalOrders || 0);
  document.querySelector(".stats-card-success .stats-value").textContent =
    formatNumber(data.totalProducts || 0);
  document.querySelector(".stats-card-info .stats-value").textContent =
    (data.avgRating || 0).toFixed(1) + "/5";
}

// Loading indicator
function showLoadingIndicator() {
  if (document.getElementById("loadingIndicator")) return;
  const loader = document.createElement("div");
  loader.id = "loadingIndicator";
  loader.className = "position-fixed top-50 start-50 translate-middle";
  loader.style.zIndex = "9999";
  loader.innerHTML = `
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    `;
  document.body.appendChild(loader);
}

function hideLoadingIndicator() {
  const loader = document.getElementById("loadingIndicator");
  if (loader) loader.remove();
}

// Alert message
function showAlert(message, type = "info") {
  const alertDiv = document.createElement("div");
  alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
  alertDiv.style.top = "20px";
  alertDiv.style.right = "20px";
  alertDiv.style.zIndex = "9999";
  alertDiv.style.minWidth = "300px";
  alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
  document.body.appendChild(alertDiv);
  setTimeout(() => alertDiv.remove(), 3000);
}

// Format helpers
function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}

function formatNumber(num) {
  return new Intl.NumberFormat("vi-VN").format(num);
}

// Export utils
window.dashboardUtils = {
  showAlert,
  formatCurrency,
  formatNumber,
};
