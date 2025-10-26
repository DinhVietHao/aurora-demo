// ====================== CART CALCULATION ======================
const cartBodies = document.querySelectorAll(".cart-body .cart-body__item");

const totalProductPrice = document.querySelector(
  ".cart-pay .total-product-price"
);
const discountElement = document.querySelector(".cart-pay .discount");
const shipElement = document.querySelector(".cart-pay .ship-discount");
const totalPayment = document.querySelector(".cart-pay .total-payment");
const shippingFeePayment = document.querySelector(".cart-pay .shipping-fee");

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

function calculateShipDiscount(total) {
  const selectedShip = document.querySelector(
    'input[name="voucherShip"]:checked'
  );
  if (selectedShip) {
    const minOrderAmount = parseInt(selectedShip.dataset.minOrderAmount) || 0;
    if (total < minOrderAmount) {
      return 0;
    }
    return parseInt(selectedShip.dataset.ship) || 0;
  }
  return 0;
}

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
    localStorage.setItem(
      "systemVoucherDiscount",
      selectedDiscount.dataset.value
    );
    textDiscount = selectedDiscount.dataset.text;
    voucherTextDiscount.innerText = textDiscount;
    appliedVoucherDiscount.classList.remove("d-none");
  }

  if (selectedShip) {
    localStorage.setItem("systemVoucherShip", selectedShip.dataset.value);
    textShip = selectedShip.dataset.text;
    voucherTextShip.innerText = textShip;
    appliedVoucherShip.classList.remove("d-none");
  }
  const VoucherModal = bootstrap.Modal.getInstance(
    document.getElementById("voucherModal")
  );
  VoucherModal.hide();
  syncWithServer();
});

removeVoucherDiscount.addEventListener("click", () => {
  const selectedDiscount = document.querySelector(
    'input[name="voucherDiscount"]:checked'
  );
  if (selectedDiscount) {
    selectedDiscount.checked = false;
    localStorage.removeItem("systemVoucherDiscount");
  }
  voucherTextDiscount.innerText = "";
  appliedVoucherDiscount.classList.add("d-none");
  syncWithServer();
});

