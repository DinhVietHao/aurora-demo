function addAuthor() {
  const container = document.getElementById("authors-container");
  const div = document.createElement("div");
  div.className = "input-group mb-2";
  div.innerHTML = `
        <input type="text" class="form-control" name="authors" placeholder="Tên tác giả khác" required>
        <button type="button" class="btn btn-outline-danger" onclick="removeAuthor(this)">🗑</button>
    `;
  container.appendChild(div);
}

function removeAuthor(btn) {
  const group = btn.parentNode;
  const container = document.getElementById("authors-container");
  if (container.children.length > 1) {
    container.removeChild(group);
  } else {
    alert("Phải có ít nhất một tác giả!");
  }
}

const modal = document.getElementById("updateProductModal");

document.querySelectorAll(".btn-update").forEach((btn) => {
  btn.addEventListener("click", (e) => {
    e.preventDefault();

    const data = btn.dataset;

    document.getElementById("productTitle").value = data.title;
    document.getElementById("productDescription").value = data.description;
    document.getElementById("productOriginalPrice").value = data.originalPrice;
    // làm tương tự...

    new bootstrap.Modal(modal).show();
  });
});

//Model Create
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
function addAuthor() {
  const container = document.getElementById("authors-container");
  const div = document.createElement("div");
  div.className = "input-group mb-2";
  div.innerHTML = `
        <input type="text" class="form-control" name="authors" placeholder="Tên tác giả khác" required>
        <button type="button" class="btn btn-outline-danger" onclick="removeAuthor(this)">🗑</button>
    `;
  container.appendChild(div);
}

function removeAuthor(btn) {
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

  let selectedFiles = []; // Mảng chứa danh sách ảnh đã chọn

  fileInput.addEventListener("change", function (event) {
    const files = Array.from(event.target.files);
    errorDiv.style.display = "none";
    errorDiv.innerText = "";

    // Kiểm tra dung lượng từng ảnh
    for (const file of files) {
      if (file.size > 5 * 1024 * 1024) {
        errorDiv.style.display = "block";
        errorDiv.innerText = `Ảnh "${file.name}" vượt quá dung lượng 5MB.`;
        return;
      }
    }

    // Gộp ảnh cũ và mới
    selectedFiles = [...selectedFiles, ...files];

    // Giới hạn tối đa 20 ảnh
    if (selectedFiles.length > 20) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Chỉ được tải lên tối đa 20 ảnh.";
      selectedFiles = selectedFiles.slice(0, 20);
    }

    renderPreview();
  });

  // Hàm hiển thị preview ảnh
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
                        <img src="${e.target.result}" 
                            class="img-fluid rounded"
                            style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3 / 4;">
                        <button type="button"
                            class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1 rounded-circle"
                            style="width: 25px; height: 25px; line-height: 0;"
                            data-index="${index}">
                            ×
                        </button>
                    </div>
                `;
        previewContainer.appendChild(col);
      };
      reader.readAsDataURL(file);
    });
  }

  // Lắng nghe sự kiện xóa ảnh
  previewContainer.addEventListener("click", function (e) {
    if (e.target.tagName === "BUTTON") {
      const index = e.target.getAttribute("data-index");
      selectedFiles.splice(index, 1);
      renderPreview();

      // Cập nhật lại input file để đồng bộ
      const dataTransfer = new DataTransfer();
      selectedFiles.forEach((f) => dataTransfer.items.add(f));
      fileInput.files = dataTransfer.files;
    }
  });
});
//Model Update
//--------------------------------------------------------------------------------------------------------------------------------------------------------------
function addAuthor() {
  const container = document.getElementById("authors-containerUpdate");
  const div = document.createElement("div");
  div.className = "input-group mb-2";
  div.innerHTML = `
        <input type="text" class="form-control" name="authorsUpdate" placeholder="Tên tác giả khác" required>
        <button type="button" class="btn btn-outline-danger" onclick="removeAuthor(this)">🗑</button>
    `;
  container.appendChild(div);
}

function removeAuthor(btn) {
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

  let selectedFiles = []; // Mảng chứa danh sách ảnh đã chọn

  fileInput.addEventListener("change", function (event) {
    const files = Array.from(event.target.files);
    errorDiv.style.display = "none";
    errorDiv.innerText = "";

    // Kiểm tra dung lượng từng ảnh
    for (const file of files) {
      if (file.size > 5 * 1024 * 1024) {
        errorDiv.style.display = "block";
        errorDiv.innerText = `Ảnh "${file.name}" vượt quá dung lượng 5MB.`;
        return;
      }
    }

    // Gộp ảnh cũ và mới
    selectedFiles = [...selectedFiles, ...files];

    // Giới hạn tối đa 20 ảnh
    if (selectedFiles.length > 20) {
      errorDiv.style.display = "block";
      errorDiv.innerText = "Chỉ được tải lên tối đa 20 ảnh.";
      selectedFiles = selectedFiles.slice(0, 20);
    }

    renderPreview();
  });

  // Hàm hiển thị preview ảnh
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
                        <img src="${e.target.result}" 
                            class="img-fluid rounded"
                            style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3 / 4;">
                        <button type="button"
                            class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1 rounded-circle"
                            style="width: 25px; height: 25px; line-height: 0;"
                            data-index="${index}">
                            ×
                        </button>
                    </div>
                `;
        previewContainer.appendChild(col);
      };
      reader.readAsDataURL(file);
    });
  }

  // Lắng nghe sự kiện xóa ảnh
  previewContainer.addEventListener("click", function (e) {
    if (e.target.tagName === "BUTTON") {
      const index = e.target.getAttribute("data-index");
      selectedFiles.splice(index, 1);
      renderPreview();

      // Cập nhật lại input file để đồng bộ
      const dataTransfer = new DataTransfer();
      selectedFiles.forEach((f) => dataTransfer.items.add(f));
      fileInput.files = dataTransfer.files;
    }
  });
});
