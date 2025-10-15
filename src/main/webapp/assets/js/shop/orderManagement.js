// ==========================
// Order Management Script
// ==========================
document.addEventListener("DOMContentLoaded", function () {
  initializeOrderManagement();
  initializeSidebar();
});

// ==========================
// Hover và nút hành động
// ==========================
function initializeOrderManagement() {
  console.log("Order Management initialized");

  // Hover nhẹ khi rê chuột vào từng đơn
  const orderItems = document.querySelectorAll(".order-item");
  orderItems.forEach((item) => {
    item.addEventListener("mouseenter", function () {
      this.style.transform = "translateY(-2px)";
    });

    item.addEventListener("mouseleave", function () {
      this.style.transform = "translateY(0)";
    });
  });

  // Nút "Xem chi tiết"
  const viewButtons = document.querySelectorAll(".btn-view-order");
  viewButtons.forEach((button) => {
    button.addEventListener("click", function () {
      const orderId = this.dataset.orderid;
      if (orderId) {
        window.location.href = `/shop/order-detail?id=${orderId}`;
      }
    });
  });
}

// ==========================
// Sidebar Toggle
// ==========================
function initializeSidebar() {
  const sidebarToggle = document.getElementById("sidebarToggle");
  const layoutSidenav = document.getElementById("layoutSidenav");

  if (sidebarToggle && layoutSidenav) {
    sidebarToggle.addEventListener("click", function () {
      layoutSidenav.classList.toggle("sb-sidenav-toggled");

      // Lưu trạng thái sidebar
      if (layoutSidenav.classList.contains("sb-sidenav-toggled")) {
        localStorage.setItem("sb|sidebar-toggle", "true");
      } else {
        localStorage.removeItem("sb|sidebar-toggle");
      }
    });

    // Khôi phục trạng thái sidebar khi reload
    if (localStorage.getItem("sb|sidebar-toggle") === "true") {
      layoutSidenav.classList.add("sb-sidenav-toggled");
    }
  }
}
