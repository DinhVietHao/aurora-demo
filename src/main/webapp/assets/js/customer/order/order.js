document.addEventListener("DOMContentLoaded", function () {
  const btnCancelOrder = document.querySelectorAll(".btn-cancel-order");
  btnCancelOrder.forEach((btn) => {
    btn.addEventListener("click", function () {
      const orderShopId = btn.dataset.orderShopId;
      document.getElementById("cancelOrderShopId").value = orderShopId;
    });
  });

  const btnConfirmOrder = document.querySelectorAll(".btn-confirm-order");
  btnConfirmOrder.forEach((btn) => {
    btn.addEventListener("click", function () {
      const orderShopId = btn.dataset.orderShopId;
      document.getElementById("confirmOrderShopId").value = orderShopId;
    });
  });

  const btnReturnOrder = document.querySelectorAll(".btn-return-order");
  btnReturnOrder.forEach((btn) => {
    btn.addEventListener("click", function () {
      const orderShopId = btn.dataset.orderShopId;
      document.getElementById("returnOrderShopId").value = orderShopId;
    });
  });

  document.querySelectorAll(".btnRepurchase").forEach((btn) => {
    btn.addEventListener("click", () => {
      btn.disabled = true;
      const originalText = btn.innerHTML;
      btn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Đang xử lý...`;
      const orderShopId = btn.dataset.orderShopId;

      fetch("/order/repurchase", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "orderShopId=" + orderShopId,
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            const cartCountBadge = document.getElementById("cartCountBadge");
            if (cartCountBadge) {
              cartCountBadge.innerText = data.cartCount;
            }
            window.location.href = "/cart";
          } else {
            if (
              data.messages &&
              Array.isArray(data.messages) &&
              data.messages.length
            ) {
              data.messages.forEach((msg) => {
                toast({
                  title: data.title,
                  message: msg,
                  type: data.type,
                  duration: 4000,
                });
              });
            } else {
              toast({
                title: data.title,
                message: data.message,
                type: data.type,
                duration: 4000,
              });
            }
          }
        })
        .catch((err) => {
          toast({
            title: "Lỗi mạng",
            message: "Không thể kết nối tới server.",
            type: "error",
            duration: 4000,
          });
          console.error(err);
        })
        .finally(() => {
          btn.disabled = false;
          btn.innerHTML = originalText;
        });
    });
  });

  const ratingModal = document.getElementById("ratingModal");
  if (!ratingModal) return;

  const modal = new bootstrap.Modal(ratingModal);

  const reviewForm = document.getElementById("reviewForm");
  const reviewOrderItemId = document.getElementById("reviewOrderItemId");
  const reviewProductName = document.getElementById("reviewProductName");
  const reviewProductImage = document.getElementById("reviewProductImage");
  const fileInput = document.getElementById("reviewImages");
  const previewContainer = document.getElementById("previewImages");
  const submitReviewBtn = document.getElementById("submitReviewBtn");

  document.querySelectorAll(".order-card__body").forEach((card) => {
    card.addEventListener("click", function (e) {
      const button = e.target.closest(".btn-open-review");
      if (button) {
        e.stopPropagation();

        const orderItemId = button.dataset.orderItemId;
        const productName = button.dataset.productName;
        const productImageUrl = button.dataset.productImage;

        reviewOrderItemId.value = orderItemId;
        reviewProductName.textContent = productName;
        reviewProductImage.src = productImageUrl;

        reviewForm.reset();
        previewContainer.innerHTML = "";
        resetRatingStars();
        submitReviewBtn.disabled = false;
        submitReviewBtn.innerHTML = "Hoàn thành";

        modal.show();
      } else {
        const orderId = this.dataset.orderId;
        if (orderId) {
          window.location.href = `/order/detail?id=${orderId}`;
        }
      }
    });
  });

  if (fileInput) {
    fileInput.addEventListener("change", function () {
      previewContainer.innerHTML = "";
      const files = Array.from(this.files);

      if (files.length > 5) {
        toast({
          title: "Lỗi",
          message: "Bạn chỉ được chọn tối đa 5 hình ảnh.",
          type: "error",
          duration: 3000,
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
            previewContainer.appendChild(img);
          };
          reader.readAsDataURL(file);
        }
      });
    });
  }

  if (reviewForm) {
    reviewForm.addEventListener("submit", async function (e) {
      e.preventDefault();

      const selectedRating = reviewForm.querySelector(
        'input[name="rating"]:checked'
      );
      if (!selectedRating) {
        toast({
          title: "Thiếu thông tin",
          message: "Vui lòng chọn số sao đánh giá.",
          type: "warning",
          duration: 3000,
        });
        return;
      }

      submitReviewBtn.disabled = true;
      submitReviewBtn.innerHTML =
        '<span class="spinner-border spinner-border-sm"></span> Đang gửi...';

      const formData = new FormData(reviewForm);
      formData.append("action", "create");

      try {
        const res = await fetch("/review", { method: "POST", body: formData });
        const data = await res.json();

        if (data.success) {
          toast({
            title: "Thành công",
            message: data.message,
            type: "success",
            duration: 3000,
          });
          modal.hide();

          const buttonJustClicked = document.querySelector(
            `.btn-open-review[data-order-item-id="${formData.get(
              "orderItemId"
            )}"]`
          );
          if (buttonJustClicked) {
            buttonJustClicked.style.display = "none";
          }
        } else {
          toast({
            title: "Gửi thất bại",
            message: data.message,
            type: "error",
            duration: 3000,
          });
        }
      } catch (error) {
        toast({
          title: "Lỗi",
          message: error.message || "Không thể kết nối đến máy chủ.",
          type: "error",
          duration: 3000,
        });
      } finally {
        submitReviewBtn.disabled = false;
        submitReviewBtn.innerHTML = "Hoàn thành";
      }
    });
  }

  function resetRatingStars() {
    const stars = reviewForm.querySelectorAll('input[name="rating"]');
    stars.forEach((star) => (star.checked = false));
  }
});
