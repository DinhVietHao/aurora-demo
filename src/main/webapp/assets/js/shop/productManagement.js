// // =============================
// // Modal Create
// // =============================
function addAuthorCreate() {
  const container = document.getElementById("authors-container");

  // Lấy tất cả tên tác giả hiện có
  const authors = Array.from(
    container.querySelectorAll('input[name="authors"]')
  )
    .map((input) => input.value.trim().toLowerCase())
    .filter((name) => name !== "");

  // Tạo input mới
  const div = document.createElement("div");
  div.className = "input-group mb-2";
  div.innerHTML = `
    <input type="text" class="form-control" name="authors" placeholder="Tên tác giả khác" required>
    <button type="button" class="btn btn-outline-danger" onclick="removeAuthorCreate(this)">🗑</button>
  `;

  const newInput = div.querySelector('input[name="authors"]');

  newInput.addEventListener("input", function () {
    const name = this.value.trim().toLowerCase();
    const allAuthors = Array.from(
      container.querySelectorAll('input[name="authors"]')
    ).map((input) => input.value.trim().toLowerCase());

    const duplicates = allAuthors.filter((a) => a === name);
    if (duplicates.length > 1) {
      this.setCustomValidity("Tên tác giả đã tồn tại!");
      this.reportValidity();
    } else {
      this.setCustomValidity("");
    }
  });

  container.appendChild(div);
}

function removeAuthorCreate(btn) {
  const group = btn.parentNode;
  const container = document.getElementById("authors-container");
  if (container.children.length > 1) {
    container.removeChild(group);
  } else {
    alert("Phải có ít nhất một tác giả!");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const container = document.getElementById("authors-container");
  const firstInput = container.querySelector('input[name="authors"]');
  if (firstInput) {
    firstInput.addEventListener("input", function () {
      const name = this.value.trim().toLowerCase();
      const allAuthors = Array.from(
        container.querySelectorAll('input[name="authors"]')
      ).map((input) => input.value.trim().toLowerCase());
      const duplicates = allAuthors.filter((a) => a === name);
      if (duplicates.length > 1) {
        this.setCustomValidity("Tên tác giả đã tồn tại!");
        this.reportValidity();
      } else {
        this.setCustomValidity("");
      }
    });
  }
});

document.addEventListener("DOMContentLoaded", function () {
  const fileInput = document.getElementById("productImages");
  const previewContainer = document.getElementById("imagePreview");
  const errorDiv = document.getElementById("imageError");
  const form = document.getElementById("addProductForm");
  let selectedFiles = [];

  fileInput.addEventListener("change", function (event) {
    const files = Array.from(event.target.files);
    errorDiv.style.display = "none";
    errorDiv.innerText = "";

    for (const file of files) {
      if (file.size > 5 * 1024 * 1024) {
        errorDiv.style.display = "block";
        errorDiv.innerText = `Ảnh "${file.name}" vượt quá dung lượng 5MB.`;
        return;
      }
    }

    selectedFiles = [...selectedFiles, ...files];

    if (selectedFiles.length > 20) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Chỉ được tải lên tối đa 20 ảnh.";
      selectedFiles = selectedFiles.slice(0, 20);
    }

    const dataTransfer = new DataTransfer();
    selectedFiles.forEach((f) => dataTransfer.items.add(f));
    fileInput.files = dataTransfer.files;

    renderPreview();
  });

  async function renderPreview() {
    previewContainer.innerHTML = "";
    if (selectedFiles.length < 2) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Vui lòng chọn ít nhất 2 ảnh.";
    } else {
      errorDiv.style.display = "none";
    }

    const submitBtn = form?.querySelector('button[type="submit"]');
    if (submitBtn) {
      submitBtn.disabled =
        selectedFiles.length < 2 || selectedFiles.length > 20;
    }

    // Sử dụng Promise để đảm bảo render theo thứ tự đúng
    const promises = selectedFiles.map((file, index) => {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = function (e) {
          resolve({ index, src: e.target.result });
        };
        reader.readAsDataURL(file);
      });
    });

    const results = await Promise.all(promises);

    // Render tuần tự theo thứ tự index
    results.forEach(({ index, src }) => {
      const col = document.createElement("div");
      col.classList.add("col-3", "position-relative");

      // Nếu là ảnh đầu tiên => hiển thị nhãn "Ảnh đại diện"
      const isMain = index === 0;
      const mainBadge = isMain
        ? `<span class="badge bg-primary position-absolute top-0 start-0 m-1 px-2 py-1"
                  style="z-index: 2; font-size: 0.75rem; border-radius: 0.25rem;">Ảnh đại diện</span>`
        : "";

      col.innerHTML = `
        <div class="border rounded position-relative overflow-hidden">
          ${mainBadge}
          <img src="${src}" class="img-fluid rounded"
            style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3 / 4;">
          <button type="button"
            class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1 rounded-circle"
            style="width: 25px; height: 25px; line-height: 0;"
            data-index="${index}">×</button>
        </div>
      `;
      previewContainer.appendChild(col);
    });
  }

  previewContainer.addEventListener("click", function (e) {
    if (e.target.tagName === "BUTTON") {
      const index = parseInt(e.target.getAttribute("data-index"));
      selectedFiles.splice(index, 1);
      const dataTransfer = new DataTransfer();
      selectedFiles.forEach((f) => dataTransfer.items.add(f));
      fileInput.files = dataTransfer.files;
      console.log("Check  fileInput.files ", fileInput.files);

      renderPreview();
    }
  });
  if (form) {
    form.addEventListener("submit", function (e) {
      if (selectedFiles.length < 2 || selectedFiles.length > 20) {
        e.preventDefault();
        errorDiv.style.display = "block";
        errorDiv.innerText = "Cần tải lên từ 2 đến 20 ảnh sản phẩm.";
        fileInput.classList.add("is-invalid");

        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) submitBtn.disabled = true;
        return;
      }

      // Nếu hợp lệ
      fileInput.classList.remove("is-invalid");
      errorDiv.style.display = "none";

      const submitBtn = form.querySelector('button[type="submit"]');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML =
          '<i class="bi bi-hourglass-split me-1"></i> Đang lưu...';
      }
    });
  }
});

