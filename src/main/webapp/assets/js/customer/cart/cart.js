// ====================== CART CALCULATION ======================
const minusBtn = document.querySelectorAll(".minus");
const plusBtn = document.querySelectorAll(".plus");
const cartBodies = document.querySelectorAll(".cart-body .cart-body__item");

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

// ====================== FORMAT PRICE ======================
function formatCurrency(num) {
  return num.toLocaleString("vi-VN") + "đ";
}

// ====================== CALCULATE TOTAL ======================
function calculateTotalProduct() {
  let total = 0;
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
      let price = parseInt(priceElement.textContent.replace(/\D/g, "") || 0);
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
    const minOrderAmount =
      parseInt(selectedDiscount.dataset.minOrderAmount) || 0;
    if (total < minOrderAmount) {
      return 0;
    }
    let type = selectedDiscount.dataset.type;
    if (type === "PERCENT") {
      let percent = parseInt(selectedDiscount.dataset.discount) || 0;
      let max = parseInt(selectedDiscount.dataset.max) || 0;
      return Math.min((total * percent) / 100, max);
    } else {
      return parseInt(selectedDiscount.dataset.discount) || 0;
    }
  }
  return 0;
}

// ====================== PRODUCT QUANTITY CALCULATION ======================

function calculateTotalQuantityProducts() {
  let totalProducts = 0;
  cartBodies.forEach((row) => {
    const checkbox = row.querySelector(".cart-checkbox");
    if (checkbox && checkbox.checked) {
      totalProducts++;
    }
  });
  return totalProducts;
}

function updateBuyButton() {
  const totalItems = calculateTotalQuantityProducts();
  buyButton.textContent = `Mua Hàng (${totalItems})`;
}
// ====================== GET ALL DISCOUNTS ======================
function getAllDiscounts(total) {
  let systemDiscount = calculateDiscount(total);

  let allShopDiscount = 0;
  const allShop = document.querySelectorAll(".cart-body[data-shop-id]");
  allShop.forEach((shopElement) => {
    const shopId = shopElement.getAttribute("data-shop-id");
    const selectedShopDiscount = document.querySelector(
      `input[name="voucherShopDiscount_${shopId}"]:checked`
    );

    if (selectedShopDiscount && shopId) {
      const shopTotal = calculateShopTotal(shopId);
      const shopDiscount = calculateShopDiscount(shopTotal, shopId);
      allShopDiscount += shopDiscount;
      const shopVoucher = document.querySelector(
        `.cart-body__footer[data-shop-id="${shopId}"] .shop-voucher-text`
      );
      if (shopVoucher) {
        shopVoucher.innerText =
          shopDiscount > 0
            ? `Đã giảm ${formatCurrency(shopDiscount)}`
            : "Chưa áp dụng";
      }
    }
  });

  return { systemDiscount, allShopDiscount };
}
// ====================== UPDATE SUMMARY ======================
function updateCartSummary() {
  let total = calculateTotalProduct();
  let { systemDiscount, allShopDiscount } = getAllDiscounts(total);

  discountElement.innerText =
    "-" + formatCurrency(systemDiscount + allShopDiscount);

  totalProductPrice.textContent = formatCurrency(total);
  let sum = Math.max(total - systemDiscount - allShopDiscount);
  totalPayment.textContent = formatCurrency(sum);

  // refresh lại voucher của tất cả shop mỗi lần update
  refreshShopVoucher();
  updateBuyButton();
}

// ====================== CHECKBOX HANDLER ======================
document.addEventListener("DOMContentLoaded", () => {
  const allChecked =
    productCheckboxes.length > 0 &&
    [...productCheckboxes].every((cb) => cb.checked);

  selectAllCheckbox.checked = allChecked;
});
selectAllCheckbox.addEventListener("change", () => {
  const checked = selectAllCheckbox.checked;
  const row = document.querySelector(".cart-body__item");
  const userId = row.dataset.userid;

  productCheckboxes.forEach((cb) => (cb.checked = checked));

  fetch("cart/check-all", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `userId=${userId}&checked=${checked}`,
  });

  updateCartSummary();
});

