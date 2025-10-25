class ReviewFilterAjax {
  constructor(productId, contextPath) {
    if (!productId || isNaN(productId)) {
      console.error("Invalid productId:", productId);
      return;
    }

    if (!contextPath) {
      console.error("Context path is required");
      return;
    }

    this.productId = productId;
    this.contextPath = contextPath;
    this.currentPage = 1;
    this.reviewsPerPage = 10;
    this.currentFilter = { rating: "all", filter: "" };
    this.isLoading = false;

    this.initEventListeners();
  }

  // Khởi tạo sự kiện filter
  initEventListeners() {
    const filterContainer = document.querySelector(".comment-filter");
    if (!filterContainer) return;

    filterContainer.addEventListener("click", (e) => {
      if (
        e.target.tagName === "A" &&
        (e.target.dataset.rating || e.target.dataset.filter)
      ) {
        e.preventDefault();
        this.handleFilterClick(e.target);
      }
    });
  }

  // Xử lý khi bấm filter
  handleFilterClick(element) {
    const rating = element.dataset.rating;
    const filter = element.dataset.filter;

    if (rating) {
      this.currentFilter.rating = rating;
      this.currentFilter.filter = "";
    } else if (filter) {
      this.currentFilter.rating = "all";
      this.currentFilter.filter = filter;
    }

    this.currentPage = 1;

    document
      .querySelectorAll(".comment-filter a")
      .forEach((b) => b.classList.remove("active"));
    element.classList.add("active");

    this.fetchReviews();
  }

  // Gọi API lấy danh sách review
  async fetchReviews() {
    if (this.isLoading) return;

    try {
      this.isLoading = true;
      this.showLoading();

      const params = new URLSearchParams({
        productId: this.productId,
        page: this.currentPage,
        limit: this.reviewsPerPage,
        rating: this.currentFilter.rating,
        filter: this.currentFilter.filter,
      });

      const response = await fetch(
        `${this.contextPath}/api/reviews?${params.toString()}`
      );
      if (!response.ok)
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);

      const data = await response.json();
      if (data.success) {
        this.renderReviews(data.reviews);
        this.renderPagination(data.totalPages, data.totalReviews);
      } else {
        throw new Error(data.error || "Unknown error");
      }
    } catch (error) {
      console.error("Error fetching reviews:", error);
      this.showError(`Không thể tải đánh giá: ${error.message}`);
    } finally {
      this.isLoading = false;
    }
  }

  // Render danh sách review
  renderReviews(reviews) {
    const container = document.getElementById("reviews-container");
    if (!container) return;

    if (reviews.length === 0) {
      container.innerHTML = `
        <div class="text-center py-5">
          <p class="text-muted">Không có đánh giá nào phù hợp với bộ lọc.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = reviews.map((r) => this.createReviewHTML(r)).join("");
  }

  // Tạo HTML cho 1 review
  createReviewHTML(review) {
    const avatarUrl = review.user.avatarUrl
      ? `${this.contextPath}/assets/images/avatars/${review.user.avatarUrl}`
      : `${this.contextPath}/assets/images/common/avatar.png`;

    const stars = "★".repeat(review.rating) + "☆".repeat(5 - review.rating);

    const imagesHtml =
      review.images && review.images.length > 0
        ? `
        <div class="d-flex gap-2 comment-review mt-2">
          ${review.images
            .map(
              (img) => `
            <img src="${this.contextPath}/assets/images/reviews/${img.url}" 
                 class="review-image" 
                 alt="review image"
                 style="width: 80px; height: 80px; object-fit: cover; border-radius: 4px; cursor: pointer;">
          `
            )
            .join("")}
        </div>
      `
        : "";

    return `
      <div class="row comment-body">
        <div class="col-auto comment-image">
          <img src="${avatarUrl}" 
               alt="avatar" 
               style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover;"
               onerror="this.src='${
                 this.contextPath
               }/assets/images/common/avatar.png'">
        </div>
        <div class="col">
          <div class="d-flex justify-content-between">
            <h6 class="mb-0 fw-bold">${this.escapeHtml(
              review.user.fullName
            )}</h6>
          </div>
          <small class="text-muted">${this.formatDate(review.createdAt)}</small>
          <div class="text-warning my-1">${stars}</div>
          <p class="mb-1">${this.escapeHtml(review.comment)}</p>
          ${imagesHtml}
        </div>
      </div>
    `;
  }

  // Render phân trang
  renderPagination(totalPages, totalReviews) {
    const container = document.getElementById("pagination-container");
    if (!container) return;

    if (totalReviews <= this.reviewsPerPage) {
      container.innerHTML = "";
      return;
    }

    let html =
      '<nav class="mt-4"><ul class="pagination justify-content-center">';

    if (this.currentPage > 1) {
      html += `<li class="page-item"><a class="page-link" href="#" data-page="${
        this.currentPage - 1
      }">‹</a></li>`;
    }

    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) {
        html += `<li class="page-item ${
          i === this.currentPage ? "active" : ""
        }">
          <a class="page-link" href="#" data-page="${i}">${i}</a></li>`;
      }
    } else {
      html += `<li class="page-item ${this.currentPage === 1 ? "active" : ""}">
        <a class="page-link" href="#" data-page="1">1</a></li>`;

      if (this.currentPage > 3)
        html +=
          '<li class="page-item disabled"><span class="page-link">...</span></li>';

      for (
        let i = Math.max(2, this.currentPage - 1);
        i <= Math.min(totalPages - 1, this.currentPage + 1);
        i++
      ) {
        html += `<li class="page-item ${
          i === this.currentPage ? "active" : ""
        }">
          <a class="page-link" href="#" data-page="${i}">${i}</a></li>`;
      }

      if (this.currentPage < totalPages - 2)
        html +=
          '<li class="page-item disabled"><span class="page-link">...</span></li>';

      html += `<li class="page-item ${
        this.currentPage === totalPages ? "active" : ""
      }">
        <a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a></li>`;
    }

    if (this.currentPage < totalPages) {
      html += `<li class="page-item"><a class="page-link" href="#" data-page="${
        this.currentPage + 1
      }">›</a></li>`;
    }

    html += "</ul></nav>";
    container.innerHTML = html;
    this.attachPaginationListeners();
  }

  // Gắn sự kiện chuyển trang
  attachPaginationListeners() {
    const container = document.getElementById("pagination-container");
    if (!container) return;

    const oldListener = container._paginationListener;
    if (oldListener) container.removeEventListener("click", oldListener);

    const newListener = (e) => {
      const target = e.target.closest("a[data-page]");
      if (target) {
        e.preventDefault();
        const page = parseInt(target.dataset.page);
        if (!isNaN(page) && page !== this.currentPage) {
          this.currentPage = page;
          this.fetchReviews();

          const reviewsSection = document.getElementById("reviews");
          if (reviewsSection) {
            reviewsSection.scrollIntoView({
              behavior: "smooth",
              block: "start",
            });
          }
        }
      }
    };

    container.addEventListener("click", newListener);
    container._paginationListener = newListener;
  }

  // Hiển thị loading
  showLoading() {
    const container = document.getElementById("reviews-container");
    if (container) {
      container.innerHTML = `
        <div class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Đang tải...</span>
          </div>
          <p class="mt-2 text-muted">Đang tải đánh giá...</p>
        </div>
      `;
    }
  }

  // Hiển thị lỗi
  showError(message) {
    const container = document.getElementById("reviews-container");
    if (container) {
      container.innerHTML = `
        <div class="alert alert-danger" role="alert">
          <i class="bi bi-exclamation-triangle-fill"></i> ${this.escapeHtml(
            message
          )}
        </div>
      `;
    }
  }

  // Escape HTML
  escapeHtml(text) {
    if (!text) return "";
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
  }

  // Định dạng ngày theo vi-VN
  formatDate(timestamp) {
    try {
      const date = new Date(timestamp);
      return date.toLocaleString("vi-VN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch {
      return timestamp;
    }
  }
}

// Khởi tạo class khi DOM sẵn sàng
document.addEventListener("DOMContentLoaded", () => {
  const productIdElement = document.getElementById("product-id");
  const contextPathElement = document.getElementById("context-path");

  if (!productIdElement || !contextPathElement) return;

  const productId = productIdElement.value;
  const contextPath = contextPathElement.value;
  if (!productId || !contextPath) return;

  new ReviewFilterAjax(productId, contextPath);
});