// =============================
// Modal Update
// =============================
function addAuthorUpdate() {
  const container = document.getElementById("authors-containerUpdate");
  const div = document.createElement("div");
  div.className = "input-group mb-2";
  div.innerHTML = `
    <input type="text" class="form-control" name="authorsUpdate" placeholder="Tên tác giả khác" required>
    <button type="button" class="btn btn-outline-danger" onclick="removeAuthorUpdate(this)">🗑</button>
  `;
  container.appendChild(div);
}

function removeAuthorUpdate(btn) {
  const group = btn.parentNode;
  const container = document.getElementById("authors-containerUpdate");
  if (container.children.length > 1) {
    container.removeChild(group);
  } else {
    alert("Phải có ít nhất một tác giả!");
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const fileInput = document.getElementById("productImagesUpdate");
  const previewContainer = document.getElementById("imagePreviewUpdate");
  const errorDiv = document.getElementById("imageErrorUpdate");
  let selectedFiles = [];

  fileInput.addEventListener("change", function (event) {
    const files = Array.from(event.target.files);
    errorDiv.style.display = "none";
    errorDiv.innerText = "";

    for (const file of files) {
      if (file.size > 5 * 1024 * 1024) {
        errorDiv.style.display = "block";
        errorDiv.innerText = `Ảnh "${file.name}" vượt quá dung lượng 5MB.`;
        return;
      }
    }

    selectedFiles = [...selectedFiles, ...files];

    if (selectedFiles.length > 20) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Chỉ được tải lên tối đa 20 ảnh.";
      selectedFiles = selectedFiles.slice(0, 20);
    }

    const dataTransfer = new DataTransfer();
    selectedFiles.forEach((f) => dataTransfer.items.add(f));
    fileInput.files = dataTransfer.files;

    renderPreview();
  });

  function renderPreview() {
    previewContainer.innerHTML = "";
    if (selectedFiles.length < 2) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Vui lòng chọn ít nhất 2 ảnh.";
    } else {
      errorDiv.style.display = "none";
    }

    selectedFiles.forEach((file, index) => {
      const reader = new FileReader();
      reader.onload = function (e) {
        const col = document.createElement("div");
        col.classList.add("col-3", "position-relative");
        col.innerHTML = `
          <div class="border rounded position-relative overflow-hidden">
            <img src="${e.target.result}" class="img-fluid rounded"
              style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3 / 4;">
            <button type="button"
              class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1 rounded-circle"
              style="width: 25px; height: 25px; line-height: 0;"
              data-index="${index}">×</button>
          </div>
        `;
        previewContainer.appendChild(col);
      };
      reader.readAsDataURL(file);
    });
  }

  previewContainer.addEventListener("click", function (e) {
    if (e.target.tagName === "BUTTON") {
      const index = e.target.getAttribute("data-index");
      selectedFiles.splice(index, 1);
      renderPreview();
      const dataTransfer = new DataTransfer();
      selectedFiles.forEach((f) => dataTransfer.items.add(f));
      fileInput.files = dataTransfer.files;
    }
  });
});

