const btnCancelOrder = document.querySelectorAll(".btn-cancel-order");
btnCancelOrder.forEach((btn) => {
  btn.addEventListener("click", function () {
    const orderShopId = btn.dataset.orderShopId;
    document.getElementById("cancelOrderShopId").value = orderShopId;
  });
});
