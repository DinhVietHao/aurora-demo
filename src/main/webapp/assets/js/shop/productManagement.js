// =============================
// Modal Create
// =============================
function addAuthorCreate() {
  const container = document.getElementById("authors-container");
  const div = document.createElement("div");
  div.className = "input-group mb-2";
  div.innerHTML = `
    <input type="text" class="form-control" name="authors" placeholder="Tên tác giả khác" required>
    <button type="button" class="btn btn-outline-danger" onclick="removeAuthorCreate(this)">🗑</button>
  `;
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

document.addEventListener("DOMContentLoaded", function () {
  const fileInput = document.getElementById("productImages");
  const previewContainer = document.getElementById("imagePreview");
  const errorDiv = document.getElementById("imageError");
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
