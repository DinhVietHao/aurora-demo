const PayBtn = document.getElementById("btnPay");

PayBtn.addEventListener("click", () => {
    const selected = document.querySelector('input[name="payment"]:checked');
    const errorPayment = document.getElementById('paymentErrorModal');
    const vnPayModalElement = document.getElementById('vnpayModal');
    if (!selected) {
        const errorPaymentModal = new bootstrap.Modal(errorPayment);
        errorPaymentModal.show();
    } else {
        if (selected.id === "vnpay") {
            const vnPayModalModal = new bootstrap.Modal(vnPayModalElement);
            vnPayModalModal.show();
        }
    }
})