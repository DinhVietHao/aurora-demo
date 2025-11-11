// View by and Collapse
const moreBtn = document.querySelector("#more");
const gradient = document.querySelector(".gradient");
const bookBody = document.querySelector(".book-description-body");
moreBtn.addEventListener("click", () => {
  if (moreBtn.innerText === "Xem thêm") {
    moreBtn.innerText = "Thu gọn";
    gradient.style.display = "none";
    bookBody.style.maxHeight = bookBody.scrollHeight + "px";
  } else {
    moreBtn.innerText = "Xem thêm";
    gradient.style.display = "block";
    bookBody.style.maxHeight = "250px";
  }
});
// End View by and Collapse

// handle click thumbnail image → change main image
const thumbnails = document.querySelectorAll(".thumbnail");
const mainImage = document.querySelector("#mainImage");

thumbnails.forEach((img) => {
  img.addEventListener("click", () => {
    thumbnails.forEach((active) => active.classList.remove("active"));
    mainImage.src = img.src;
    img.classList.add("active");
  });
});
//END handle click thumbnail image → change main image
// ======================== ADD CART ==========================
const addToCartBtn = document.getElementById("add-to-cart");
addToCartBtn.addEventListener("click", () => {
  addToCartBtn.disabled = true;
  const originalText = addToCartBtn.innerHTML;
  addToCartBtn.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Đang xử lý...
  `;
  const productId = addToCartBtn.dataset.productId;
  fetch("/cart/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: "productId=" + productId,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        toast({
          title: "Thành công!",
          message: data.message,
          type: "success",
          duration: 3000,
        });
        const cartCountBadge = document.getElementById("cartCountBadge");
        if (cartCountBadge) {
          cartCountBadge.innerText = data.cartCount;
        }

        if (data.check === "flashsale_exceeded") {
          const modalEl = document.getElementById("flashSaleModal");
          const flashSaleModal = new bootstrap.Modal(modalEl);
          document.getElementById("flashSaleMessage").textContent =
            data.messageModal;
          flashSaleModal.show();
        }
      } else {
        toast({
          title: data.title,
          message: data.message,
          type: data.type,
          duration: 3000,
        });
        const loginModalEl = document.getElementById("loginModal");
        if (data.user && loginModalEl) {
          setTimeout(() => {
            new bootstrap.Modal(loginModalEl).show();
          }, 3000);
        }
      }
    })
    .catch((error) => {
      console.error(error);
    })
    .finally(() => {
      addToCartBtn.disabled = false;
      addToCartBtn.innerHTML = originalText;
    });
});
// ======================== BUY NOW ==========================
const buyNow = document.getElementById("buyNow");
buyNow.addEventListener("click", () => {
  buyNow.disabled = true;
  const originalText = buyNow.innerHTML;
  buyNow.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Đang xử lý...
  `;
  const productId = buyNow.dataset.productId;
  fetch("/cart/buyNow", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: "productId=" + productId,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        window.location.href = "/checkout";
        const cartCountBadge = document.getElementById("cartCountBadge");
        if (cartCountBadge) {
          cartCountBadge.innerText = data.cartCount;
        }
      } else {
        toast({
          title: data.title,
          message: data.message,
          type: data.type,
          duration: 3000,
        });
        const loginModalEl = document.getElementById("loginModal");
        if (data.user == null && loginModalEl) {
          setTimeout(() => {
            new bootstrap.Modal(loginModalEl).show();
          }, 3000);
        }
      }
    })
    .catch((error) => {
      console.error(error);
    })
    .finally(() => {
      buyNow.disabled = false;
      buyNow.innerHTML = originalText;
    });
});

