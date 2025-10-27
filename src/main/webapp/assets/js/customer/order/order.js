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
          window.location.href = "/cart";
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
// Product Reviews
const fileInput = document.getElementById("fileInput");
const previewImages = document.getElementById("previewImages");

fileInput.addEventListener("change", () => {
  previewImages.innerHTML = "";
  console.dir(fileInput);
  const files = Array.from(fileInput.files);

  files.forEach((file, index) => {
    const imgDiv = document.createElement("div");
    imgDiv.className = "position-relative";
    imgDiv.style.width = "80px";
    imgDiv.style.height = "80px";

    const img = document.createElement("img");
    img.src = `./assets/images/${file.name}`;
    img.style.width = "100%";
    img.style.height = "100%";
    img.style.objectFit = "cover";
    img.className = "rounded";

    const removeBtn = document.createElement("button");
    removeBtn.innerHTML = "&times;";
    removeBtn.className = "btn btn-sm btn-danger position-absolute top-0 end-0";
    removeBtn.style.padding = "0 5px";
    removeBtn.addEventListener("click", () => {
      files.splice(index, 1);
      updateFileList(files);
      imgDiv.remove();
    });

    imgDiv.appendChild(removeBtn);
    imgDiv.appendChild(img);
    previewImages.appendChild(imgDiv);
  });
});

function updateFileList(files) {
  const dataTransfer = new DataTransfer();
  files.forEach((file) => dataTransfer.items.add(file));
  fileInput.files = dataTransfer.files;
}
//End Product Reviews