// Bắt lỗi giá bán và giá gốc
document.addEventListener("DOMContentLoaded", function () {
  const originalPriceInput = document.getElementById("productOriginalPrice");
  const salePriceInput = document.getElementById("productSalePrice");
  const errorDiv = document.getElementById("priceError");
  const form = originalPriceInput.closest("form");

  function validatePrices() {
    const originalPrice = parseFloat(originalPriceInput.value);
    const salePrice = parseFloat(salePriceInput.value);

    if (isNaN(originalPrice) || isNaN(salePrice)) {
      errorDiv.style.display = "none";
      return true;
    }

    if (originalPrice < 0 || salePrice < 0) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Giá không được âm.";
      return false;
    }

    if (originalPrice < salePrice) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Giá gốc phải lớn hơn hoặc bằng giá bán.";
      return false;
    }

    errorDiv.style.display = "none";
    return true;
  }

  // Khi người dùng nhập -> kiểm tra
  originalPriceInput.addEventListener("input", validatePrices);
  salePriceInput.addEventListener("input", validatePrices);

  // Khi submit -> chặn nếu sai
  form.addEventListener("submit", function (e) {
    if (!validatePrices()) {
      e.preventDefault();
    }
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const deleteButtons = document.querySelectorAll(".btn-delete");
  const deleteMessage = document.getElementById("deleteMessage");
  const deleteProductId = document.getElementById("deleteProductId");

  deleteButtons.forEach((btn) => {
    btn.addEventListener("click", function () {
      const productTitle = this.getAttribute("data-product-title");
      const productId = this.getAttribute("data-product-id");

      deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa sản phẩm <strong>"${productTitle}"</strong> này không?`;
      deleteProductId.value = productId;
    });
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const publishedDateInput = document.getElementById("publishedDate");

  // Tạo phần hiển thị lỗi dưới ô input (nếu chưa có)
  let errorMsg = document.createElement("div");
  errorMsg.classList.add("invalid-feedback");
  errorMsg.style.display = "none"; // Ẩn ban đầu
  publishedDateInput.parentNode.appendChild(errorMsg);

  // Hàm kiểm tra ngày
  function validatePublishedDate() {
    const dateValue = publishedDateInput.value;
    errorMsg.textContent = ""; // Xóa thông báo cũ
    errorMsg.style.display = "none";

    if (dateValue) {
      const today = new Date();
      today.setHours(0, 0, 0, 0); // Đặt về đầu ngày để so sánh chính xác

      const publishedDate = new Date(dateValue);

      if (publishedDate > today) {
        // Báo lỗi: làm input đỏ và hiển thị thông báo
        publishedDateInput.classList.add("is-invalid");
        errorMsg.textContent =
          "⚠️ Ngày xuất bản không được lớn hơn ngày hiện tại!";
        errorMsg.style.display = "block";
      } else {
        // Hợp lệ: xóa lỗi
        publishedDateInput.classList.remove("is-invalid");
        errorMsg.style.display = "none";
      }
    } else {
      // Nếu bỏ trống: xóa lỗi
      publishedDateInput.classList.remove("is-invalid");
      errorMsg.style.display = "none";
    }
  }

  // Gọi hàm kiểm tra khi người dùng chọn ngày (event blur hoặc change)
  publishedDateInput.addEventListener("blur", validatePublishedDate);
  publishedDateInput.addEventListener("change", validatePublishedDate);

  // Tùy chọn: Kiểm tra khi submit form (để chặn submit nếu sai)
  const form = document.getElementById("addProductForm");
  if (form) {
    form.addEventListener("submit", function (event) {
      validatePublishedDate(); // Kiểm tra lại
      if (publishedDateInput.classList.contains("is-invalid")) {
        event.preventDefault(); // Ngăn submit nếu có lỗi
      }
    });
  }
});

// =============================
// Modal Update: Populate Data via AJAX
// =============================
document.addEventListener("DOMContentLoaded", function () {
  const updateButtons = document.querySelectorAll(".btn-update");

  updateButtons.forEach((btn) => {
    btn.addEventListener("click", function () {
      const productId = this.getAttribute("data-product-id");

      fetch(`/shop/product?action=getProduct&id=${productId}`)
        .then((response) => {
          if (!response.ok) throw new Error("Không thể tải dữ liệu sản phẩm");
          return response.json();
        })
        .then((product) => populateUpdateModal(product))
        .catch((error) => {
          console.error("Lỗi:", error);
          alert("Không thể tải thông tin sản phẩm. Vui lòng thử lại.");
        });
    });
  });

  function populateUpdateModal(product) {
    const dateString = product.publishedDate;
    const date = new Date(dateString);
    const formattedDate = date.toISOString().split("T")[0];

    document.getElementById("updateProductForm").action =
      "/shop/product?action=update&productId=" + product.productId;

    document.getElementById("productTitleUpdate").value = product.title || "";
    document.getElementById("productDescriptionUpdate").value =
      product.description || "";
    document.getElementById("productOriginalPriceUpdate").value =
      product.originalPrice || "";
    document.getElementById("productSalePriceUpdate").value =
      product.salePrice || "";
    document.getElementById("productQuantityUpdate").value =
      product.quantity || "";
    document.getElementById("weightUpdate").value = product.weight || "";
    document.getElementById("publisherNameUpdate").value =
      product.publisher.name || "";
    document.getElementById("publishedDateUpdate").value = formattedDate;
    document.getElementById("translatorUpdate").value =
      product.bookDetail?.translator || "";
    document.getElementById("versionUpdate").value =
      product.bookDetail?.version || "";
    document.getElementById("coverTypeUpdate").value =
      product.bookDetail?.coverType || "";
    document.getElementById("pagesUpdate").value =
      product.bookDetail?.pages || "";
    document.getElementById("sizeUpdate").value =
      product.bookDetail?.size || "";
    document.getElementById("languageCodeUpdate").value =
      product.bookDetail?.languageCode || "";
    document.getElementById("isbnUpdate").value =
      product.bookDetail?.isbn || "";

    // ======== Authors ========
    const authorsContainer = document.getElementById("authors-containerUpdate");
    authorsContainer.innerHTML = "";
    if (product.authors && product.authors.length > 0) {
      product.authors.forEach((author) => {
        const div = document.createElement("div");
        div.className = "input-group mb-2";
        div.innerHTML = `
          <input type="text" class="form-control" name="authorsUpdate" value="${author.authorName}" required>
          <button type="button" class="btn btn-outline-danger" onclick="removeAuthorUpdate(this)">🗑</button>
        `;
        authorsContainer.appendChild(div);
      });
    } else {
      const div = document.createElement("div");
      div.className = "input-group mb-2";
      div.innerHTML = `
        <input type="text" class="form-control" name="authorsUpdate" placeholder="Tên tác giả" required>
        <button type="button" class="btn btn-outline-danger" onclick="removeAuthorUpdate(this)">🗑</button>
      `;
      authorsContainer.appendChild(div);
    }

    // ======== Categories ========
    document
      .querySelectorAll('#updateProductModal input[name="CategoryIDs"]')
      .forEach((cb) => (cb.checked = false));
    if (product.categories) {
      product.categories.forEach((cat) => {
        const cb = document.querySelector(
          `#updateProductModal input[name="CategoryIDs"][value="${cat.categoryId}"]`
        );
        if (cb) cb.checked = true;
      });
    }

    // ======== Images ========
    const imageInput = document.getElementById("productImagesUpdate");
    const previewContainer = document.getElementById("imagePreviewUpdate");
    const errorDiv = document.getElementById("imageErrorUpdate");
    imageInput.value = ""; // reset input

    let currentImages = product.images
      ? product.images.map((img, index) => ({
          id: img.imageId,
          url: `http://localhost:8080/assets/images/catalog/products/${img.url}`,
          isPrimary: img.isPrimary || index === 0,
          isNew: false,
        }))
      : [];

    renderImagePreview();

    imageInput.onchange = function (event) {
      const files = Array.from(event.target.files || []);
      if (!errorDiv) return;
      errorDiv.style.display = "none";
      errorDiv.innerText = "";

      for (const file of files) {
        if (file.size > 5 * 1024 * 1024) {
          errorDiv.style.display = "block";
          errorDiv.innerText = `Ảnh "${file.name}" vượt quá 5MB.`;
          return;
        }
      }

      files.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (e) => {
          currentImages.push({
            file,
            url: e.target.result,
            isPrimary: false,
            isNew: true,
          });
          renderImagePreview();
        };
        reader.readAsDataURL(file);
      });
    };

    function renderImagePreview() {
      previewContainer.innerHTML = "";

      currentImages.forEach((img, index) => {
        const col = document.createElement("div");
        col.classList.add("col-3", "position-relative");

        const mainBadge = img.isPrimary
          ? `<span class="badge bg-primary position-absolute top-0 start-0 m-1 px-2 py-1" style="font-size: 0.75rem;">Ảnh đại diện</span>`
          : "";

        col.innerHTML = `
          <div class="border rounded position-relative overflow-hidden">
            ${mainBadge}
            <img src="${img.url}" class="img-fluid rounded"
                 style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3/4;">
            <button type="button"
                    class="btn btn-light text-danger btn-sm position-absolute top-0 end-0 m-1 p-0 px-2 fw-bold"
                    data-index="${index}">×</button>
          </div>`;
        previewContainer.appendChild(col);
      });

      validateImageCount();
    }

    // chỉ gắn 1 lần
    previewContainer.onclick = function (e) {
      const btn = e.target.closest("button[data-index]");
      if (!btn) return;
      const index = parseInt(btn.dataset.index);
      if (isNaN(index)) return;
      currentImages.splice(index, 1);
      renderImagePreview();
    };

    // ✅ Chỉ 1 hàm validate duy nhất
    function validateImageCount() {
      if (!errorDiv) return;
      const count = currentImages.length;
      if (count < 2) {
        errorDiv.style.display = "block";
        errorDiv.innerText = "Vui lòng chọn ít nhất 2 ảnh.";
      } else if (count > 20) {
        errorDiv.style.display = "block";
        errorDiv.innerText = "Không được vượt quá 20 ảnh.";
      } else {
        errorDiv.style.display = "none";
      }
    }
  }

  // Xóa tác giả
  window.removeAuthorUpdate = function (btn) {
    const group = btn.parentNode;
    const container = document.getElementById("authors-containerUpdate");
    if (container.children.length > 1) {
      container.removeChild(group);
    } else {
      alert("Phải có ít nhất một tác giả!");
    }
  };
});

