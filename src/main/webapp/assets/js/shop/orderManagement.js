// ==========================
// Order Management Script
// ==========================

document.addEventListener("DOMContentLoaded", function () {
  console.log("✅ Order Management script loaded");

  initializeOrderManagement();
  initializeSidebar();
  initializeCountdowns();
});

// ==========================
// Hover + Nút hành động
// ==========================
function initializeOrderManagement() {
  console.log("🔹 Initializing Order Management...");

  // Hover nhẹ khi rê chuột vào từng đơn
  document.querySelectorAll(".order-item").forEach((item) => {
    item.addEventListener("mouseenter", () => {
      item.style.transform = "translateY(-2px)";
    });
    item.addEventListener("mouseleave", () => {
      item.style.transform = "translateY(0)";
    });
  });

  // Nút "Xem chi tiết"
  document.querySelectorAll(".btn-view-order").forEach((button) => {
    button.addEventListener("click", () => {
      const orderId = button.dataset.orderid;
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
  console.log("🔹 Initializing Sidebar...");

  const sidebarToggle = document.getElementById("sidebarToggle");
  const layoutSidenav = document.getElementById("layoutSidenav");

  if (!sidebarToggle || !layoutSidenav) return;

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

// ==========================
// Countdown cho đơn hàng chờ
// ==========================
function initializeCountdowns() {
  console.log("🔹 Initializing Countdowns...");

  document.querySelectorAll(".countdown").forEach((el) => {
    const createdAtMillis = parseInt(el.dataset.createdAt, 10);
    if (isNaN(createdAtMillis)) return;

    const expireTime = createdAtMillis + 3 * 24 * 60 * 60 * 1000; 

    function updateCountdown() {
      const now = Date.now();
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
}
