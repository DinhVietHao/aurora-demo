(function () {
  "use strict";

  const NotificationManager = {
    init() {
      this.bindEvents();
      this.updateCounts();
    },

    bindEvents() {
      // Mark all as read
      document
        .getElementById("markAllReadBtn")
        ?.addEventListener("click", () => {
          this.markAllAsRead();
        });

      // Delete all
      document.getElementById("deleteAllBtn")?.addEventListener("click", () => {
        this.deleteAll();
      });

      // Mark single as read
      document.querySelectorAll(".mark-read-btn").forEach((btn) => {
        btn.addEventListener("click", (e) => {
          e.stopPropagation();
          const id = btn.dataset.id;
          this.markAsRead(id);
        });
      });

      // Delete single
      document.querySelectorAll(".delete-btn").forEach((btn) => {
        btn.addEventListener("click", (e) => {
          e.stopPropagation();
          const id = btn.dataset.id;
          this.deleteNotification(id);
        });
      });

      // Click notification item
      document.querySelectorAll(".notification-item").forEach((item) => {
        item.addEventListener("click", () => {
          const id = item.dataset.notificationId;
          this.markAsRead(id);

          // Navigate to detail if has link
          const link = item.querySelector(".notification-action");
          if (link) {
            window.location.href = link.href;
          }
        });
      });
    },

    async markAsRead(notificationId) {
      try {
        const response = await fetch(
          `/api/notifications/${notificationId}/mark-read`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.ok) {
          const item = document.querySelector(
            `[data-notification-id="${notificationId}"]`
          );
          if (item) {
            item.classList.remove("unread");
            item.classList.add("read");
          }
          this.updateCounts();
          this.showToast("success", "Đã đánh dấu đã đọc");
        }
      } catch (error) {
        console.error("Error marking as read:", error);
        this.showToast("error", "Có lỗi xảy ra");
      }
    },

    async markAllAsRead() {
      if (!confirm("Đánh dấu tất cả thông báo đã đọc?")) return;

      try {
        const response = await fetch("/api/notifications/mark-all-read", {
          method: "POST",
        });

        if (response.ok) {
          document
            .querySelectorAll(".notification-item.unread")
            .forEach((item) => {
              item.classList.remove("unread");
              item.classList.add("read");
            });
          this.updateCounts();
          this.showToast("success", "Đã đánh dấu tất cả đã đọc");
        }
      } catch (error) {
        console.error("Error marking all as read:", error);
        this.showToast("error", "Có lỗi xảy ra");
      }
    },

    async deleteNotification(notificationId) {
      if (!confirm("Bạn có chắc muốn xóa thông báo này?")) return;

      try {
        const response = await fetch(`/api/notifications/${notificationId}`, {
          method: "DELETE",
        });

        if (response.ok) {
          const item = document.querySelector(
            `[data-notification-id="${notificationId}"]`
          );
          if (item) {
            item.style.animation = "slideOutRight 0.3s ease-out";
            setTimeout(() => item.remove(), 300);
          }
          this.updateCounts();
          this.showToast("success", "Đã xóa thông báo");
        }
      } catch (error) {
        console.error("Error deleting notification:", error);
        this.showToast("error", "Có lỗi xảy ra");
      }
    },

    async deleteAll() {
      if (!confirm("Bạn có chắc muốn xóa TẤT CẢ thông báo?")) return;

      try {
        const response = await fetch("/api/notifications/delete-all", {
          method: "DELETE",
        });

        if (response.ok) {
          document.querySelectorAll(".notification-item").forEach((item) => {
            item.remove();
          });
          this.updateCounts();
          this.showEmptyState();
          this.showToast("success", "Đã xóa tất cả thông báo");
        }
      } catch (error) {
        console.error("Error deleting all:", error);
        this.showToast("error", "Có lỗi xảy ra");
      }
    },

    updateCounts() {
      const unreadCount = document.querySelectorAll(
        ".notification-item.unread"
      ).length;
      const allCount = document.querySelectorAll(".notification-item").length;

      const unreadBadge = document.getElementById("unreadCount");
      const allBadge = document.getElementById("allCount");

      if (unreadBadge) unreadBadge.textContent = unreadCount;
      if (allBadge) allBadge.textContent = allCount;
    },

    showEmptyState() {
      const container = document.querySelector(".notification-list");
      if (container) {
        container.innerHTML = `
                    <div class="notification-empty">
                        <div class="empty-icon">
                            <i class="bi bi-bell-slash"></i>
                        </div>
                        <h5>Chưa có thông báo</h5>
                        <p class="text-muted">
                            Bạn chưa có thông báo nào. Các thông báo về đơn hàng, 
                            khuyến mãi sẽ hiển thị ở đây.
                        </p>
                        <a href="/home?action=bookstore" class="btn btn-primary mt-3">
                            <i class="bi bi-shop me-2"></i>
                            Khám phá sản phẩm
                        </a>
                    </div>
                `;
      }
    },

    showToast(type, message) {
      if (typeof toast === "function") {
        toast({
          title: type === "success" ? "Thành công" : "Lỗi",
          message: message,
          type: type,
          duration: 3000,
        });
      }
    },
  };

  // Initialize when DOM ready
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () =>
      NotificationManager.init()
    );
  } else {
    NotificationManager.init();
  }
})();

// Slideout animation
const style = document.createElement("style");
style.textContent = `
    @keyframes slideOutRight {
        from {
            opacity: 1;
            transform: translateX(0);
        }
        to {
            opacity: 0;
            transform: translateX(100%);
        }
    }
`;
document.head.appendChild(style);
