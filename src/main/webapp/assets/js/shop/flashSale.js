document.addEventListener("DOMContentLoaded", function () {
  const joinButtons = document.querySelectorAll(".btn-join-flashsale");
  const registerModal = new bootstrap.Modal(
    document.getElementById("flashsaleRegisterModal")
  );
  let selectedEventId = null;

  // Khi bấm nút "Tham gia" → mở modal
  joinButtons.forEach((btn) => {
    btn.addEventListener("click", function () {
      selectedEventId = this.dataset.id;
      registerModal.show();
    });
  });

  // Khi bấm "Đăng ký" trong modal
  document
    .getElementById("flashsaleBtnSubmitRegister")
    .addEventListener("click", function () {
      const productId = document.getElementById("flashsaleProductSelect").value;
      const quantity = document.getElementById("flashsaleQuantityInput").value;
      const price = document.getElementById("flashsalePriceInput").value;

      if (!productId || !quantity || !price) {
        alert("⚠️ Vui lòng nhập đầy đủ thông tin trước khi đăng ký!");
        return;
      }

      const payload = {
        eventId: selectedEventId,
        productId,
        quantity,
        price,
      };

      console.log("📤 Dữ liệu đăng ký:", payload);

      alert(
        "✅ Đăng ký Flash Sale thành công!\n" +
          `Sự kiện: ${selectedEventId}\n` +
          `Sản phẩm: ${productId}\n` +
          `Số lượng: ${quantity}\n` +
          `Giá: ${price} VNĐ`
      );

      registerModal.hide();
    });
});
