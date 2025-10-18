document.addEventListener("DOMContentLoaded", () => {
  // =============================
  // UTILS
  // =============================
  const $ = (selector, parent = document) => parent.querySelector(selector);
  const $$ = (selector, parent = document) =>
    Array.from(parent.querySelectorAll(selector));

  const showError = (el, msg) => {
    if (!el) return;
    el.style.display = "block";
    el.innerText = msg;
  };
  const hideError = (el) => {
    if (!el) return;
    el.style.display = "none";
    el.innerText = "";
  };

  // =============================
  // AUTHORS (add/remove + duplicate validation)
  // =============================
  window.addAuthorCreate = function () {
    const container = document.getElementById("authors-container");
    const div = document.createElement("div");
    div.className = "input-group mb-2";
    div.innerHTML = `
      <input type="text" class="form-control" name="authors" placeholder="Tên tác giả khác" required>
      <button type="button" class="btn btn-outline-danger btn-remove">🗑</button>
    `;
    container.appendChild(div);
  };

  window.addAuthorUpdate = function () {
    const container = document.getElementById("authors-containerUpdate");
    const div = document.createElement("div");
    div.className = "input-group mb-2";
    div.innerHTML = `
      <input type="text" class="form-control" name="authorsUpdate" placeholder="Tên tác giả khác" required>
      <button type="button" class="btn btn-outline-danger btn-remove">🗑</button>
    `;
    container.appendChild(div);
  };

  window.removeAuthorCreate = function (btn) {
    removeAuthor("authors-container", btn);
  };

  window.removeAuthorUpdate = function (btn) {
    removeAuthor("authors-containerUpdate", btn);
  };

  function removeAuthor(containerId, btn) {
    const container = document.getElementById(containerId);
    const group = btn.closest(".input-group");
    if (container.children.length > 1) container.removeChild(group);
    else alert("Phải có ít nhất một tác giả!");
  }

  document.body.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-remove")) {
      const parentContainer = e.target.closest("[id^='authors-container']");
      if (parentContainer) removeAuthor(parentContainer.id, e.target);
    }
  });

  function validateDuplicateNames(container, inputName, message) {
    if (!container) return;
    container.addEventListener("input", (e) => {
      if (e.target.name !== inputName) return;
      const allNames = $$(`input[name="${inputName}"]`, container)
        .map((i) => i.value.trim().toLowerCase())
        .filter(Boolean);
      const dup = allNames.filter(
        (n) => n === e.target.value.trim().toLowerCase()
      );
      if (dup.length > 1) {
        e.target.setCustomValidity(message);
        e.target.reportValidity();
      } else e.target.setCustomValidity("");
    });
  }

  validateDuplicateNames(
    $("#authors-container"),
    "authors",
    "Tên tác giả đã tồn tại!"
  );
  validateDuplicateNames(
    $("#authors-containerUpdate"),
    "authorsUpdate",
    "Tên tác giả đã tồn tại!"
  );

  // =============================
  // IMAGE UPLOAD PREVIEW - Create (simple)
  // =============================
  function initImageUpload({
    fileInputId,
    previewContainerId,
    errorDivId,
    formId,
  }) {
    const fileInput = document.getElementById(fileInputId);
    const previewContainer = document.getElementById(previewContainerId);
    const errorDiv = document.getElementById(errorDivId);
    const form = formId ? document.getElementById(formId) : null;
    let selectedFiles = [];

    if (!fileInput) return;

    const renderPreview = async () => {
      previewContainer.innerHTML = "";
      const count = selectedFiles.length;
      if (count < 2) showError(errorDiv, "Vui lòng chọn ít nhất 2 ảnh.");
      else if (count > 20)
        showError(errorDiv, "Chỉ được tải lên tối đa 20 ảnh.");
      else hideError(errorDiv);

      const submitBtn = form?.querySelector('button[type="submit"]');
      if (submitBtn) submitBtn.disabled = count < 2 || count > 20;

      const promises = selectedFiles.map(
        (file, index) =>
          new Promise((resolve) => {
            const reader = new FileReader();
            reader.onload = (e) => resolve({ index, src: e.target.result });
            reader.readAsDataURL(file);
          })
      );

      const results = await Promise.all(promises);
      results.forEach(({ index, src }) => {
        const isMain = index === 0;
        const mainBadge = isMain
          ? `<span class="badge bg-primary position-absolute top-0 start-0 m-1 px-2 py-1" style="font-size:0.75rem;border-radius:0.25rem;">Ảnh đại diện</span>`
          : "";
        const col = document.createElement("div");
        col.classList.add("col-3", "position-relative");
        col.innerHTML = `
          <div class="border rounded position-relative overflow-hidden">
            ${mainBadge}
            <img src="${src}" class="img-fluid rounded" style="object-fit:cover;height:200px;width:100%;aspect-ratio:3/4;">
            <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0 m-1 rounded-circle" style="width:25px;height:25px;" data-index="${index}">×</button>
          </div>`;
        previewContainer.appendChild(col);
      });
    };

    fileInput.addEventListener("change", (e) => {
      const files = Array.from(e.target.files);
      hideError(errorDiv);

      for (const f of files) {
        if (f.size > 5 * 1024 * 1024)
          return showError(errorDiv, `Ảnh "${f.name}" vượt quá 5MB.`);
      }

      selectedFiles = [...selectedFiles, ...files].slice(0, 20);

      const dt = new DataTransfer();
      selectedFiles.forEach((f) => dt.items.add(f));
      fileInput.files = dt.files;
      renderPreview();
    });

    previewContainer.addEventListener("click", (e) => {
      if (e.target.closest("button[data-index]")) {
        const i = +e.target.closest("button").dataset.index;
        selectedFiles.splice(i, 1);
        const dt = new DataTransfer();
        selectedFiles.forEach((f) => dt.items.add(f));
        fileInput.files = dt.files;
        renderPreview();
      }
    });

    form?.addEventListener("submit", (e) => {
      const count = selectedFiles.length;
      if (count < 2 || count > 20) {
        e.preventDefault();
        showError(errorDiv, "Cần tải lên từ 2 đến 20 ảnh sản phẩm.");
      }
    });
  }

  // init for create
  initImageUpload({
    fileInputId: "productImages",
    previewContainerId: "imagePreview",
    errorDivId: "imageError",
    formId: "addProductForm",
  });

  // =============================
  // IMAGE UPLOAD PREVIEW - Update (KEEP old images + ADD new ones)
  // =============================
  function initImageUploadForUpdate({
    fileInputId,
    previewContainerId,
    errorDivId,
    formId,
    hiddenRemovedIdName = "removedImageIds", // hidden input name to send removed existing image ids
  }) {
    const fileInput = document.getElementById(fileInputId);
    const previewContainer = document.getElementById(previewContainerId);
    const errorDiv = document.getElementById(errorDivId);
    const form = document.getElementById(formId);
    if (!previewContainer || !form) return;

    // currentImages holds both existing (from server) and new files
    // { id?, url, isNew: boolean, file?, isPrimary: boolean }
    let currentImages = [];
    let removedImageIds = new Set();

    // expose helper to populate (used by populateUpdateModal)
    function setInitialImages(images) {
      currentImages = images.map((img, idx) => ({
        id: img.id || null,
        url: img.url || "",
        isNew: !!img.isNew,
        file: img.file || null,
        isPrimary: !!img.isPrimary || idx === 0,
      }));
      removedImageIds = new Set();
      render();
    }

    function render() {
      // Preserve reference to form for later use
      const form = document.getElementById("updateProductForm");

      previewContainer.innerHTML = "";
      const count = currentImages.length;
      if (count < 2) showError(errorDiv, "Vui lòng chọn ít nhất 2 ảnh.");
      else if (count > 20) showError(errorDiv, "Không được vượt quá 20 ảnh.");
      else hideError(errorDiv);

      // Update file input to contain only NEW files
      if (fileInput) {
        const dt = new DataTransfer();
        currentImages.forEach((it) => {
          if (it.isNew && it.file) dt.items.add(it.file);
        });
        fileInput.files = dt.files;
      }

      // Render each image
      currentImages.forEach((img, index) => {
        const col = document.createElement("div");
        col.classList.add("col-3", "position-relative");

        const mainBadge = img.isPrimary
          ? `<span class="badge bg-primary position-absolute top-0 start-0 m-1 px-2 py-1" style="font-size:0.75rem;">Ảnh đại diện</span>`
          : "";

        const removeBtn = `<button type="button" class="btn btn-light text-danger btn-sm position-absolute top-0 end-0 m-1 p-0 px-2 fw-bold" data-index="${index}">×</button>`;
        const setPrimaryBtn = !img.isPrimary
          ? `<button type="button" class="btn btn-sm btn-outline-secondary position-absolute bottom-0 start-0 m-1 set-primary" data-index="${index}" style="font-size:0.7rem;">Đặt làm chính</button>`
          : "";

        col.innerHTML = `
          <div class="border rounded position-relative overflow-hidden">
            ${mainBadge}
            <img src="${img.url}" class="img-fluid rounded" style="object-fit: cover; height: 200px; width: 100%; aspect-ratio: 3/4;">
            ${removeBtn}
            ${setPrimaryBtn}
          </div>`;
        previewContainer.appendChild(col);
      });
    }

    // when user picks new files -> append as new entries
    if (fileInput) {
      fileInput.addEventListener("change", (e) => {
        const files = Array.from(e.target.files || []);
        hideError(errorDiv);
        for (const f of files) {
          if (f.size > 5 * 1024 * 1024)
            return showError(errorDiv, `Ảnh "${f.name}" vượt quá 5MB.`);
        }

        // Append as new images
        files.forEach((file) => {
          const reader = new FileReader();
          reader.onload = (ev) => {
            currentImages.push({
              id: null,
              url: ev.target.result,
              isNew: true,
              file,
              isPrimary: currentImages.length === 0, // if no images, first new becomes primary
            });
            // ensure primary uniqueness: if this is primary, unset others
            if (currentImages[currentImages.length - 1].isPrimary) {
              currentImages.forEach((it, idx) => {
                if (idx !== currentImages.length - 1) it.isPrimary = false;
              });
            }
            // limit
            if (currentImages.length > 20) {
              showError(errorDiv, "Chỉ được tải lên tối đa 20 ảnh.");
              currentImages = currentImages.slice(0, 20);
            }
            render();
          };
          reader.readAsDataURL(file);
        });
      });
    }

    // Click handlers for all image-related actions
    previewContainer.addEventListener("click", (e) => {
      e.preventDefault();
      e.stopPropagation();

      // Handle "Set as primary" button clicks
      const setPrimaryBtn = e.target.closest(".set-primary");
      if (setPrimaryBtn) {
        const index = +setPrimaryBtn.dataset.index;
        if (!isNaN(index) && currentImages[index]) {
          // Update primary status
          currentImages.forEach((img) => (img.isPrimary = false));
          currentImages[index].isPrimary = true;

          // Update form hidden input
          const form = document.getElementById("updateProductForm");
          if (form) {
            // Clear existing primary updates
            $$('input[name="primaryImageUpdate"]', form).forEach((el) =>
              el.remove()
            );

            // Add new primary update
            const primaryInput = document.createElement("input");
            primaryInput.type = "hidden";
            primaryInput.name = "primaryImageUpdate";

            if (currentImages[index].isNew && fileInput) {
              const fileIndex = Array.from(fileInput.files).findIndex(
                (f) =>
                  f.name === currentImages[index].file.name &&
                  f.size === currentImages[index].file.size
              );
              if (fileIndex !== -1) {
                primaryInput.value = `new:${fileIndex}:1`;
              }
            } else if (currentImages[index].id) {
              primaryInput.value = `${currentImages[index].id}:1`;
            }

            form.appendChild(primaryInput);
          }
          render();
        }
        return;
      }

      // Handle remove button clicks
      const removeBtn = e.target.closest("button[data-index]");
      if (removeBtn) {
        const index = +removeBtn.dataset.index;
        if (isNaN(index)) return;

        const removed = currentImages.splice(index, 1)[0];
        if (removed?.id) {
          removedImageIds.add(removed.id);
        }

        // If we removed the primary image, make the first remaining image primary
        if (removed?.isPrimary && currentImages.length > 0) {
          currentImages[0].isPrimary = true;
        }

        render();
      }
    });

    // prepare data before submit
    form.addEventListener("submit", (e) => {
      // validations
      const count = currentImages.length;
      if (count < 2 || count > 20) {
        e.preventDefault();
        showError(errorDiv, "Cần tải lên từ 2 đến 20 ảnh sản phẩm.");
        return;
      }

      // Create/replace hidden input for removed image ids
      let removedInput = form.querySelector(
        `input[name="${hiddenRemovedIdName}"]`
      );
      if (!removedInput) {
        removedInput = document.createElement("input");
        removedInput.type = "hidden";
        removedInput.name = hiddenRemovedIdName;
        form.appendChild(removedInput);
      }
      removedInput.value = Array.from(removedImageIds).join(",");

      // Ensure fileInput.files contains only NEW files (we already set that in render())
      // Additionally, create hidden inputs for existing image ids we want to keep and their primary flags.
      // Remove previous if any:
      $$("#existingImageIds", form).forEach((el) => el.remove());
      $$("#existingImagePrimary", form).forEach((el) => el.remove());

      // Clear existing primary inputs first
      $$('input[name="primaryImageUpdate"]', form).forEach((el) => el.remove());
      $$('input[name="existingImageIds"]', form).forEach((el) => el.remove());

      currentImages.forEach((it, idx) => {
        if (!it.isNew && it.id) {
          // Add existing image ID to form
          const inp = document.createElement("input");
          inp.type = "hidden";
          inp.name = "existingImageIds";
          inp.value = it.id;
          form.appendChild(inp);

          // If this is primary, add primary update info
          if (it.isPrimary) {
            const primaryInput = document.createElement("input");
            primaryInput.type = "hidden";
            primaryInput.name = "primaryImageUpdate";
            primaryInput.value = `${it.id}:1`;
            form.appendChild(primaryInput);
          }
        }
        if (it.isNew && it.file && it.isPrimary) {
          // For new primary image, send the index in the uploaded files
          const newIndex = Array.from(fileInput.files).findIndex(
            (f) => f.name === it.file.name && f.size === it.file.size
          );
          if (newIndex !== -1) {
            const primaryInput = document.createElement("input");
            primaryInput.type = "hidden";
            primaryInput.name = "primaryImageUpdate";
            primaryInput.value = `new:${newIndex}:1`;
            form.appendChild(primaryInput);
          }
        }
      });

      // At this point fileInput.files already contains the new files (render() set it).
      // Let the form submit normally (multipart/form-data expected on server).
    });

    // return setter for populateUpdateModal
    return { setInitialImages };
  }

  // Initialize update image handler and store setter
  const imageUpdateHandler = initImageUploadForUpdate({
    fileInputId: "productImagesUpdate",
    previewContainerId: "imagePreviewUpdate",
    errorDivId: "imageErrorUpdate",
    formId: "updateProductForm",
    hiddenRemovedIdName: "removedImageIds",
  });

  // =============================
  // PRICE VALIDATION (Create + Update)
  // =============================
  function initPriceValidation(originalId, saleId, errorId, formId) {
    const orig = document.getElementById(originalId);
    const sale = document.getElementById(saleId);
    const error = document.getElementById(errorId);
    const form = document.getElementById(formId);
    if (!orig || !sale) return;

    // Add classes for visual feedback
    orig.classList.add("price-input");
    sale.classList.add("price-input");

    // Ensure error element exists and is properly styled
    if (error) {
      error.classList.add("mt-2");
      error.style.display = "none";
    }

    const validate = () => {
      const o = parseFloat(orig.value);
      const s = parseFloat(sale.value);

      // Reset validation state
      orig.classList.remove("is-invalid");
      sale.classList.remove("is-invalid");
      if (error) error.style.display = "none";

      // Skip validation if either field is empty or not a number
      if (isNaN(o) || isNaN(s)) return true;

      // Validate negative numbers
      if (o < 0 || s < 0) {
        if (error) {
          error.textContent = "⚠️ Giá không được âm.";
          error.style.display = "block";
        }
        if (o < 0) orig.classList.add("is-invalid");
        if (s < 0) sale.classList.add("is-invalid");
        return false;
      }

      // Validate sale price not greater than original
      if (o < s) {
        if (error) {
          error.textContent = "⚠️ Giá gốc phải lớn hơn hoặc bằng giá bán.";
          error.style.display = "block";
        }
        orig.classList.add("is-invalid");
        sale.classList.add("is-invalid");
        return false;
      }

      return true;
    };

    // Add input event listeners for real-time validation
    orig.addEventListener("input", validate);
    sale.addEventListener("input", validate);

    // Add submit validation
    form?.addEventListener("submit", (e) => {
      if (!validate()) {
        e.preventDefault();
        e.stopPropagation();
      }
    });

    // Initial validation
    validate();
  }

  // Initialize validation for create form
  initPriceValidation(
    "productOriginalPrice",
    "productSalePrice",
    "priceError",
    "addProductForm"
  );

  // Initialize validation for update form
  initPriceValidation(
    "productOriginalPriceUpdate",
    "productSalePriceUpdate",
    "priceErrorUpdate",
    "updateProductForm"
  );

  // For create form
  let createPriceError = document.getElementById("priceError");
  if (!createPriceError) {
    createPriceError = document.createElement("div");
    createPriceError.id = "priceError";
    createPriceError.className = "alert alert-danger mt-2";
    createPriceError.style.display = "none";
    document
      .getElementById("productSalePrice")
      ?.parentNode.appendChild(createPriceError);
  }

  // For update form
  let updatePriceError = document.getElementById("priceErrorUpdate");
  if (!updatePriceError && document.getElementById("productSalePriceUpdate")) {
    updatePriceError = document.createElement("div");
    updatePriceError.id = "priceErrorUpdate";
    updatePriceError.className = "alert alert-danger mt-2";
    updatePriceError.style.display = "none";
    document
      .getElementById("productSalePriceUpdate")
      .parentNode.appendChild(updatePriceError);
  }

  // =============================
  // DATE VALIDATION (Create + Update)
  // =============================
  function initDateValidation(inputId, formId) {
    const input = document.getElementById(inputId);
    const form = document.getElementById(formId);
    if (!input) return;

    let errorMsg = document.createElement("div");
    errorMsg.classList.add("invalid-feedback");
    errorMsg.style.display = "none";
    input.parentNode.appendChild(errorMsg);

    const validate = () => {
      const val = input.value;
      errorMsg.textContent = "";
      errorMsg.style.display = "none";
      if (!val) return;

      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const date = new Date(val);

      if (date > today) {
        input.classList.add("is-invalid");
        errorMsg.textContent = "⚠️ Ngày xuất bản không được lớn hơn hiện tại!";
        errorMsg.style.display = "block";
      } else input.classList.remove("is-invalid");
    };

    input.addEventListener("blur", validate);
    input.addEventListener("change", validate);
    form?.addEventListener("submit", (e) => {
      validate();
      if (input.classList.contains("is-invalid")) e.preventDefault();
    });
  }

  initDateValidation("publishedDate", "addProductForm");
  initDateValidation("publishedDateUpdate", "updateProductForm");

  // =============================
  // DELETE CONFIRM MODAL
  // =============================
  const deleteButtons = $$(".btn-delete");
  const deleteMsg = $("#deleteMessage");
  const deleteId = $("#deleteProductId");
  deleteButtons.forEach((btn) =>
    btn.addEventListener("click", () => {
      deleteMsg.innerHTML = `Bạn có chắc chắn muốn xóa sản phẩm <strong>"${btn.dataset.productTitle}"</strong>?`;
      deleteId.value = btn.dataset.productId;
    })
  );

  // =============================
  // POPULATE UPDATE MODAL (AJAX) - tích hợp với imageUpdateHandler
  // =============================
  const updateButtons = $$(".btn-update");
  updateButtons.forEach((btn) =>
    btn.addEventListener("click", () => {
      const id = btn.dataset.productId;
      fetch(`/shop/product?action=getProduct&id=${id}`)
        .then((r) => (r.ok ? r.json() : Promise.reject("Fetch error")))
        .then(populateUpdateModal)
        .catch((err) => {
          console.error(err);
          alert("Không thể tải thông tin sản phẩm!");
        });
    })
  );

  function populateUpdateModal(product) {
    const formatDate = (d) => new Date(d).toISOString().split("T")[0];
    const updateForm = $("#updateProductForm");
    if (updateForm)
      updateForm.action = `/shop/product?action=update&productId=${product.productId}`;

    $("#productTitleUpdate").value = product.title || "";
    $("#productDescriptionUpdate").value = product.description || "";
    $("#productOriginalPriceUpdate").value = product.originalPrice || "";
    $("#productSalePriceUpdate").value = product.salePrice || "";
    $("#productQuantityUpdate").value = product.quantity || "";
    $("#weightUpdate").value = product.weight || "";
    $("#publisherNameUpdate").value = product.publisher?.name || "";
    $("#publishedDateUpdate").value = product.publishedDate
      ? formatDate(product.publishedDate)
      : "";
    $("#translatorUpdate").value = product.bookDetail?.translator || "";
    $("#versionUpdate").value = product.bookDetail?.version || "";
    $("#coverTypeUpdate").value = product.bookDetail?.coverType || "";
    $("#pagesUpdate").value = product.bookDetail?.pages || "";
    $("#sizeUpdate").value = product.bookDetail?.size || "";
    $("#languageCodeUpdate").value = product.bookDetail?.languageCode || "";
    $("#isbnUpdate").value = product.bookDetail?.isbn || "";

    // Authors
    const authorsContainer = $("#authors-containerUpdate");
    authorsContainer.innerHTML = "";
    (product.authors && product.authors.length > 0
      ? product.authors
      : [{ authorName: "" }]
    ).forEach((a) => {
      const div = document.createElement("div");
      div.className = "input-group mb-2";
      div.innerHTML = `
        <input type="text" class="form-control" name="authorsUpdate" value="${
          a.authorName || ""
        }" required>
        <button type="button" class="btn btn-outline-danger btn-remove">🗑</button>`;
      authorsContainer.appendChild(div);
    });

    // Categories: uncheck all then check those from product
    $$('#updateProductModal input[name="CategoryIDs"]').forEach(
      (cb) => (cb.checked = false)
    );
    if (product.categories) {
      product.categories.forEach((cat) => {
        const cb = document.querySelector(
          `#updateProductModal input[name="CategoryIDs"][value="${cat.categoryId}"]`
        );
        if (cb) cb.checked = true;
      });
    }

    // Images: prepare structure for imageUpdateHandler
    const images = (product.images || []).map((img, idx) => ({
      id: img.imageId,
      url: img.url
        ? `http://localhost:8080/assets/images/catalog/products/${img.url}`
        : "",
      isPrimary: !!img.isPrimary || idx === 0,
      isNew: false,
      file: null,
    }));
    if (imageUpdateHandler && imageUpdateHandler.setInitialImages) {
      imageUpdateHandler.setInitialImages(images);
    }
    // reset file input value to empty (so change event will contain only newly selected files)
    const fi = document.getElementById("productImagesUpdate");
    if (fi) fi.value = "";
  }
});
