const btnCancelOrder = document.querySelectorAll(".btn-cancel-order");
btnCancelOrder.forEach((btn) => {
  btn.addEventListener("click", function () {
    const orderShopId = btn.dataset.orderShopId;
    document.getElementById("cancelOrderShopId").value = orderShopId;
  });
});



const btnConfirmOrder = document.querySelectorAll(".btn-confirm-order");
btnConfirmOrder.forEach((btn) => {
  btn.addEventListener("click", function () {
    const orderShopId = btn.dataset.orderShopId;
    document.getElementById("confirmOrderShopId").value = orderShopId;
  });
});

const btnReturnOrder = document.querySelectorAll(".btn-return-order");
btnReturnOrder.forEach((btn) => {
  btn.addEventListener("click", function () {
    const orderShopId = btn.dataset.orderShopId;
    document.getElementById("returnOrderShopId").value = orderShopId;
  });
});

document.querySelectorAll(".btnRepurchase").forEach((btn) => {
  btn.addEventListener("click", () => {
    btn.disabled = true;
    const originalText = btn.innerHTML;
    btn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Đang xử lý...`;
    const orderShopId = btn.dataset.orderShopId;
    fetch("/order/repurchase", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: "orderShopId=" + orderShopId,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          const cartCountBadge = document.getElementById("cartCountBadge");
          if (cartCountBadge) {
            cartCountBadge.innerText = data.cartCount;
          }
          window.location.href = "/checkout";
        } else {
          if (
            data.messages &&
            Array.isArray(data.messages) &&
            data.messages.length
          ) {
            data.messages.forEach((msg) => {
              toast({
                title: data.title,
                message: msg,
                type: data.type,
                duration: 4000,
              });
            });
          } else {
            toast({
              title: data.title,
              message: data.message,
              type: data.type,
              duration: 4000,
            });
          }
        }
      })
      .catch((err) => {
        toast({
          title: "Lỗi mạng",
          message: "Không thể kết nối tới server. Vui lòng thử lại.",
          type: "error",
          duration: 4000,
        });
        console.error(err);
      })
      .finally(() => {
        btn.disabled = false;
        btn.innerHTML = originalText;
      });
  });
});
