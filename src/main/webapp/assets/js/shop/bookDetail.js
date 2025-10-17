document.addEventListener("DOMContentLoaded", function () {
  // --- PHẦN 1: Chuyển ảnh thumbnail ---
  const thumbnails = document.querySelectorAll(".thumbnail");
  const mainImage = document.querySelector("#mainImage");

  thumbnails.forEach((img) => {
    img.addEventListener("click", () => {
      thumbnails.forEach((active) => active.classList.remove("active"));
      mainImage.src = img.src;
      img.classList.add("active");
    });
  });

  // --- PHẦN 2: Xử lý số lượng và thêm giỏ hàng ---
  const minusBtn = document.querySelector(".btn-minus");
  const plusBtn = document.querySelector(".btn-plus");
  const qtyInput = document.querySelector("#quantity");
  const addToCartBtn = document.querySelector(".add-to-cart-btn");
  const alertBox = document.querySelector(".alert");

  if (qtyInput) {
    // Giảm số lượng
    minusBtn?.addEventListener("click", function () {
      let qty = parseInt(qtyInput.value) || 1;
      if (qty > 1) qtyInput.value = qty - 1;
    });

    // Tăng số lượng
    plusBtn?.addEventListener("click", function () {
      let qty = parseInt(qtyInput.value) || 1;
      qtyInput.value = qty + 1;
    });

    // Xử lý thêm vào giỏ hàng
    addToCartBtn?.addEventListener("click", function () {
      const qty = parseInt(qtyInput.value);
      if (qty <= 0) {
        showAlert("Số lượng không hợp lệ!", "error");
        return;
      }

      // Giả lập thêm vào giỏ (sau này thay bằng Ajax)
      showAlert("Đã thêm vào giỏ hàng!", "success");
    });
  }

  function showAlert(message, type) {
    if (!alertBox) return;
    alertBox.textContent = message;
    alertBox.className = `alert ${type}`;
    alertBox.style.display = "block";

    setTimeout(() => {
      alertBox.style.display = "none";
    }, 2500);
  }
});

document.addEventListener("DOMContentLoaded", function () {
  const desc = document.getElementById("bookDescription");
  const toggleBtn = document.getElementById("toggleDescription");

  if (!desc || !toggleBtn) return;

  // Nếu mô tả ngắn hơn giới hạn -> ẩn nút "xem thêm"
  const limitHeight = 120; // giống trong CSS
  if (desc.scrollHeight <= limitHeight + 10) {
    toggleBtn.style.display = "none";
    return;
  }

  // Mặc định thu gọn
  desc.classList.add("collapsed");

  // Khi click
  toggleBtn.addEventListener("click", function (e) {
    e.preventDefault();
    if (desc.classList.contains("collapsed")) {
      desc.classList.remove("collapsed");
      toggleBtn.textContent = "Thu gọn";
    } else {
      desc.classList.add("collapsed");
      toggleBtn.textContent = "Xem thêm";
      // Cuộn lại lên đầu đoạn mô tả nếu người dùng cuộn quá xa
      desc.scrollIntoView({ behavior: "smooth", block: "center" });
    }
  });
});
