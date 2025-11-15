document.addEventListener("DOMContentLoaded", function () {
  const categoryFilter = document.getElementById("categoryFilter");
  const statusFilter = document.getElementById("statusFilter");
  const searchInput = document.getElementById("searchProduct");
  const table = document.getElementById("datatablesSimple");
  const rows = table.querySelectorAll("tbody tr");

  function normalize(str) {
    return str
      .toLowerCase()
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/\s+/g, " ")
      .trim();
  }

  function filterTable() {
    const selectedCategory =
      categoryFilter.options[categoryFilter.selectedIndex].text.trim();
    const selectedStatusText =
      statusFilter.options[statusFilter.selectedIndex].text.trim();
    const selectedStatus =
      selectedStatusText === "Tất cả trạng thái"
        ? ""
        : normalize(selectedStatusText);
    const searchText = normalize(searchInput.value.trim());

    rows.forEach((row) => {
      const categoryCell = row.querySelector("td:nth-child(2)");
      const categoryText = normalize(
        categoryCell.getAttribute("data-categories") || categoryCell.textContent
      );

      const statusBadge = row.querySelector("td:nth-child(5) .badge");
      const statusText = statusBadge ? normalize(statusBadge.textContent) : "";
      const titleText = normalize(
        row.querySelector("td:nth-child(1) .fw-bold").textContent
      );

      const matchCategory =
        !selectedCategory ||
        selectedCategory === "Tất cả danh mục" ||
        categoryText.includes(normalize(selectedCategory));
      const matchStatus =
        !selectedStatus || statusText.includes(selectedStatus);
      const matchSearch = !searchText || titleText.includes(searchText);

      row.style.display =
        matchCategory && matchStatus && matchSearch ? "" : "none";
    });
  }

  categoryFilter.addEventListener("change", filterTable);
  statusFilter.addEventListener("change", filterTable);
  searchInput.addEventListener("keyup", filterTable);

  // =============================
  // CATEGORY SELECTION LOGIC
  // =============================

  // 🟢 1. Modal CREATE – chỉ chọn thể loại chính 1 lần, không auto đổi
  function initCategoryModalCreate(modalSelector, formSelector, hiddenInputId) {
    const modal = document.querySelector(modalSelector);
    if (!modal) return;

    const checkboxes = modal.querySelectorAll("input[name='CategoryIDs']");
    const container = modal.querySelector(".border.rounded.p-3");
    const hiddenInput = document.getElementById(hiddenInputId);

    // Tạo phần hiển thị lỗi nếu chưa có
    let errorMsg = container.nextElementSibling;
    if (!errorMsg || !errorMsg.classList.contains("text-danger")) {
      errorMsg = document.createElement("div");
      errorMsg.className = "text-danger mt-2";
      errorMsg.style.display = "none";
      errorMsg.textContent = "Vui lòng chọn ít nhất một thể loại chính.";
      container.insertAdjacentElement("afterend", errorMsg);
    }

    let mainCategory = null;

    // 🟢 Gắn nhãn “(Thể loại chính)”
    function setMainCategory(cb) {
      // Xóa nhãn cũ
      modal.querySelectorAll(".primary-label").forEach((el) => el.remove());
      checkboxes.forEach((c) => c.classList.remove("main-category"));

      const label = modal.querySelector(`label[for='${cb.id}']`);
      if (!label) return;

      const note = document.createElement("span");
      note.textContent = " (Thể loại chính)";
      note.classList.add("text-success", "fw-bold", "primary-label");
      label.appendChild(note);

      cb.classList.add("main-category");
      hiddenInput.value = cb.value;
      mainCategory = cb;

      hideError();
    }

    // 🔵 Xóa nhãn
    function clearMainCategory() {
      modal.querySelectorAll(".primary-label").forEach((el) => el.remove());
      checkboxes.forEach((c) => c.classList.remove("main-category"));
      hiddenInput.value = "";
      mainCategory = null;
    }

    function showError() {
      errorMsg.style.display = "block";
      container.classList.add("border-danger");
    }

    function hideError() {
      errorMsg.style.display = "none";
      container.classList.remove("border-danger");
    }

    // 🧠 Tick checkbox: chỉ chọn thể loại chính lần đầu
    checkboxes.forEach((cb) => {
      cb.addEventListener("change", () => {
        const label = modal.querySelector(`label[for='${cb.id}']`);

        if (cb.checked && !mainCategory) {
          setMainCategory(cb);
        } else if (!cb.checked && mainCategory === cb) {
          clearMainCategory();
        }
      });
    });

    // ⚠️ Validate khi submit
    const form = document.querySelector(formSelector);
    form.addEventListener("submit", (e) => {
      if (!mainCategory) {
        e.preventDefault();
        showError();
        container.scrollIntoView({ behavior: "smooth", block: "center" });
      }
    });
  }

  // 🟣 2. Modal UPDATE – cho phép thay đổi hoặc bỏ chọn linh hoạt
  function initCategoryModalUpdate(modalSelector, formSelector, hiddenInputId) {
    const modal = document.querySelector(modalSelector);
    if (!modal) return;

    const checkboxes = modal.querySelectorAll("input[name='CategoryIDs']");
    const container = modal.querySelector(".border.rounded.p-3");
    const hiddenInput = document.getElementById(hiddenInputId);

    // Tạo phần hiển thị lỗi nếu chưa có
    let errorMsg = container.nextElementSibling;
    if (!errorMsg || !errorMsg.classList.contains("text-danger")) {
      errorMsg = document.createElement("div");
      errorMsg.className = "text-danger mt-2";
      errorMsg.style.display = "none";
      errorMsg.textContent = "Vui lòng chọn ít nhất một thể loại chính.";
      container.insertAdjacentElement("afterend", errorMsg);
    }

    let mainCategory = null;

    // 🟢 Gắn nhãn “(Thể loại chính)”
    function setMainCategory(cb) {
      modal.querySelectorAll(".primary-label").forEach((el) => el.remove());
      checkboxes.forEach((c) => c.classList.remove("main-category"));

      const label = modal.querySelector(`label[for='${cb.id}']`);
      if (!label) return;

      const note = document.createElement("span");
      note.textContent = " (Thể loại chính)";
      note.classList.add("text-success", "fw-bold", "primary-label");
      label.appendChild(note);

      cb.classList.add("main-category");
      hiddenInput.value = cb.value;
      mainCategory = cb;
      hideError();
    }

    // 🔵 Xóa nhãn
    function clearMainCategory() {
      modal.querySelectorAll(".primary-label").forEach((el) => el.remove());
      checkboxes.forEach((c) => c.classList.remove("main-category"));
      hiddenInput.value = "";
      mainCategory = null;
    }

    function showError() {
      errorMsg.style.display = "block";
      container.classList.add("border-danger");
    }

    function hideError() {
      errorMsg.style.display = "none";
      container.classList.remove("border-danger");
    }

    // 🧠 Tick checkbox: cho phép thay đổi linh hoạt
    checkboxes.forEach((cb) => {
      cb.addEventListener("change", () => {
        const label = modal.querySelector(`label[for='${cb.id}']`);
        if (cb.checked) {
          setMainCategory(cb);
        } else if (!cb.checked && mainCategory === cb) {
          clearMainCategory();
        }
      });
    });

    // Cho phép click lại label để đặt làm thể loại chính
    modal.addEventListener("click", (e) => {
      const label = e.target.closest("label");
      if (!label) return;
      const cb = label.querySelector("input[name='CategoryIDs']");
      if (cb && cb.checked) {
        setMainCategory(cb);
      }
    });

    // ⚠️ Validate khi submit
    const form = document.querySelector(formSelector);
    form.addEventListener("submit", (e) => {
      if (!mainCategory) {
        e.preventDefault();
        showError();
        container.scrollIntoView({ behavior: "smooth", block: "center" });
      }
    });

    // ✅ Hàm public để đồng bộ dữ liệu khi mở modal update
    window.setMainCategoryForModal = function (modalSel, categoryId) {
      const modalEl = document.querySelector(modalSel);
      if (!modalEl) return;
      const cb = modalEl.querySelector(
        `input[name='CategoryIDs'][value='${categoryId}']`
      );
      if (cb) setMainCategory(cb);
    };
  }

  // =============================
  // INIT FOR BOTH MODALS
  // =============================
  initCategoryModalCreate(
    "#addProductModal",
    "#addProductForm",
    "PrimaryCategoryID"
  );
  initCategoryModalUpdate(
    "#updateProductModal",
    "#updateProductForm",
    "PrimaryCategoryIDUpdate"
  );
});