removeVoucherShip.addEventListener("click", () => {
  const selectedShip = document.querySelector(
    'input[name="voucherShip"]:checked'
  );
  if (selectedShip) {
    selectedShip.checked = false;
    localStorage.removeItem("systemVoucherShip");
  }

  voucherTextShip.innerText = "";
  appliedVoucherShip.classList.add("d-none");
  syncWithServer();
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

    if (selectedShopDiscount) {
      const code = selectedShopDiscount.dataset.value;
      localStorage.setItem(`shopVoucher_${shopId}`, code);
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
    syncWithServer();
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
// ====================== SYNC SERVER ======================

function syncWithServer() {
  const params = new URLSearchParams();
  const addressId =
    document.querySelector(`[name='addressId']:checked`)?.value || "";
  params.append("addressId", addressId);

  const selectedDiscount = document.querySelector(
    'input[name="voucherDiscount"]:checked'
  );
  if (selectedDiscount) {
    params.append("systemVoucherDiscount", selectedDiscount.value);
  }

  const selectedShip = document.querySelector(
    'input[name="voucherShip"]:checked'
  );
  if (selectedShip) {
    params.append("systemVoucherShip", selectedShip.value);
  }

  document.querySelectorAll(".cart-body[data-shop-id]").forEach((shop) => {
    const shopId = shop.dataset.shopId;
    const selectedShopDiscount = document.querySelector(
      `input[name="voucherShopDiscount_${shopId}"]:checked`
    );
    if (selectedShopDiscount) {
      params.append(`shopVoucher_${shopId}`, selectedShopDiscount.value);
    }
  });

  fetch("/checkout/update-summary", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString(),
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        totalProductPrice.textContent = formatCurrency(data.totalProduct);
        shippingFeePayment.textContent = formatCurrency(data.totalShippingFee);
        discountElement.textContent = "-" + formatCurrency(data.totalDiscount);
        shipElement.textContent = "-" + formatCurrency(data.shipDiscount);
        totalPayment.textContent = formatCurrency(data.finalAmount);
      }
    });
}
// ====================== SHOP VOUCHER HANDLER =====================
const applyVoucherShops = document.querySelectorAll(".applyVoucherShop");
applyVoucherShops.forEach((btn) => {
  btn.addEventListener("click", () => {
    btn.disabled = true;
    const originalText = btn.innerHTML;
    btn.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Áp dụng
  `;
    const modal = btn.closest(".cart-shop-voucher");
    const shopId = modal.dataset.shopId;
    const code = modal
      .querySelector(".voucherShopInput")
      .value.trim()
      .toUpperCase();
    const msg = modal.querySelector(".voucherShopMessage");

    msg.textContent = "";
    msg.classList.remove("text-danger", "text-success", "text-warning");
    if (!code) {
      msg.classList.add("text-warning");
      msg.textContent = "Vui lòng nhập mã giảm giá.";
      btn.disabled = false;
      btn.innerHTML = originalText;
      return;
    }
    const selectedShopRadio = document.querySelector(
      `input[name="voucherShopDiscount_${shopId}"]:checked`
    );
    if (selectedShopRadio && selectedShopRadio.value === code) {
      msg.classList.add("text-warning");
      msg.textContent = "Mã này đã áp dụng rồi.";
      btn.disabled = false;
      btn.innerHTML = originalText;
      return;
    }
    fetch("/checkout/voucher/shop", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `shopId=${shopId}&code=${code}`,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          localStorage.setItem(`shopVoucher_${shopId}`, code);
          loadSavedVouchers();
          const modalEl = document.getElementById(`shopVoucherModal_${shopId}`);
          if (modalEl) {
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();
          }
          btn.innerHTML = originalText;
          btn.disabled = false;
        } else {
          localStorage.removeItem(`shopVoucher_${shopId}`);
          msg.classList.add("text-danger");
          msg.textContent = data.message || "Mã không hợp lệ.";
          btn.innerHTML = originalText;
          btn.disabled = false;
        }
      })
      .catch((error) => {
        console.error("Fetch voucher error:", error);
        msg.classList.add("text-danger");
        msg.textContent = "Lỗi khi áp dụng mã giảm giá.";
        btn.innerHTML = originalText;
      })
      .finally(() => {
        btn.disabled = false;
      });
  });
});
document.querySelectorAll('[id^="shopVoucherModal_"]').forEach((modalEl) => {
  modalEl.addEventListener("hidden.bs.modal", () => {
    const input = modalEl.querySelector(".voucherShopInput");
    const msg = modalEl.querySelector(".voucherShopMessage");

    if (msg) {
      msg.textContent = "";
      msg.className = "voucherShopMessage";
    }
    if (input) {
      input.value = "";
    }
  });
});
// ====================== SYSTEM VOUCHER HANDLER ======================
const applySystemVoucher = document.getElementById("applySystemVoucher");
applySystemVoucher.addEventListener("click", () => {
  applySystemVoucher.disabled = true;
  const originalText = applySystemVoucher.innerHTML;
  applySystemVoucher.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Áp dụng
  `;
  const code = document
    .getElementById("voucherSystemInput")
    .value.toUpperCase();
  const msg = document.getElementById("voucherSystemMessage");

  msg.textContent = "";
  msg.classList.remove("text-danger", "text-success", "text-warning");
  if (!code) {
    msg.textContent = "Vui lòng nhập mã giảm giá.";
    msg.classList.add("text-warning");
    applySystemVoucher.disabled = false;
    applySystemVoucher.innerHTML = originalText;
    return;
  }

  const selectedDiscount = document.querySelector(
    'input[name="voucherDiscount"]:checked'
  );
  const selectedShip = document.querySelector(
    'input[name="voucherShip"]:checked'
  );

  if (
    (selectedDiscount && selectedDiscount.value === code) ||
    (selectedShip && selectedShip.value === code)
  ) {
    msg.classList.add("text-warning");
    msg.textContent = "Mã này đã áp dụng rồi.";
    applySystemVoucher.disabled = false;
    applySystemVoucher.innerHTML = originalText;
    return;
  }

  fetch("/checkout/voucher/system", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `code=${code}`,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        console.log("Check type ", data.type);

        if (data.type === "SHIPPING") {
          localStorage.setItem("systemVoucherShip", code);
        } else if (data.type === "AMOUNT" || data.type === "PERCENT") {
          localStorage.setItem("systemVoucherDiscount", code);
        }
        loadSystemVouchers();
        const modalEl = document.getElementById("voucherModal");
        if (modalEl) {
          const modal = bootstrap.Modal.getInstance(modalEl);
          modal.hide();
        }

        applySystemVoucher.innerHTML = originalText;
        applySystemVoucher.disabled = false;
      } else {
        localStorage.removeItem("systemVoucherShip");
        localStorage.removeItem("systemVoucherDiscount");
        msg.classList.add("text-danger");
        msg.textContent = data.message || "Mã không hợp lệ.";
        applySystemVoucher.innerHTML = originalText;
        applySystemVoucher.disabled = false;
      }
    })
    .catch(() => {
      msg.classList.add("text-danger");
      msg.textContent = "Lỗi khi áp dụng mã giảm giá.";
      applySystemVoucher.innerHTML = originalText;
      applySystemVoucher.disabled = false;
    })
    .finally(() => {
      applySystemVoucher.disabled = false;
    });
});

const voucherModalEl = document.getElementById("voucherModal");
if (voucherModalEl) {
  voucherModalEl.addEventListener("hidden.bs.modal", () => {
    const msg = document.getElementById("voucherSystemMessage");
    const input = document.getElementById("voucherSystemInput");

    if (msg) {
      msg.textContent = "";
      msg.className = "";
    }
    if (input) {
      input.value = "";
    }
  });
}
// ====================== KHÔI PHỤC VOUCHER SAU KHI RELOAD ======================
function loadSavedVouchers() {
  document.querySelectorAll(".voucher-input-shop").forEach((input) => {
    const shopId = input.dataset.shopId;
    const savedCode = localStorage.getItem(`shopVoucher_${shopId}`);
    if (savedCode) {
      const radio = document.querySelector(
        `input[name="voucherShopDiscount_${shopId}"][value="${savedCode}"]`
      );

      if (radio) {
        radio.checked = true;

        const selectedShopDiscount = document.querySelector(
          `input[name="voucherShopDiscount_${shopId}"]:checked`
        );

        if (selectedShopDiscount) {
          const shopVoucher = document.querySelector(
            `.cart-body__footer[data-shop-id="${shopId}"] .shop-voucher-text`
          );

          if (shopVoucher) {
            const shopTotal = calculateShopTotal(shopId);
            const discount = calculateShopDiscount(shopTotal, shopId);

            shopVoucher.innerText =
              discount > 0
                ? `Đã giảm ${formatCurrency(discount)}`
                : "Chưa áp dụng";
          }
        }
      }
    }
  });

  syncWithServer();
}

function loadSystemVouchers() {
  const discountCode = localStorage.getItem("systemVoucherDiscount");
  const shipCode = localStorage.getItem("systemVoucherShip");
  let textDiscount = "";
  let textShip = "";
  if (discountCode) {
    const discountRadio = document.querySelector(
      `input[name="voucherDiscount"][value="${discountCode}"]`
    );
    if (discountRadio) {
      discountRadio.checked = true;

      textDiscount = discountRadio.dataset.text;
      voucherTextDiscount.innerText = textDiscount;
      appliedVoucherDiscount.classList.remove("d-none");
    }
  }

  if (shipCode) {
    const shipRadio = document.querySelector(
      `input[name="voucherShip"][value="${shipCode}"]`
    );
    if (shipRadio) {
      shipRadio.checked = true;
      textShip = shipRadio.dataset.text;
      voucherTextShip.innerText = textShip;
      appliedVoucherShip.classList.remove("d-none");
    }
    const voucherModal = bootstrap.Modal.getInstance(
      document.getElementById("voucherModal")
    );
    if (voucherModal) {
      voucherModal.hide();
    }
  }
  syncWithServer();
}

window.addEventListener("DOMContentLoaded", () => {
  loadSavedVouchers();
  loadSystemVouchers();
  refreshShopVoucher();
});

const btnPlaceOrder = document.getElementById("btnPlaceOrder");
btnPlaceOrder.addEventListener("click", () => {
  btnPlaceOrder.disabled = true;
  const originalText = btnPlaceOrder.innerHTML;
  btnPlaceOrder.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Đang xử lý...
  `;
  const params = new URLSearchParams();
  const addressId =
    document.querySelector(`[name='addressId']:checked`)?.value || "";
  params.append("addressId", addressId);

  const selectedDiscount = document.querySelector(
    'input[name="voucherDiscount"]:checked'
  );
  if (selectedDiscount) {
    params.append("systemVoucherDiscount", selectedDiscount.value);
  }

  const selectedShip = document.querySelector(
    'input[name="voucherShip"]:checked'
  );
  if (selectedShip) {
    params.append("systemVoucherShip", selectedShip.value);
  }

  document.querySelectorAll(".cart-body[data-shop-id]").forEach((shop) => {
    const shopId = shop.dataset.shopId;
    const selectedShopDiscount = document.querySelector(
      `input[name="voucherShopDiscount_${shopId}"]:checked`
    );
    if (selectedShopDiscount) {
      params.append(`shopVoucher_${shopId}`, selectedShopDiscount.value);
    }
  });
  console.log(params.toString());
  fetch("/order/create", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString(),
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        // toast({
        //   title: data.title,
        //   message: data.message,
        //   type: data.type,
        //   duration: 3000,
        // });
        // setTimeout(() => {
        //   window.location.href = "/home";
        // }, 800);
        window.location.href = data.url;
      } else {
        toast({
          title: data.title,
          message: data.message,
          type: data.type,
          duration: 3000,
        });
      }
    })
    .catch(() => {
      toast({
        title: "Lỗi hệ thống",
        message: "Không thể kết nối đến máy chủ. Vui lòng thử lại.",
        type: "error",
        duration: 3000,
      });
    })
    .finally(() => {
      btnPlaceOrder.disabled = false;
      btnPlaceOrder.innerHTML = originalText;
    });
});
