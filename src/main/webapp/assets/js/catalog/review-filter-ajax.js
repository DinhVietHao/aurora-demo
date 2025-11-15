class ReviewFilterAjax {
  constructor(productId, contextPath) {
    if (!productId || isNaN(productId)) {
      console.error("Invalid productId:", productId);
      return;
    }

    if (contextPath === undefined || contextPath === null) {
      console.error("Context path is undefined or null");
      return;
    }

    this.productId = productId;
    this.contextPath = contextPath;
    this.currentPage = 1;
    this.reviewsPerPage = 10;
    this.currentFilter = { rating: "all", filter: "" };
    this.isLoading = false;

    console.log("✅ ReviewFilterAjax initialized with:", {
      productId: this.productId,
      contextPath:
        this.contextPath === "" ? "[root context]" : this.contextPath,
    });

    this.initEventListeners();
  }

  initEventListeners() {
    const filterContainer = document.querySelector(".comment-filter");
    if (!filterContainer) {
      console.error("❌ Filter container not found");
      return;
    }

    filterContainer.addEventListener("click", (e) => {
      const target = e.target.closest(
        "a[data-rating], a[data-filter], button[data-rating], button[data-filter]"
      );

      if (target) {
        e.preventDefault();

        if (this.isLoading) {
          console.log("⏳ Already loading, ignoring click");
          return;
        }

        this.handleFilterClick(target);
      }
    });
  }

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
      .querySelectorAll(".comment-filter a, .comment-filter button")
      .forEach((b) => b.classList.remove("active"));
    element.classList.add("active");

    this.showLoading();
    this.fetchReviews();
  }

  async fetchReviews() {
    if (this.isLoading) {
      console.log("⏳ Already loading, skipping fetch");
      return;
    }

    try {
      this.isLoading = true;

      const params = new URLSearchParams({
        productId: this.productId,
        page: this.currentPage,
        limit: this.reviewsPerPage,
        rating: this.currentFilter.rating,
        filter: this.currentFilter.filter,
      });

      const url = `${this.contextPath}/api/reviews?${params.toString()}`;
      const response = await fetch(url);
      const data = await response.json();

      if (!data || typeof data.success === "undefined") {
        throw new Error("Invalid response format from server");
      }

      if (data.success) {
        this.renderReviews(data.reviews);
        this.renderPagination(data.totalPages, data.totalReviews);
      } else {
        throw new Error(data.message || "Unknown error from API");
      }
    } catch (error) {
      console.error("❌ Error fetching reviews:", error);
      this.showError(`Không thể tải đánh giá: ${error.message}`);
    } finally {
      this.isLoading = false;
    }
  }

  // Render reviews và modals
  renderReviews(reviews) {
    const container = document.getElementById("reviews-container");
    if (!container) {
      console.error("❌ Reviews container not found");
      return;
    }

    if (!reviews || reviews.length === 0) {
      container.innerHTML = `
        <div class="text-center py-5">
          <p class="text-muted">Không có đánh giá nào phù hợp với bộ lọc.</p>
        </div>
      `;
      return;
    }

    // Render review bodies
    container.innerHTML = reviews
      .map((r) => this.createReviewBodyHTML(r))
      .join("");

    // Append modals to body
    reviews.forEach((review) => {
      if (review.images && review.images.length > 0) {
        this.appendReviewModal(review);
      }
    });
  }

  // Create review body HTML (without modal)
  createReviewBodyHTML(review) {
    const avatarUrl = review.user.avatarUrl
      ? `${this.contextPath}/assets/images/avatars/${review.user.avatarUrl}`
      : `${this.contextPath}/assets/images/common/avatar.png`;

    const starsHtml = Array.from({ length: 5 }, (_, i) => {
      return i < review.rating
        ? '<i class="bi bi-star-fill small"></i>'
        : '<i class="bi bi-star small"></i>';
    }).join("");

    const imagesHtml =
      review.images && review.images.length > 0
        ? `
        <div class="d-flex gap-2 comment-review mt-2">
          ${review.images
            .map(
              (img, index) => `
            <img src="${this.contextPath}/assets/images/reviews/${img.url}" 
                 class="review-image" 
                 alt="review image"
                 style="width: 80px; height: 80px; object-fit: cover; border-radius: 4px; cursor: pointer;"
                 data-bs-toggle="modal" 
                 data-bs-target="#reviewModal${review.reviewId}"
                 data-bs-slide-to="${index}">
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
          <div class="text-warning my-1">${starsHtml}</div>
          <p class="mb-1">${this.escapeHtml(review.comment)}</p>
          ${imagesHtml}
        </div>
      </div>
    `;
  }

  // Append modal to document body
  appendReviewModal(review) {
    const oldModal = document.getElementById(`reviewModal${review.reviewId}`);
    if (oldModal) {
      oldModal.remove();
    }

    const modalHtml = `
      <div class="modal fade" id="reviewModal${review.reviewId}" tabindex="-1" 
           aria-labelledby="reviewModalLabel${
             review.reviewId
           }" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-body">
              <div class="d-flex justify-content-end">
                <button type="button" class="btn-close" data-bs-dismiss="modal" 
                        aria-label="Close"></button>
              </div>
              <div id="reviewCarousel${
                review.reviewId
              }" class="carousel slide" data-bs-ride="false">
                <div class="carousel-inner">
                  ${review.images
                    .map(
                      (img, index) => `
                    <div class="carousel-item ${index === 0 ? "active" : ""}">
                      <img src="${this.contextPath}/assets/images/reviews/${
                        img.url
                      }" 
                           class="d-block w-100" alt="ảnh review"
                           style="max-height: 80vh; object-fit: contain;">
                    </div>
                  `
                    )
                    .join("")}
                </div>
                ${
                  review.images.length > 1
                    ? `
                <button class="carousel-control-prev" type="button" 
                        data-bs-target="#reviewCarousel${review.reviewId}" data-bs-slide="prev">
                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" 
                        data-bs-target="#reviewCarousel${review.reviewId}" data-bs-slide="next">
                  <span class="carousel-control-next-icon" aria-hidden="true"></span>
                  <span class="visually-hidden">Next</span>
                </button>
                `
                    : ""
                }
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
    document.body.insertAdjacentHTML("beforeend", modalHtml);
  }

  renderPagination(totalPages, totalReviews) {
    const container = document.getElementById("pagination-container");
    if (!container) return;

    if (totalReviews <= this.reviewsPerPage) {
      container.innerHTML = "";
      return;
    }

    let html =
      '<nav class="mt-4" aria-label="Review pagination"><ul class="pagination justify-content-center">';

    if (this.currentPage > 1) {
      html += `<li class="page-item"><a class="page-link" href="#" data-page="${
        this.currentPage - 1
      }" aria-label="Previous">‹</a></li>`;
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

      if (this.currentPage > 3) {
        html +=
          '<li class="page-item disabled"><span class="page-link">...</span></li>';
      }

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

      if (this.currentPage < totalPages - 2) {
        html +=
          '<li class="page-item disabled"><span class="page-link">...</span></li>';
      }

      html += `<li class="page-item ${
        this.currentPage === totalPages ? "active" : ""
      }">
        <a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a></li>`;
    }

    if (this.currentPage < totalPages) {
      html += `<li class="page-item"><a class="page-link" href="#" data-page="${
        this.currentPage + 1
      }" aria-label="Next">›</a></li>`;
    }

    html += "</ul></nav>";
    container.innerHTML = html;
    this.attachPaginationListeners();
  }

  attachPaginationListeners() {
    const container = document.getElementById("pagination-container");
    if (!container) return;

    const oldListener = container._paginationListener;
    if (oldListener) {
      container.removeEventListener("click", oldListener);
    }

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

  escapeHtml(text) {
    if (!text) return "";
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
  }

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

document.addEventListener("DOMContentLoaded", () => {
  const productIdElement = document.getElementById("product-id");
  const contextPathElement = document.getElementById("context-path");
  if (!productIdElement || !contextPathElement) {
    console.error("❌ Missing product-id or context-path elements");
    return;
  }

  const productId = productIdElement.value;
  const contextPath = contextPathElement.value;
  if (!productId) {
    console.error("❌ Invalid productId value:", productId);
    return;
  }

  console.log("✅ Initializing with:", {
    productId,
    contextPath: contextPath === "" ? "[root context]" : contextPath,
  });

  new ReviewFilterAjax(productId, contextPath);
});
