document.addEventListener("DOMContentLoaded", function () {
  const joinButtons = document.querySelectorAll(".btn-join-flashsale");
  const registerModalEl = document.getElementById("flashsaleRegisterModal");
  const registerModal = registerModalEl
    ? new bootstrap.Modal(registerModalEl)
    : null;
  let selectedEventId = null;

  // =========================
  // 1️⃣ OPEN MODAL
  // =========================
  joinButtons.forEach((btn) => {
    btn.addEventListener("click", function () {
      selectedEventId = this.dataset.id;
      if (registerModal) registerModal.show();

      // 🟢 Gọi servlet để lấy danh sách sản phẩm đang hoạt động của shop
      fetch(`/shop/flashSale?action=getActiveProducts`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      })
        .then((res) => {
          if (!res.ok) throw new Error("Không thể tải danh sách sản phẩm");
          return res.json();
        })
        .then((data) => {
          console.log("✅ Danh sách sản phẩm hoạt động:", data);

          const productList = document.getElementById("productList");
          if (!productList) return;
          productList.innerHTML = "";

          if (!data || data.length === 0) {
            productList.innerHTML =
              '<li class="list-group-item text-muted text-center">Không có sản phẩm hoạt động</li>';
            return;
          }

          // 🟢 Duyệt danh sách sản phẩm để render
          data.forEach((p) => {
            const li = document.createElement("button");
            li.className =
              "list-group-item list-group-item-action d-flex align-items-center gap-3";
            li.dataset.id = p.productId;
            li.dataset.name = p.productName;
            li.dataset.quantity = p.quantity;
            li.dataset.category = p.categoryNames?.split(",")[0]?.trim() || "";

            const firstCategory =
              p.categoryNames && p.categoryNames.includes(",")
                ? p.categoryNames.split(",")[0].trim() + ", ..."
                : p.categoryNames || "Không phân loại";

            li.innerHTML = `
              <img src="http://localhost:8080/assets/images/catalog/products/${
                p.imageUrl || "no-image.jpg"
              }"
                  alt="${p.productName}"
                  class="rounded border flex-shrink-0"
                  style="width: 64px; height: 74px; object-fit: cover;">
              <div class="flex-grow-1 text-start">
                <div class="fw-semibold">${p.productName}</div>
                <small class="text-muted">${p.salePrice.toLocaleString()} đ</small>
              </div>
              <div class="text-muted">Số lượng: ${p.quantity}  </div>
              <span class="badge bg-light text-dark border">${firstCategory}</span>
            `;

            productList.appendChild(li);
          });

          // Kích hoạt filter lại sau khi load danh sách
          if (typeof filterProducts === "function") {
            filterProducts();
          }
        })
        .catch((err) => {
          console.error("❌ Lỗi khi tải danh sách sản phẩm:", err);
          const productList = document.getElementById("productList");
          if (productList)
            productList.innerHTML =
              '<li class="list-group-item text-danger text-center">Lỗi khi tải dữ liệu sản phẩm</li>';
        });
    });
  });

  // =========================
  // 3️⃣ DROPDOWN FILTER / SEARCH
  // =========================
  const dropdownBtn = document.getElementById("dropdownProductBtn");
  const dropdownMenu = document.getElementById("productDropdownMenu");
  const searchInput = document.getElementById("searchProduct");
  const filterSelect = document.getElementById("filterCategory");
  const selectedText = document.getElementById("selectedProductText");
  const productList = document.getElementById("productList");

  // Hidden input để lưu productId
  let hiddenSelectInput = document.getElementById("flashsaleProductSelect");
  if (!hiddenSelectInput) {
    hiddenSelectInput = document.createElement("input");
    hiddenSelectInput.type = "hidden";
    hiddenSelectInput.id = "flashsaleProductSelect";
    const form = document.getElementById("flashsaleRegisterForm");
    if (form) form.appendChild(hiddenSelectInput);
  }

  let selectedProductId = null;
  window._selectedProductId = selectedProductId;

  function normalizeText(s) {
    if (!s) return "";
    return s
      .toString()
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .trim();
  }

  // =========================
  // Toggle dropdown
  // =========================
  if (dropdownBtn && dropdownMenu) {
    dropdownBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      dropdownMenu.classList.toggle("show");
      // Khi mở thì tự focus vào ô tìm kiếm
      if (dropdownMenu.classList.contains("show") && searchInput) {
        setTimeout(() => searchInput.focus(), 100);
      }
    });
  }

  // Click ra ngoài -> đóng dropdown
  document.addEventListener("click", (e) => {
    if (!dropdownBtn.contains(e.target) && !dropdownMenu.contains(e.target)) {
      dropdownMenu.classList.remove("show");
    }
  });

  // =========================
  // Hàm Lọc sản phẩm
  // =========================
  function filterProducts() {
    if (!productList) return;
    const keyword = normalizeText(searchInput?.value);
    const category = filterSelect?.value || "";

    const items = productList.querySelectorAll(".list-group-item");
    items.forEach((item) => {
      const name = normalizeText(item.dataset.name);
      const cat = item.dataset.category || "";
      const matchName = !keyword || name.includes(keyword);
      const matchCategory = !category || cat === category;
      item.style.display = matchName && matchCategory ? "" : "none";
    });
  }

  if (searchInput) searchInput.addEventListener("input", filterProducts);
  if (filterSelect) filterSelect.addEventListener("change", filterProducts);

  // =========================
  // Chọn sản phẩm
  // =========================
  if (productList) {
    productList.addEventListener("click", function (e) {
      const btn = e.target.closest(".list-group-item");
      if (!btn || btn.style.display === "none") return;

      e.preventDefault();
      e.stopPropagation();

      selectedProductId = btn.dataset.id;
      window._selectedProductId = selectedProductId;

      const img = btn.querySelector("img");
      const name = btn.dataset.name;
      const quantity = btn.dataset.quantity;
      const price = btn.querySelector("small")?.innerText || "";
      const category = btn.querySelector(".badge")?.innerText || "";
      const imgSrc = img ? img.src : "";

      // ✅ Cập nhật hiển thị trên nút chọn
      selectedText.innerHTML = `
      <div class="d-flex w-100 align-items-center">
        ${
          imgSrc
            ? `<img src="${imgSrc}" alt="${name}" class="rounded border flex-shrink-0 me-2" 
                style="object-fit: cover; width: 64px; height: 74px;">`
            : ""
        }
        <div class="d-flex justify-content-between align-items-center w-100">
          <div class="d-flex flex-column text-start text-truncate">
            <span class="fw-semibold text-truncate">${name}</span>
            <small class="text-muted text-truncate">${price}</small>
          </div>
          <small class="text-muted flex-shrink-0 ms-2">Số lượng: ${quantity}</small>
        </div>
      </div>
    `;

      hiddenSelectInput.value = selectedProductId;

      // Highlight item đã chọn
      productList.querySelectorAll(".list-group-item").forEach((it) => {
        it.classList.remove("active");
      });
      btn.classList.add("active");

      // Đóng dropdown
      dropdownMenu.classList.remove("show");

      console.log("✅ Sản phẩm đã chọn:", {
        id: selectedProductId,
        name,
        price,
        category,
      });

      // ===============================
      // 🧩 VALIDATION INPUT GIÁ & SỐ LƯỢNG
      // ===============================
      const quantityInput = document.getElementById("flashsaleQuantityInput");
      const priceInput = document.getElementById("flashsalePriceInput");

      const currentQuantity = parseInt(quantity, 10);
      const currentPrice = parseFloat(price.replace(/[^\d]/g, ""));
      let hasSelectedProduct = true;

      // 👉 Hàm hiển thị lỗi
      function showError(input, message) {
        input.classList.add("is-invalid");
        let feedback = input.nextElementSibling;
        if (!feedback || !feedback.classList.contains("invalid-feedback")) {
          feedback = document.createElement("div");
          feedback.className = "invalid-feedback";
          input.insertAdjacentElement("afterend", feedback);
        }
        feedback.textContent = message;
      }

      function clearError(input) {
        input.classList.remove("is-invalid");
        const feedback = input.nextElementSibling;
        if (feedback && feedback.classList.contains("invalid-feedback")) {
          feedback.remove();
        }
      }

      // 👉 Kiểm tra khi người dùng rời input
      quantityInput.addEventListener("blur", function () {
        const value = parseInt(quantityInput.value, 10);
        clearError(quantityInput);

        if (!hasSelectedProduct) {
          showError(quantityInput, "Vui lòng chọn sản phẩm trước.");
          return;
        }
        if (!value || value <= 0) {
          showError(quantityInput, "Số lượng phải lớn hơn 0.");
          return;
        }
        if (currentQuantity && value > currentQuantity) {
          showError(
            quantityInput,
            `Số lượng tối đa có thể đăng ký là ${currentQuantity}.`
          );
          return;
        }
      });

      priceInput.addEventListener("blur", function () {
        const value = parseFloat(priceInput.value);
        clearError(priceInput);

        if (!hasSelectedProduct) {
          showError(priceInput, "Vui lòng chọn sản phẩm trước.");
          return;
        }
        if (!value || value <= 0) {
          showError(priceInput, "Giá Flash Sale phải lớn hơn 0.");
          return;
        }
        if (currentPrice && value >= currentPrice) {
          showError(
            priceInput,
            `Giá Flash Sale phải nhỏ hơn giá hiện tại (${currentPrice.toLocaleString()} đ).`
          );
          return;
        }
      });
    });
  }

  // Gọi lọc ban đầu để đảm bảo hiển thị chính xác
  filterProducts();

  // ===============================
  // 🧩 VALIDATION KHI SUBMIT FORM
  // ===============================
  const form = document.getElementById("flashsaleRegisterForm");

  if (form) {
    form.addEventListener("submit", function (e) {
      const productId = hiddenSelectInput.value?.trim();
      const quantityInput = document.getElementById("flashsaleQuantityInput");
      const priceInput = document.getElementById("flashsalePriceInput");

      let valid = true;

      // 👉 Hàm hiển thị lỗi
      function showError(input, message) {
        input.classList.add("is-invalid");
        let feedback = input.nextElementSibling;
        if (!feedback || !feedback.classList.contains("invalid-feedback")) {
          feedback = document.createElement("div");
          feedback.className = "invalid-feedback";
          input.insertAdjacentElement("afterend", feedback);
        }
        feedback.textContent = message;
      }

      function clearError(input) {
        input.classList.remove("is-invalid");
        const feedback = input.nextElementSibling;
        if (feedback && feedback.classList.contains("invalid-feedback")) {
          feedback.remove();
        }
      }

      // Xóa lỗi cũ
      clearError(quantityInput);
      clearError(priceInput);
      dropdownBtn.classList.remove("is-invalid");

      // 1️⃣ Chưa chọn sản phẩm
      if (!productId) {
        dropdownBtn.classList.add("is-invalid");
        valid = false;
      }

      // 2️⃣ Kiểm tra số lượng
      const quantityValue = parseInt(quantityInput.value, 10);
      if (!quantityValue || quantityValue <= 0) {
        showError(quantityInput, "Vui lòng nhập số lượng hợp lệ (> 0).");
        valid = false;
      }

      // 3️⃣ Kiểm tra giá
      const priceValue = parseFloat(priceInput.value);
      if (!priceValue || priceValue <= 0) {
        showError(priceInput, "Vui lòng nhập giá Flash Sale hợp lệ (> 0).");
        valid = false;
      }

      // 👉 Nếu có lỗi thì chặn gửi form
      if (!valid) {
        e.preventDefault();
        e.stopPropagation();
      }
    });
  }
});