// =============================
// Validate Form Update
// =============================
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("updateProductForm");

  // --- Validate giá ---
  const originalPriceInput = document.getElementById(
    "productOriginalPriceUpdate"
  );
  const salePriceInput = document.getElementById("productSalePriceUpdate");
  const priceErrorDiv =
    document.getElementById("priceErrorUpdate") ||
    document.createElement("div");

  priceErrorDiv.id = "priceErrorUpdate";
  priceErrorDiv.classList.add("text-danger", "mt-1");
  originalPriceInput.parentNode.appendChild(priceErrorDiv);

  function validatePricesUpdate() {
    const originalPrice = parseFloat(originalPriceInput.value);
    const salePrice = parseFloat(salePriceInput.value);

    if (isNaN(originalPrice) || isNaN(salePrice)) {
      priceErrorDiv.style.display = "none";
      return true;
    }

    if (originalPrice < 0 || salePrice < 0) {
      priceErrorDiv.style.display = "block";
      priceErrorDiv.innerText = "Giá không được âm.";
      return false;
    }

    if (originalPrice < salePrice) {
      priceErrorDiv.style.display = "block";
      priceErrorDiv.innerText = "Giá gốc phải lớn hơn hoặc bằng giá bán.";
      return false;
    }

    priceErrorDiv.style.display = "none";
    return true;
  }

  originalPriceInput.addEventListener("input", validatePricesUpdate);
  salePriceInput.addEventListener("input", validatePricesUpdate);

  // --- Validate ngày phát hành ---
  const publishedDateInput = document.getElementById("publishedDateUpdate");
  let errorMsg = document.createElement("div");
  errorMsg.classList.add("invalid-feedback");
  errorMsg.style.display = "none";
  publishedDateInput.parentNode.appendChild(errorMsg);

  function validatePublishedDateUpdate() {
    const dateValue = publishedDateInput.value;
    errorMsg.textContent = "";
    errorMsg.style.display = "none";

    if (dateValue) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const publishedDate = new Date(dateValue);

      if (publishedDate > today) {
        publishedDateInput.classList.add("is-invalid");
        errorMsg.textContent =
          "⚠️ Ngày xuất bản không được lớn hơn ngày hiện tại!";
        errorMsg.style.display = "block";
      } else {
        publishedDateInput.classList.remove("is-invalid");
        errorMsg.style.display = "none";
      }
    }
  }

  publishedDateInput.addEventListener("blur", validatePublishedDateUpdate);
  publishedDateInput.addEventListener("change", validatePublishedDateUpdate);

  // --- Validate trùng tác giả ---
  const authorContainer = document.getElementById("authors-containerUpdate");
  authorContainer.addEventListener("input", function (e) {
    if (e.target.name === "authorsUpdate") {
      const allAuthors = Array.from(
        authorContainer.querySelectorAll('input[name="authorsUpdate"]')
      )
        .map((input) => input.value.trim().toLowerCase())
        .filter((name) => name !== "");

      const duplicates = allAuthors.filter(
        (a) => a === e.target.value.trim().toLowerCase()
      );
      if (duplicates.length > 1) {
        e.target.setCustomValidity("Tên tác giả đã tồn tại!");
        e.target.reportValidity();
      } else {
        e.target.setCustomValidity("");
      }
    }
  });

  // --- Validate ảnh upload (2–20 ảnh) ---
  const fileInput = document.getElementById("productImagesUpdate");
  const previewContainer = document.getElementById("imagePreviewUpdate");
  const errorDiv = document.getElementById("imageErrorUpdate");
  let selectedFiles = [];

  fileInput.addEventListener("change", function (event) {
    const files = Array.from(event.target.files);
    if (!errorDiv) return;
    errorDiv.style.display = "none";
    errorDiv.innerText = "";

    for (const file of files) {
      if (file.size > 5 * 1024 * 1024) {
        errorDiv.style.display = "block";
        errorDiv.innerText = `Ảnh "${file.name}" vượt quá dung lượng 5MB.`;
        return;
      }
    }

    selectedFiles = [...selectedFiles, ...files];

    if (selectedFiles.length > 20) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Chỉ được tải lên tối đa 20 ảnh.";
      selectedFiles = selectedFiles.slice(0, 20);
    }

    const dataTransfer = new DataTransfer();
    selectedFiles.forEach((f) => dataTransfer.items.add(f));
    fileInput.files = dataTransfer.files;

    renderPreviewUpdate();
  });

  async function renderPreviewUpdate() {
    if (!Array.isArray(selectedFiles)) selectedFiles = [];
    if (!previewContainer) return;
    previewContainer.innerHTML = "";
    if (selectedFiles.length < 2) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Vui lòng chọn ít nhất 2 ảnh.";
    } else {
      errorDiv.style.display = "none";
    }

    const promises = selectedFiles.map((file, index) => {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = function (e) {
          resolve({ index, src: e.target.result });
        };
        reader.readAsDataURL(file);
      });
    });

    const results = await Promise.all(promises);

    results.forEach(({ index, src }) => {
      const col = document.createElement("div");
      col.classList.add("col-3", "position-relative");
      const isMain = index === 0;
      const mainBadge = isMain
        ? `<span class="badge bg-primary position-absolute top-0 start-0 m-1 px-2 py-1"
                  style="z-index: 2; font-size: 0.75rem; border-radius: 0.25rem;">Ảnh đại diện</span>`
        : "";
      col.innerHTML = `
        <div class="border rounded position-relative overflow-hidden">
          ${mainBadge}
          <img src="${src}" class="img-fluid rounded"
            style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3 / 4;">
          <button type="button"
            class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1 rounded-circle"
            style="width: 25px; height: 25px; line-height: 0;"
            data-index="${index}">×</button>
        </div>`;
      previewContainer.appendChild(col);
    });
  }

  previewContainer.addEventListener("click", function (e) {
    if (e.target.tagName === "BUTTON") {
      const index = parseInt(e.target.getAttribute("data-index"));
      selectedFiles.splice(index, 1);
      const dataTransfer = new DataTransfer();
      selectedFiles.forEach((f) => dataTransfer.items.add(f));
      fileInput.files = dataTransfer.files;
      renderPreviewUpdate();
    }
  });

  // --- Validate tổng thể khi submit ---
  form.addEventListener("submit", function (e) {
    validatePricesUpdate();
    validatePublishedDateUpdate();

    if (
      publishedDateInput.classList.contains("is-invalid") ||
      !validatePricesUpdate()
    ) {
      e.preventDefault();
      return;
    }

    if (
      selectedFiles.length > 0 &&
      (selectedFiles.length < 2 || selectedFiles.length > 20)
    ) {
      e.preventDefault();
      errorDiv.style.display = "block";
      errorDiv.innerText = "Cần tải lên từ 2 đến 20 ảnh sản phẩm.";
      return;
    }
  });
});