// Edit review
const editModalEl = document.getElementById("editReviewModal");
if (editModalEl) {
  const editModal = new bootstrap.Modal(editModalEl);
  const editForm = document.getElementById("editReviewForm");
  const editReviewId = document.getElementById("editReviewId");
  const editComment = document.getElementById("editReviewComment");
  const editFileInput = document.getElementById("editReviewImages");
  const editPreviewContainer = document.getElementById("editPreviewImages");
  const submitEditBtn = document.getElementById("submitEditReviewBtn");

  document.body.addEventListener("click", function (e) {
    const button = e.target.closest(".btn-open-edit-review");
    if (button) {
      const reviewId = button.dataset.reviewId;
      const rating = parseInt(button.dataset.rating);
      const comment = button.dataset.comment;

      editReviewId.value = reviewId;
      editComment.value = comment;

      resetEditRatingStars();
      const starInput = editForm.querySelector(
        `input[name="rating"][value="${rating}"]`
      );

      if (starInput) {
        starInput.checked = true;
      }

      editFileInput.value = "";
      editPreviewContainer.innerHTML = "";

      const reviewContainer = button.closest(".col");
      if (reviewContainer) {
        const oldImages = reviewContainer.querySelectorAll(
          ".comment-review img"
        );

        oldImages.forEach((img) => {
          const imgClone = img.cloneNode(true);

          imgClone.removeAttribute("data-bs-toggle");
          imgClone.removeAttribute("data-bs-target");
          imgClone.removeAttribute("data-bs-slide-to");

          imgClone.style.width = "80px";
          imgClone.style.height = "80px";
          imgClone.style.objectFit = "cover";
          imgClone.style.borderRadius = "5px";
          imgClone.style.cursor = "default";

          imgClone.classList.add("old-review-image");
          editPreviewContainer.appendChild(imgClone);
        });
      }
    }
  });

  editFileInput.addEventListener("change", function () {
    editPreviewContainer.innerHTML = "";
    const files = Array.from(this.files);
    if (files.length > 5) {
      toast({
        title: "Lỗi",
        message: "Bạn chỉ được chọn tối đa 5 hình ảnh.",
        type: "error",
      });
      this.value = "";
      return;
    }

    files.forEach((file) => {
      if (file.type.startsWith("image/")) {
        const reader = new FileReader();
        reader.onload = function (e) {
          const img = document.createElement("img");
          img.src = e.target.result;
          img.style.width = "80px";
          img.style.height = "80px";
          img.style.objectFit = "cover";
          img.style.borderRadius = "5px";
          editPreviewContainer.appendChild(img);
        };
        reader.readAsDataURL(file);
      }
    });
  });

  editForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const selectedRating = editForm.querySelector(
      'input[name="rating"]:checked'
    );

    if (!selectedRating) {
      toast({
        title: "Thiếu thông tin",
        message: "Vui lòng chọn số sao.",
        type: "warning",
      });
      return;
    }

    submitEditBtn.disabled = true;
    submitEditBtn.innerHTML =
      '<span class="spinner-border spinner-border-sm"></span> Đang lưu...';

    const formData = new FormData(editForm);
    formData.append("action", "update");

    try {
      const res = await fetch("/review", {
        method: "POST",
        body: formData,
      });

      const data = await res.json();

      if (data.success) {
        toast({
          title: "Thành công",
          message: data.message,
          type: "success",
        });
        editModal.hide();

        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        toast({
          title: "Thất bại",
          message: data.message,
          type: "error",
        });
      }
    } catch (error) {
      toast({
        title: "Lỗi",
        message: "Không thể kết nối máy chủ: " + error.message,
        type: "error",
      });
    } finally {
      submitEditBtn.disabled = false;
      submitEditBtn.innerHTML = "Lưu thay đổi";
    }
  });

  function resetEditRatingStars() {
    const stars = editForm.querySelectorAll('input[name="rating"]');
    stars.forEach((star) => (star.checked = false));
  }
}

const style = document.createElement("style");
style.innerHTML = `
    .rating-stars-edit { 
        display: inline-block; 
        direction: rtl; 
    }
    .rating-stars-edit input[type="radio"] { 
        display: none; 
    }
    .rating-stars-edit label { 
        font-size: 2rem; 
        color: #ccc; 
        cursor: pointer; 
        transition: color 0.2s; 
        padding: 0 0.1em; 
    }
    .rating-stars-edit:not(:hover) input[type="radio"]:checked ~ label,
    .rating-stars-edit:hover input[type="radio"]:hover ~ label,
    .rating-stars-edit input[type="radio"]:checked ~ label,
    .rating-stars-edit input[type="radio"]:hover ~ label { 
        color: #fcc800; 
    }
`;
document.head.appendChild(style);
