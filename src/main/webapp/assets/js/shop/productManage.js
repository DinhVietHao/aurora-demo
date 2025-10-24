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
});
