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

document.addEventListener("DOMContentLoaded", function () {
  const countdowns = document.querySelectorAll(".countdown");

  countdowns.forEach((el) => {
    const createdAtMillis = parseInt(el.dataset.createdAt);
    const expireTime = createdAtMillis + 3 * 24 * 60 * 60 * 1000;

    function updateCountdown() {
      const now = new Date().getTime();
      const distance = expireTime - now;

      if (distance <= 0) {
        el.textContent = "Đơn hàng đã hết hạn ⏰";
        el.classList.add("text-danger", "fw-bold");
        return;
      }

      const days = Math.floor(distance / (1000 * 60 * 60 * 24));
      const hours = Math.floor(
        (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
      );
      const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distance % (1000 * 60)) / 1000);

      el.textContent = `${days} ngày ${hours}h ${minutes}m ${seconds}s`;
    }

    updateCountdown();
    setInterval(updateCountdown, 1000);
  });
});
