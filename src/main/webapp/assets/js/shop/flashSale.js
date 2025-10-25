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
  // 2️⃣ Render danh sách sản phẩm (bổ sung data-price)
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
      // Thêm data-price để validate sau này
      li.dataset.id = p.productId;
      li.dataset.name = p.productName || "";
      li.dataset.quantity = p.quantity || 0;
      li.dataset.price = p.salePrice || p.price || 0; // ưu tiên salePrice, fallback price

      const firstCategory =
        p.categoryNames?.split(",")[0]?.trim() || "Không phân loại";
      const priceText = (p.salePrice || p.price || 0).toLocaleString();

      li.innerHTML = `
        <img src="http://localhost:8080/assets/images/catalog/products/${
          p.imageUrl || "no-image.jpg"
        }" alt="${p.productName || ""}"
          class="rounded border flex-shrink-0" style="width:64px;height:74px;object-fit:cover;">
        <div class="flex-grow-1 text-start">
          <div class="fw-semibold text-truncate">${p.productName || ""}</div>
          <small class="text-muted">${priceText} đ</small>
        </div>
        <div class="text-muted">SL: ${p.quantity || 0}</div>
        <span class="badge bg-light text-dark border">${firstCategory}</span>
      `;
      productList.appendChild(li);
    });

    // Áp dụng filter nếu đã có từ khóa
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
  // 4️⃣ Chọn sản phẩm (cập nhật selectedProductQty / Price)
  // =====================================================
  productList?.addEventListener("click", (e) => {
    const btn = e.target.closest(".list-group-item");
    if (!btn || btn.classList.contains("d-none")) return;

    selectedProductId = btn.dataset.id;
    hiddenProductInput.value = selectedProductId;

    // Cập nhật thông tin số lượng và giá của sản phẩm đã chọn
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
        <small class="text-muted flex-shrink-0 ms-2">SL: ${quantity}</small>
      </div>
    `;

    productList
      .querySelectorAll(".list-group-item")
      .forEach((it) => it.classList.remove("active"));
    btn.classList.add("active");

    // Ẩn dropdown
    try {
      const dd = bootstrap.Dropdown.getInstance(dropdownBtn);
      if (dd) dd.hide();
    } catch (err) {
      // ignore
    }
    dropdownMenu?.classList.remove("show");

    // Khi chọn sản phẩm mới, xóa các lỗi cũ (nếu có)
    clearError(quantityInput);
    clearError(priceInput);
    dropdownBtn?.classList.remove("is-invalid");
  });

  // =====================================================
  // 5️⃣ Toggle dropdown sản phẩm
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
  // 6️⃣ Validation real-time + khi submit (theo rule mới)
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

  // Validate số lượng:
  // - Nếu chưa chọn sản phẩm -> lỗi ("vui lòng chọn sản phẩm trước")
  // - Nếu <= 0 -> lỗi
  // - Nếu > selectedProductQty -> lỗi
  function validateQuantity() {
    // Nếu chưa chọn sản phẩm
    if (!hiddenProductInput.value) {
      showError(
        quantityInput,
        "Vui lòng chọn sản phẩm trước khi nhập số lượng."
      );
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

  // Validate giá:
  // - Nếu chưa chọn sản phẩm -> lỗi
  // - Nếu <= 1000 -> lỗi
  // - Nếu >= selectedProductPrice -> lỗi (yêu cầu nhỏ hơn giá sản phẩm)
  function validatePrice() {
    if (!hiddenProductInput.value) {
      showError(priceInput, "Vui lòng chọn sản phẩm trước khi nhập giá.");
      return false;
    }

    const value = Number(priceInput.value);
    if (isNaN(value) || value <= 1000) {
      showError(priceInput, "Giá phải lớn hơn 1.000 VND.");
      return false;
    }

    // nếu không có giá sản phẩm (selectedProductPrice falsy), vẫn cho qua check này
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

  // Nếu người dùng gõ mà chưa chọn sản phẩm: vẫn sẽ bị bắt lỗi (theo yêu cầu)
  quantityInput?.addEventListener("blur", validateQuantity);
  quantityInput?.addEventListener("input", validateQuantity);
  priceInput?.addEventListener("blur", validatePrice);
  priceInput?.addEventListener("input", validatePrice);

  form?.addEventListener("submit", (e) => {
    let valid = true;
    dropdownBtn?.classList.remove("is-invalid");

    // Nếu chưa chọn sản phẩm: mark dropdown và 2 ô nhập
    if (!hiddenProductInput.value) {
      dropdownBtn?.classList.add("is-invalid");
      // hiển thị lỗi ở cả 2 ô để user thấy
      showError(quantityInput, "Vui lòng chọn sản phẩm trước khi đăng ký.");
      showError(priceInput, "Vui lòng chọn sản phẩm trước khi đăng ký.");
      valid = false;
    }

    if (!validateQuantity()) valid = false;
    if (!validatePrice()) valid = false;

    if (!valid) {
      e.preventDefault();
      e.stopPropagation();
      // mở dropdown/modal nếu bị lỗi để user sửa nhanh (tùy ý)
      if (registerModal) registerModal.show();
    }
  });
});