document.addEventListener("change", (e) => {
  if (e.target.matches(".cart-checkbox")) {
    const checkbox = e.target;
    const row = checkbox.closest(".cart-body__item");
    const cartItemId = row.dataset.cartitemid;
    const checked = checkbox.checked;

    fetch("cart/update-check", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `cartItemId=${cartItemId}&checked=${checked}`,
    });

    const allChecked = [...productCheckboxes].every((cb) => cb.checked);
    selectAllCheckbox.checked = allChecked;
    updateCartSummary();
  }
});
// ====================== QUANTITY HANDLER ======================
minusBtn.forEach((btn) => {
  btn.addEventListener("click", () => {
    const number = btn.nextElementSibling;
    let quantity = parseInt(number.textContent);
    if (quantity > 1) {
      quantity--;
      const row = btn.closest(".cart-body__item");
      const cartItemId = row.dataset.cartitemid;

      fetch("cart/update-quantity", {
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
      const deleteCartModalEl = document.getElementById("deleteCartModal");
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
    const row = btn.closest(".cart-body__item");
    const cartItemId = row.dataset.cartitemid;

    fetch("cart/update-quantity", {
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
  updateCartSummary();
});

removeVoucherShip.addEventListener("click", () => {
  const selectedShip = document.querySelector(
    'input[name="voucherShip"]:checked'
  );
  if (selectedShip) selectedShip.checked = false;

  voucherTextShip.innerText = "";
  appliedVoucherShip.classList.add("d-none");
  updateCartSummary();
});

// ====================== CALCULATE SHOP TOTAL ======================
function calculateShopTotal(shopId) {
  const shopElement = document.querySelector(
    `.cart-body[data-shop-id="${shopId}"]`
  );
  if (!shopElement) return 0;

  let shopTotal = 0;

  const items = shopElement.querySelectorAll(".cart-body__item");

  items.forEach((item) => {
    const checkbox = item.querySelector(".cart-checkbox");
    const priceElement = item.querySelector(".unit-price");
    const quantityElement = item.querySelector(".number");

    if (checkbox && checkbox.checked && priceElement && quantityElement) {
      let price = parseInt(priceElement.textContent.replace(/\D/g, "")) || 0;
      let quantity = parseInt(quantityElement.textContent) || 1;
      shopTotal += price * quantity;
    }
  });
  return shopTotal;
}
// ====================== VOUCHER SHOP CALCULATION ======================
function calculateShopDiscount(total, shopId) {
  const selectedShopDiscount = document.querySelector(
    `input[name="voucherShopDiscount_${shopId}"]:checked`
  );
  if (selectedShopDiscount) {
    const minOrderAmount =
      parseInt(selectedShopDiscount.dataset.minOrderAmount) || 0;
    if (total < minOrderAmount) {
      return 0;
    }
    let type = selectedShopDiscount.dataset.type;
    if (type === "PERCENT") {
      let percent = parseInt(selectedShopDiscount.dataset.discount) || 0;
      let max = parseInt(selectedShopDiscount.dataset.max) || 0;
      return Math.min((total * percent) / 100, max);
    } else {
      return parseInt(selectedShopDiscount.dataset.discount) || 0;
    }
  }
  return 0;
}

// ====================== VOUCHER SHOP HANDLER ======================
const confirmShopVoucher = document.querySelectorAll(".confirmShopVoucher");
const shopVoucherModal = document.querySelectorAll('[data-bs-toggle="modal"]');

shopVoucherModal.forEach((btn) => {
  btn.addEventListener("click", () => {
    const shopId = btn.getAttribute("data-shop-id");
    toggleVoucherAvailability(shopId);
  });
});

confirmShopVoucher.forEach((btn) => {
  btn.addEventListener("click", () => {
    const shopId = btn.getAttribute("data-shop-id");
    const selectedShopDiscount = document.querySelector(
      `input[name="voucherShopDiscount_${shopId}"]:checked`
    );

    console.log("check shopid ", shopId);

    if (selectedShopDiscount) {
      const shopVoucher = document.querySelector(
        `.cart-body__footer[data-shop-id="${shopId}"] .shop-voucher-text`
      );
      if (shopVoucher) {
        const shopTotal = calculateShopTotal(shopId);
        const discount = calculateShopDiscount(shopTotal, shopId);
        shopVoucher.innerText =
          discount > 0 ? `Đã giảm ${formatCurrency(discount)}` : "Chưa áp dụng";
      }
    }
    const shopVoucherModalEl = document.getElementById(
      `shopVoucherModal_${shopId}`
    );
    const shopVoucherModal = bootstrap.Modal.getInstance(shopVoucherModalEl);
    shopVoucherModal.hide();

    updateCartSummary();
  });
});
// ====================== Check if user is condition to apply Voucher ======================
function toggleVoucherAvailability(shopId) {
  const shopTotal = calculateShopTotal(shopId);
  const vouchers = document.querySelectorAll(
    `input[name="voucherShopDiscount_${shopId}"]`
  );

  vouchers.forEach((voucher) => {
    const minOrderAmount = parseInt(voucher.dataset.minOrderAmount) || 0;

    const label = voucher.closest("label");
    if (shopTotal < minOrderAmount) {
      if (voucher.checked) {
        const shopVoucher = document.querySelector(
          `.cart-body__footer[data-shop-id="${shopId}"] .shop-voucher-text`
        );
        if (shopVoucher) shopVoucher.innerText = "Chưa đủ điều kiện";
      }
      label.classList.add("disabled");
    } else {
      label.classList.remove("disabled");
    }
  });
}

// ================== TOGGLE SYSTEM VOUCHERS ==================
function toggleSystemVoucherAvailability() {
  const total = calculateTotalProduct();
  const discountVouchers = document.querySelectorAll(
    'input[name="voucherDiscount"]'
  );

  // Xử lý voucher giảm giá sản phẩm
  discountVouchers.forEach((voucher) => {
    const minOrderAmount = parseInt(voucher.dataset.minOrderAmount) || 0;
    const label = voucher.closest("label");
    if (total < minOrderAmount) {
      if (voucher.checked) {
        voucherTextDiscount.innerText = "Chưa đủ điều kiện";
      }
      label.classList.add("disabled");
    } else {
      label.classList.remove("disabled");
      if (voucher.checked) {
        voucherTextDiscount.innerText = voucher.dataset.text;
      }
    }
  });

  // Xử lý voucher giảm phí ship
  const shipVouchers = document.querySelectorAll('input[name="voucherShip"]');
  shipVouchers.forEach((voucher) => {
    const minOrderAmount = parseInt(voucher.dataset.minOrderAmount) || 0;
    const label = voucher.closest("label");
    if (total < minOrderAmount) {
      if (voucher.checked) {
        voucherTextShip.innerText = "Chưa đủ điều kiện ";
      }
      label.classList.add("disabled");
    } else {
      label.classList.remove("disabled");
      if (voucher.checked) {
        voucherTextShip.innerText = voucher.dataset.text;
      }
    }
  });
}

// ====================== REFRESH SHOP VOUCHER ======================
function refreshShopVoucher() {
  const shopVouchers = document.querySelectorAll(".cart-body[data-shop-id]");
  shopVouchers.forEach((shop) => {
    const shopId = shop.getAttribute("data-shop-id");
    toggleVoucherAvailability(shopId);
  });
  toggleSystemVoucherAvailability();
}
// ====================== Handling when users buy without selecting a product ======================
buyButton.addEventListener("click", () => {
  const hasChecked = [...productCheckboxes].some((cb) => cb.checked);
  if (!hasChecked) {
    const emptySelection = document.getElementById("emptySelectionModal");
    const emptySelectionModal = new bootstrap.Modal(emptySelection);
    emptySelectionModal.show();
    return;
  }

  window.location.href = "/checkout";
});

updateCartSummary();

// ====================== DELETE CARTITEM HANDLER ======================
const deleteCartItem = document.querySelectorAll(".button-delete");
const confirmDeleteCartItem = document.getElementById("confirmDeleteCartItem");
deleteCartItem.forEach((btn) => {
  btn.addEventListener("click", () => {
    const deleteCartModal = document.getElementById("deleteCartModal");
    deleteCartModal.dataset.cartitemid = btn.dataset.cartitemid;
  });
});
confirmDeleteCartItem.addEventListener("click", () => {
  const deleteCartModalEl = document.getElementById("deleteCartModal");
  const cartItemId = deleteCartModalEl.dataset.cartitemid;

  fetch("/cart/delete", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `cartItemId=${cartItemId}`,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        const deleteCartModal = bootstrap.Modal.getInstance(deleteCartModalEl);
        deleteCartModal.hide();

        const rowCartBodyItem = document.getElementById(
          `cartItemId${cartItemId}`
        );
        if (rowCartBodyItem) {
          const shopBody = rowCartBodyItem.closest(".cart-body");
          rowCartBodyItem.remove();
          if (shopBody.querySelectorAll(".cart-body__item").length === 0) {
            shopBody.remove();
          }

          if (document.querySelectorAll(".cart-body").length === 0) {
            const cartLeft = document.querySelector(".cart-left");
            cartLeft.innerHTML = `
            <div class="text-center">
              <img src="http://localhost:8080/assets/images/common/cartEmpty.png" alt="Cart Empty">
              <p class="text-muted">Giỏ hàng trống</p>
              <p>Bạn tham khảo thêm các sản phẩm được Aurora gợi ý bên dưới nhé!</p>
            </div>`;
          }
        }

        const cartCountBadge = document.getElementById("cartCountBadge");
        if (cartCountBadge) {
          cartCountBadge.innerText = data.cartCount;
        }
        updateCartSummary();
      }
    });
});
