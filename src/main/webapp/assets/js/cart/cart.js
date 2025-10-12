/* global bootstrap */

// ====================== CART CALCULATION ======================
const minusBtn = document.querySelectorAll(".minus");
const plusBtn = document.querySelectorAll(".plus");

const totalProductPrice = document.querySelector(
    ".cart-pay .total-product-price"
);
const discountElement = document.querySelector(".cart-pay .discount");
const shipElement = document.querySelector(".cart-pay .ship-discount");
const totalPayment = document.querySelector(".cart-pay .total-payment");
const shippingFeePayment = document.querySelector(".cart-pay .shipping-fee");
const buyButton = document.querySelector(".cart-pay #cart-pay-button");

const selectAllCheckbox = document.querySelector(
    ".cart-header .cart-checkboxAll"
);
const productCheckboxes = document.querySelectorAll(
    ".cart-body .cart-checkbox"
);

const deleteCartItem = document.querySelectorAll(".button-delete");
const confirmDeleteCartItem = document.getElementById("confirmDeleteCartItem");

// ====================== FORMAT PRICE ======================
function formatCurrency(num) {
    return num.toLocaleString("vi-VN") + "đ";
}

// ====================== CALCULATE TOTAL ======================
function calculateTotalProduct() {
    let total = 0;
    const cartBodies = document.querySelectorAll(".cart-body");
    cartBodies.forEach((row) => {
        const priceElement = row.querySelector(".unit-price");
        const quantityElement = row.querySelector(".number");
        const subtotalElement = row.querySelector(".subtotal");
        const checkbox = row.querySelector(".cart-checkbox");

        if (
            priceElement &&
            quantityElement &&
            subtotalElement &&
            checkbox &&
            checkbox.checked
        ) {
            let price = parseInt(
                priceElement.textContent.replace(/\D/g, "") || 0
            );
            let quantity = parseInt(quantityElement.textContent) || 1;
            let subtotal = price * quantity;
            subtotalElement.textContent = formatCurrency(subtotal);
            total += subtotal;
        }
    });
    return total;
}

// ====================== VOUCHER CALCULATION ======================
function calculateDiscount(total) {
    const selectedDiscount = document.querySelector(
        'input[name="voucherDiscount"]:checked'
    );
    if (selectedDiscount) {
        let type = selectedDiscount.dataset.type;
        if (type === "percent") {
            let percent = parseInt(selectedDiscount.dataset.discount) || 0;
            let max = parseInt(selectedDiscount.dataset.max) || 0;
            return Math.min((total * percent) / 100, max);
        } else {
            return parseInt(selectedDiscount.dataset.discount) || 0;
        }
    }
    return 0;
}

function calculateShipDiscount() {
    const selectedShip = document.querySelector(
        'input[name="voucherShip"]:checked'
    );
    if (selectedShip) {
        return parseInt(selectedShip.dataset.ship) || 0;
    }
    return 0;
}

// ====================== PRODUCT QUANTITY CALCULATION ======================

function calculateTotalProducts() {
    let totalProducts = 0;
    const cartBodies = document.querySelectorAll(".cart-body");
    cartBodies.forEach((row) => {
        const checkbox = row.querySelector(".cart-checkbox");
        if (checkbox && checkbox.checked) {
            totalProducts++;
        }
    });
    return totalProducts;
}

function updateBuyButton() {
    const totalItems = calculateTotalProducts();
    buyButton.textContent = `Mua Hàng (${totalItems})`;
}

// ====================== UPDATE SUMMARY ======================
function updateCartSummary() {
    let total = calculateTotalProduct();
    let discount = calculateDiscount(total);
    let ship = calculateShipDiscount();

    discountElement.innerText = "-" + formatCurrency(discount);
    shipElement.innerText = "-" + formatCurrency(ship);

    // Shipping Fee
    let shippingFee =
        parseInt(shippingFeePayment.textContent.replace(/\D/g, "")) || 30000;

    totalProductPrice.textContent = formatCurrency(total);
    let sum = Math.max(total + shippingFee - discount - ship, 0);
    totalPayment.textContent = formatCurrency(sum);

    updateBuyButton();
}

// ====================== CHECKBOX HANDLER ======================
selectAllCheckbox.addEventListener("change", () => {
    const checked = selectAllCheckbox.checked;
    const cartBody = document.querySelector(".cart-body");
    const cartId = cartBody.dataset.cartid;

    productCheckboxes.forEach((cb) => (cb.checked = checked));

    fetch("update-cart-all-checked", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `cartId=${cartId}&checked=${checked}`,
    });

    updateCartSummary();
});

document.addEventListener("change", (e) => {
    if (e.target.matches(".cart-checkbox")) {
        const checkbox = e.target;
        const row = checkbox.closest(".cart-body");
        const cartItemId = row.dataset.cartitemid;
        const cartId = row.dataset.cartid;
        const checked = checkbox.checked;

        fetch("update-cart-item-checked", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `cartItemId=${cartItemId}&checked=${checked}`,
        });

        const allChecked = [...productCheckboxes].every((cb) => cb.checked);
        selectAllCheckbox.checked = allChecked;

        updateCartSummary();
    }
});

window.addEventListener("DOMContentLoaded", () => {
    if (productCheckboxes.length > 0) {
        const allChecked = [...productCheckboxes].every((cb) => cb.checked);
        selectAllCheckbox.checked = allChecked;
    } else {
        selectAllCheckbox.checked = false;
    }
});

