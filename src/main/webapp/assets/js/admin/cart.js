// ====================== CART CALCULATION ======================
const minusBtn = document.querySelectorAll('.minus');
const plusBtn = document.querySelectorAll('.plus');
const cartBodies = document.querySelectorAll('.cart-body');

const totalProductPrice = document.querySelector('.cart-pay .total-product-price');
const discountElement = document.querySelector('.cart-pay .discount');
const shipElement = document.querySelector('.cart-pay .ship-discount');
const totalPayment = document.querySelector('.cart-pay .total-payment');
const shippingFeePayment = document.querySelector('.cart-pay .shipping-fee');
const buyButton = document.querySelector('.cart-pay #cart-pay-button');


const selectAllCheckbox = document.querySelector('.cart-header .cart-checkboxAll');
const productCheckboxes = document.querySelectorAll('.cart-body .cart-checkbox');



// ====================== FORMAT PRICE ======================
function formatCurrency(num) {
    return num.toLocaleString('vi-VN') + 'đ';
}

// ====================== CALCULATE TOTAL ======================
function calculateTotalProduct() {
    let total = 0;
    cartBodies.forEach(row => {
        const priceElement = row.querySelector('.unit-price');
        const quantityElement = row.querySelector('.number');
        const subtotalElement = row.querySelector('.subtotal');
        const checkbox = row.querySelector('.cart-checkbox');


        if (priceElement && quantityElement && subtotalElement && checkbox && checkbox.checked) {
            let price = parseInt(priceElement.textContent.replace(/\D/g, '') || 0);
            let quantity = parseInt(quantityElement.textContent) || 1;
            let subtotal = price * quantity;
            subtotalElement.textContent = formatCurrency(subtotal);
            total += subtotal
        }
    });
    return total;
}

// ====================== VOUCHER CALCULATION ======================
function calculateDiscount(total) {
    const selectedDiscount = document.querySelector('input[name="voucherDiscount"]:checked');
    if (selectedDiscount) {
        let type = selectedDiscount.dataset.type;
        if (type === 'percent') {
            let percent = parseInt(selectedDiscount.dataset.discount) || 0;
            let max = parseInt(selectedDiscount.dataset.max) || 0;
            return Math.min(total * percent / 100, max);
        } else {
            return parseInt(selectedDiscount.dataset.discount) || 0;
        }
    }
    return 0;
}

function calculateShipDiscount() {
    const selectedShip = document.querySelector('input[name="voucherShip"]:checked');
    if (selectedShip) {
        return parseInt(selectedShip.dataset.ship) || 0;
    }
    return 0;
}


// ====================== PRODUCT QUANTITY CALCULATION ======================

function calculateTotalProducts() {
    let totalProducts = 0;
    cartBodies.forEach(row => {
        const checkbox = row.querySelector('.cart-checkbox');
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

    discountElement.innerText = '-' + formatCurrency(discount);
    shipElement.innerText = '-' + formatCurrency(ship);

    // Shipping Fee
    let shippingFee = parseInt(shippingFeePayment.textContent.replace(/\D/g, '')) || 30000;

    totalProductPrice.textContent = formatCurrency(total);
    let sum = Math.max(total + shippingFee - discount - ship, 0);
    totalPayment.textContent = formatCurrency(sum);

    updateBuyButton();

}

// ====================== CHECKBOX HANDLER ======================
selectAllCheckbox.addEventListener('change', () => {
    productCheckboxes.forEach(cb => cb.checked = selectAllCheckbox.checked);
    updateCartSummary();
});

productCheckboxes.forEach(cb => {
    cb.addEventListener('change', () => {
        const allChecked = [...productCheckboxes].every(cb => cb.checked);
        selectAllCheckbox.checked = allChecked;
        updateCartSummary();
    });
});


// ====================== QUANTITY HANDLER ======================
minusBtn.forEach(btn => {
    btn.addEventListener('click', () => {
        const number = btn.nextElementSibling;
        let quantity = parseInt(number.textContent);
        if (quantity > 1) {
            quantity--;
            number.textContent = quantity;
            updateCartSummary();
        }
    });

});

plusBtn.forEach(btn => {
    btn.addEventListener('click', () => {
        const number = btn.previousElementSibling;
        let quantity = parseInt(number.textContent);
        quantity++;
        number.textContent = quantity;
        updateCartSummary();
    });

});

updateCartSummary();




// ====================== VOUCHER HANDLER ======================
const confirmVoucher = document.getElementById('confirmVoucher');

const appliedVoucherDiscount = document.getElementById('appliedVoucherDiscount');
const voucherTextDiscount = document.getElementById('voucherTextDiscount');

const appliedVoucherShip = document.getElementById('appliedVoucherShip');
const voucherTextShip = document.getElementById('voucherTextShip');

const removeVoucherDiscount = document.getElementById('removeVoucherDiscount')
const removeVoucherShip = document.getElementById('removeVoucherShip');


confirmVoucher.addEventListener('click', () => {

    const selectedDiscount = document.querySelector('input[name="voucherDiscount"]:checked');
    const selectedShip = document.querySelector('input[name="voucherShip"]:checked');

    let textDiscount = '';
    let textShip = '';

    if (selectedDiscount) {
        textDiscount = selectedDiscount.dataset.text;
        voucherTextDiscount.innerText = textDiscount;
        appliedVoucherDiscount.classList.remove('d-none');

    }

    if (selectedShip) {
        textShip = selectedShip.dataset.text;
        voucherTextShip.innerText = textShip;
        appliedVoucherShip.classList.remove('d-none');

    }
    const VoucherModal = bootstrap.Modal.getInstance(document.getElementById('voucherModal'));
    VoucherModal.hide();
    updateCartSummary();

})

removeVoucherDiscount.addEventListener('click', () => {
    const selectedDiscount = document.querySelector('input[name="voucherDiscount"]:checked');
    if (selectedDiscount) selectedDiscount.checked = false;

    voucherTextDiscount.innerText = '';
    appliedVoucherDiscount.classList.add('d-none');
    discountElement.innerText = '-0đ';
    updateCartSummary();
})

removeVoucherShip.addEventListener('click', () => {
    const selectedShip = document.querySelector('input[name="voucherShip"]:checked');
    if (selectedShip) selectedShip.checked = false;

    voucherTextShip.innerText = '';
    appliedVoucherShip.classList.add('d-none');
    shipElement.innerText = '-0đ';
    updateCartSummary();

});

