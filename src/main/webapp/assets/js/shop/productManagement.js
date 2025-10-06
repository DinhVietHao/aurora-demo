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

document.addEventListener("DOMContentLoaded", () => {

    const modal = document.getElementById("updateProductModal");
    let data = null;

    document.querySelectorAll(".btn-update").forEach((btn) => {
        btn.addEventListener("click", (e) => {
            e.preventDefault();

            data = btn.dataset;

            new bootstrap.Modal(modal).show();
        });
    });

    modal.addEventListener("shown.bs.modal", () => {
        document.getElementById("productTitle").value = data.title;
        document.getElementById("productDescription").value = data.description;
        document.getElementById("productOriginalPrice").value =
            data.originalPrice;
    });
});