// ====================== QUANTITY HANDLER ======================
minusBtn.forEach((btn) => {
    btn.addEventListener("click", () => {
        const number = btn.nextElementSibling;
        let quantity = parseInt(number.textContent);
        if (quantity > 1) {
            quantity--;

            const row = btn.closest(".cart-body");
            const cartItemId = row.dataset.cartitemid;

            fetch("update-cart-item-quantity", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: `cartItemId=${cartItemId}&quantity=${quantity}`,
            })
                .then((res) => res.json())
                .then((data) => {
                    if (data.success) {
                        number.textContent = quantity;
                        updateCartSummary();
                    } else {
                        alert(data.message);
                    }
                });
        } else {
            const deleteCartModalEl =
                document.getElementById("deleteCartModal");
            deleteCartModalEl.dataset.cartitemid = btn.dataset.cartitemid;
            deleteCartModalEl.dataset.cartid = btn.dataset.cartid;

            const deleteCartModal = new bootstrap.Modal(deleteCartModalEl);
            deleteCartModal.show();
        }
    });
});

plusBtn.forEach((btn) => {
    btn.addEventListener("click", () => {
        const number = btn.previousElementSibling;
        let quantity = parseInt(number.textContent);
        quantity++;

        const row = btn.closest(".cart-body");
        const cartItemId = row.dataset.cartitemid;

        fetch("update-cart-item-quantity", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `cartItemId=${cartItemId}&quantity=${quantity}`,
        })
            .then((res) => res.json())
            .then((data) => {
                if (data.success) {
                    number.textContent = quantity;
                    updateCartSummary();
                } else {
                    alert(data.message);
                }
            });
    });
});

updateCartSummary();

// ====================== VOUCHER HANDLER ======================
const confirmVoucher = document.getElementById("confirmVoucher");

const appliedVoucherDiscount = document.getElementById(
    "appliedVoucherDiscount"
);
const voucherTextDiscount = document.getElementById("voucherTextDiscount");

const appliedVoucherShip = document.getElementById("appliedVoucherShip");
const voucherTextShip = document.getElementById("voucherTextShip");

const removeVoucherDiscount = document.getElementById("removeVoucherDiscount");
const removeVoucherShip = document.getElementById("removeVoucherShip");

confirmVoucher.addEventListener("click", () => {
    const selectedDiscount = document.querySelector(
        'input[name="voucherDiscount"]:checked'
    );
    const selectedShip = document.querySelector(
        'input[name="voucherShip"]:checked'
    );

    let textDiscount = "";
    let textShip = "";

    if (selectedDiscount) {
        textDiscount = selectedDiscount.dataset.text;
        voucherTextDiscount.innerText = textDiscount;
        appliedVoucherDiscount.classList.remove("d-none");
    }

    if (selectedShip) {
        textShip = selectedShip.dataset.text;
        voucherTextShip.innerText = textShip;
        appliedVoucherShip.classList.remove("d-none");
    }
    const VoucherModal = bootstrap.Modal.getInstance(
        document.getElementById("voucherModal")
    );
    VoucherModal.hide();
    updateCartSummary();
});

removeVoucherDiscount.addEventListener("click", () => {
    const selectedDiscount = document.querySelector(
        'input[name="voucherDiscount"]:checked'
    );
    if (selectedDiscount) selectedDiscount.checked = false;

    voucherTextDiscount.innerText = "";
    appliedVoucherDiscount.classList.add("d-none");
    discountElement.innerText = "-0đ";
    updateCartSummary();
});

removeVoucherShip.addEventListener("click", () => {
    const selectedShip = document.querySelector(
        'input[name="voucherShip"]:checked'
    );
    if (selectedShip) selectedShip.checked = false;

    voucherTextShip.innerText = "";
    appliedVoucherShip.classList.add("d-none");
    shipElement.innerText = "-0đ";
    updateCartSummary();
});

// ====================== DELETE CARTITEM HANDLER ======================

deleteCartItem.forEach((btn) => {
    btn.addEventListener("click", () => {
        const deleteCartModal = document.getElementById("deleteCartModal");
        deleteCartModal.dataset.cartitemid = btn.dataset.cartitemid;
        deleteCartModal.dataset.cartid = btn.dataset.cartid;
    });
});
confirmDeleteCartItem.addEventListener("click", () => {
    const deleteCartModalEl = document.getElementById("deleteCartModal");
    const cartItemId = deleteCartModalEl.dataset.cartitemid;
    const cartId = deleteCartModalEl.dataset.cartid;

    fetch("delete-cart-item", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `cartItemId=${cartItemId}&cartId=${cartId}`,
    })
        .then((res) => res.json())
        .then((data) => {
            if (data.success) {
                const deleteCartModal =
                    bootstrap.Modal.getInstance(deleteCartModalEl);
                deleteCartModal.hide();

                const rowCartBody = document.getElementById(
                    `cartItemId${cartItemId}`
                );
                if (rowCartBody) {
                    rowCartBody.remove();
                }

                const cartCountBadge =
                    document.getElementById("cartCountBadge");
                if (cartCountBadge) {
                    cartCountBadge.innerText = data.cartCount;
                }

                updateCartSummary();
            }
        });
});

// ====================== Handling when users buy without selecting a product ======================
buyButton.addEventListener("click", () => {
    const hasChecked = [...productCheckboxes].some((cb) => cb.checked);
    if (!hasChecked) {
        const emptySelection = document.getElementById("emptySelectionModal");
        const emptySelectionModal = new bootstrap.Modal(emptySelection);
        emptySelectionModal.show();
    }
});
