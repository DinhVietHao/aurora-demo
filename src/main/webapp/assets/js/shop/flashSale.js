document.addEventListener("DOMContentLoaded", () => {
  const joinButtons = document.querySelectorAll(".btn-join-flashsale");
  const registerModalEl = document.getElementById("flashsaleRegisterModal");
  const registerModal = registerModalEl
    ? new bootstrap.Modal(registerModalEl)
    : null;

  const productList = document.getElementById("productList");
  const dropdownBtn = document.getElementById("dropdownProductBtn");
  const dropdownMenu = document.getElementById("productDropdownMenu");
  const searchInput = document.getElementById("searchProduct");
  const selectedText = document.getElementById("selectedProductText");
  const form = document.getElementById("flashsaleRegisterForm");

  const hiddenProductInput = document.getElementById("flashsaleProductSelect");
  const hiddenShopInput = document.getElementById("flashsaleShopId");
  const quantityInput = document.getElementById("flashsaleQuantityInput");
  const priceInput = document.getElementById("flashsalePriceInput");

  // Lưu tạm thông tin sản phẩm đã chọn để validate nhanh
  let selectedProductId = null;
  let selectedProductQty = 0;
  let selectedProductPrice = 0;

  // =====================================================
  // 1️⃣ Mở modal và tải danh sách sản phẩm
  // =====================================================
  joinButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const flashSaleId = btn.dataset.id;
      const flashSaleIdInput = document.getElementById("flashSaleId");
      if (flashSaleIdInput) flashSaleIdInput.value = flashSaleId;
      if (registerModal) registerModal.show();

      fetch(`/shop/flashSale?action=getActiveProducts`)
        .then((res) => {
          if (!res.ok) throw new Error("Không thể tải danh sách sản phẩm");
          return res.json();
        })
        .then((data) => {
          if (hiddenShopInput) hiddenShopInput.value = data.shopId || "";
          renderProducts(data.products || []);
        })
        .catch((err) => {
          console.error(err);
          if (productList)
            productList.innerHTML =
              '<li class="list-group-item text-danger text-center">Lỗi khi tải dữ liệu sản phẩm</li>';
        });
    });
  });

  // =====================================================
  // 2️⃣ Render danh sách sản phẩm
  // =====================================================
  function renderProducts(products = []) {
    if (!productList) return;
    productList.innerHTML = "";

    if (!products.length) {
      productList.innerHTML =
        '<li class="list-group-item text-muted text-center">Không có sản phẩm hoạt động</li>';
      return;
    }

    products.forEach((p) => {
      const li = document.createElement("button");
      li.type = "button";
      li.className =
        "list-group-item list-group-item-action d-flex align-items-center gap-3";
      li.dataset.id = p.productId;
      li.dataset.name = p.productName || "";
      li.dataset.quantity = p.quantity || 0;
      li.dataset.price = p.salePrice || p.price || 0;

      const category = p.categoryNames || "Không phân loại";
      const price = (p.salePrice || p.price || 0).toLocaleString();

      li.innerHTML = `
      <img src="http://localhost:8080/assets/images/catalog/products/${
        p.imageUrl || "no-image.jpg"
      }" 
        alt="${p.productName || ""}"
        class="rounded border flex-shrink-0"
        style="width:64px;height:74px;object-fit:cover;">

      <div class="flex-grow-1 text-start">
        <div class="fw-semibold text-truncate">${p.productName || ""}</div>
        <small class="text-muted">${price} đ</small>
      </div>

      <div class="text-muted">SL: ${p.quantity || 0}</div>
      <span class="badge bg-light text-dark border">${category}</span>
    `;

      productList.appendChild(li);
    });

    filterProducts();
  }

  // =====================================================
  // 3️⃣ Tìm kiếm theo tên (bỏ dấu)
  // =====================================================
  function normalizeText(s) {
    return (s || "")
      .toString()
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .trim();
  }

  function filterProducts() {
    const keyword = normalizeText(searchInput?.value || "");
    productList.querySelectorAll(".list-group-item").forEach((item) => {
      const name = normalizeText(item.dataset.name || "");
      item.classList.toggle("d-none", !name.includes(keyword));
    });
  }

  searchInput?.addEventListener("input", filterProducts);

  // =====================================================
  // 4️⃣ Chọn sản phẩm
  // =====================================================
  productList?.addEventListener("click", (e) => {
    const btn = e.target.closest(".list-group-item");
    if (!btn || btn.classList.contains("d-none")) return;

    selectedProductId = btn.dataset.id;
    hiddenProductInput.value = selectedProductId;
    selectedProductQty = Number(btn.dataset.quantity || 0);
    selectedProductPrice = Number(btn.dataset.price || 0);

    const imgSrc = btn.querySelector("img")?.src || "";
    const name = btn.dataset.name || "";
    const quantity = btn.dataset.quantity || 0;
    const price = btn.querySelector("small")?.innerText || "";

    selectedText.innerHTML = `
      <div class="d-flex w-100 align-items-center">
        ${
          imgSrc
            ? `<img src="${imgSrc}" alt="${name}" class="rounded border flex-shrink-0 me-2" style="width:64px;height:74px;object-fit:cover;">`
            : ""
        }
        <div class="d-flex flex-column text-start text-truncate">
          <span class="fw-semibold text-truncate">${name}</span>
          <small class="text-muted text-truncate">${price}</small>
        </div>
      </div>
      <div class="text-muted flex-shrink-0 ms-2">SL: ${quantity}</div>
    `;

    productList
      .querySelectorAll(".list-group-item")
      .forEach((it) => it.classList.remove("active"));
    btn.classList.add("active");

    // Ẩn dropdown
    dropdownMenu?.classList.remove("show");

    clearError(quantityInput);
    clearError(priceInput);
    clearError(dropdownBtn);
    dropdownBtn?.classList.remove("is-invalid");

    // 🧩 Kiểm tra sản phẩm đã trong Flash Sale chưa
    const flashSaleId = document.getElementById("flashSaleId")?.value;
    if (flashSaleId && selectedProductId) {
      fetch(
        `/shop/flashSale?action=checkProductInFlashSale&flashSaleId=${flashSaleId}&productId=${selectedProductId}`
      )
        .then((res) => res.json())
        .then((data) => {
          const confirmBtn = document.getElementById("confirmSubmitBtn");
          if (data.exists) {
            showError(
              dropdownBtn,
              data.message ||
                "Sản phẩm này đã được đăng ký trong Flash Sale này."
            );
            if (confirmBtn) confirmBtn.disabled = true;
          } else {
            clearError(dropdownBtn);
            if (confirmBtn) confirmBtn.disabled = false;
          }
        })
        .catch((err) => console.error("Lỗi khi kiểm tra sản phẩm:", err));
    }
  });

  // =====================================================
  // 5️⃣ Hiển thị lỗi / Xóa lỗi
  // =====================================================
  function showError(input, msg) {
    if (!input) return;
    input.classList.add("is-invalid");
    let feedback = input.nextElementSibling;
    if (!feedback || !feedback.classList.contains("invalid-feedback")) {
      feedback = document.createElement("div");
      feedback.className = "invalid-feedback";
      input.insertAdjacentElement("afterend", feedback);
    }
    feedback.textContent = msg;
  }

  function clearError(input) {
    if (!input) return;
    input.classList.remove("is-invalid");
    const feedback = input.nextElementSibling;
    if (feedback?.classList.contains("invalid-feedback")) feedback.remove();
  }

  // =====================================================
  // 6️⃣ Toggle dropdown sản phẩm
  // =====================================================
  dropdownBtn?.addEventListener("click", (e) => {
    e.stopPropagation();
    dropdownMenu.classList.toggle("show");
    if (dropdownMenu.classList.contains("show"))
      setTimeout(() => searchInput?.focus(), 0);
  });

  document.addEventListener("click", (e) => {
    if (!dropdownBtn.contains(e.target) && !dropdownMenu.contains(e.target))
      dropdownMenu.classList.remove("show");
  });

  // =====================================================
  // 7️⃣ Validation real-time + khi submit
  // =====================================================
  function validateQuantity() {
    if (!hiddenProductInput.value) {
      showError(quantityInput, "Vui lòng chọn sản phẩm trước.");
      return false;
    }

    const value = Number(quantityInput.value);
    if (isNaN(value) || value <= 0) {
      showError(quantityInput, "Số lượng phải lớn hơn 0.");
      return false;
    }

    if (selectedProductQty && value > selectedProductQty) {
      showError(
        quantityInput,
        `Số lượng không được vượt quá (${selectedProductQty}).`
      );
      return false;
    }

    clearError(quantityInput);
    return true;
  }

  function validatePrice() {
    if (!hiddenProductInput.value) {
      showError(priceInput, "Vui lòng chọn sản phẩm trước.");
      return false;
    }

    const value = Number(priceInput.value);
    if (isNaN(value) || value <= 1000) {
      showError(priceInput, "Giá phải lớn hơn 1.000 VND.");
      return false;
    }

    if (selectedProductPrice && !(value < selectedProductPrice)) {
      showError(
        priceInput,
        `Giá phải nhỏ hơn giá sản phẩm (${selectedProductPrice.toLocaleString()} đ).`
      );
      return false;
    }

    clearError(priceInput);
    return true;
  }

  quantityInput?.addEventListener("blur", validateQuantity);
  quantityInput?.addEventListener("input", validateQuantity);
  priceInput?.addEventListener("blur", validatePrice);
  priceInput?.addEventListener("input", validatePrice);

  // =====================================================
  // 8️⃣ Xử lý submit form + hiển thị modal xác nhận
  // =====================================================
  form?.addEventListener("submit", (e) => {
    e.preventDefault();
    e.stopPropagation();

    let valid = true;
    dropdownBtn?.classList.remove("is-invalid");

    if (!hiddenProductInput.value) {
      dropdownBtn?.classList.add("is-invalid");
      showError(quantityInput, "Vui lòng chọn sản phẩm trước khi đăng ký.");
      showError(priceInput, "Vui lòng chọn sản phẩm trước khi đăng ký.");
      valid = false;
    }

    if (!validateQuantity()) valid = false;
    if (!validatePrice()) valid = false;

    if (!valid) {
      if (registerModal) registerModal.show();
      return;
    }

    // ✅ Nếu hợp lệ, mở modal xác nhận
    const confirmModalEl = document.getElementById("flashsaleConfirmModal");
    const confirmModal = new bootstrap.Modal(confirmModalEl);
    if (registerModal) registerModal.hide();

    const productName =
      selectedText.querySelector(".fw-semibold")?.textContent || "Chưa chọn";
    const flashPrice = priceInput.value || "-";
    const qty = quantityInput.value || "-";
    const flashTime =
      document.querySelector(`#flashSaleId`)?.selectedOptions?.[0]
        ?.textContent || "(Tự động theo Flash Sale)";

    document.getElementById("confirmProductName").textContent = productName;
    document.getElementById("confirmFlashPrice").textContent = `${Number(
      flashPrice
    ).toLocaleString()} VND`;
    document.getElementById("confirmQuantity").textContent = qty;
    document.getElementById("confirmFlashTime").textContent = flashTime;

    const productImgInRegister = selectedText.querySelector("img");
    const confirmProductImg = document.getElementById("confirmProductImg");
    if (confirmProductImg) {
      if (productImgInRegister) {
        confirmProductImg.src = productImgInRegister.src;
        confirmProductImg.alt = productName;
      } else {
        confirmProductImg.src = "/assets/images/catalog/products/no-image.jpg";
        confirmProductImg.alt = "Không có ảnh sản phẩm";
      }
    }

    confirmModal.show();

    const confirmBtn = document.getElementById("confirmSubmitBtn");
    confirmBtn.onclick = () => {
      confirmModal.hide();
      form.submit();
    };
  });

  const confirmModalEl = document.getElementById("flashsaleConfirmModal");
  if (confirmModalEl) {
    confirmModalEl.addEventListener("hidden.bs.modal", function () {
      const registerModalEl = document.getElementById("flashsaleRegisterModal");
      const registerModal =
        bootstrap.Modal.getOrCreateInstance(registerModalEl);
      setTimeout(() => registerModal.show(), 200);
    });
  }
});
