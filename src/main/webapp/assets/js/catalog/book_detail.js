// View by and Collapse
const moreBtn = document.querySelector("#more");
const gradient = document.querySelector(".gradient");
const bookBody = document.querySelector(".book-description-body");
moreBtn.addEventListener("click", () => {
  if (moreBtn.innerText === "Xem thêm") {
    moreBtn.innerText = "Thu gọn";
    gradient.style.display = "none";
    bookBody.style.maxHeight = bookBody.scrollHeight + "px";
  } else {
    moreBtn.innerText = "Xem thêm";
    gradient.style.display = "block";
    bookBody.style.maxHeight = "250px";
  }
});
// End View by and Collapse

// handle click thumbnail image → change main image
const thumbnails = document.querySelectorAll(".thumbnail");
const mainImage = document.querySelector("#mainImage");

thumbnails.forEach((img) => {
  img.addEventListener("click", () => {
    thumbnails.forEach((active) => active.classList.remove("active"));
    mainImage.src = img.src;
    img.classList.add("active");
  });
});
//END handle click thumbnail image → change main image
// ======================== ADD CART ==========================
const addToCartBtn = document.getElementById("add-to-cart");
addToCartBtn.addEventListener("click", () => {
  addToCartBtn.disabled = true;
  const originalText = addToCartBtn.innerHTML;
  addToCartBtn.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Đang xử lý...
  `;
  const productId = addToCartBtn.dataset.productId;
  fetch("/cart/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: "productId=" + productId,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        toast({
          title: "Thành công!",
          message: data.message,
          type: "success",
          duration: 3000,
        });
        const cartCountBadge = document.getElementById("cartCountBadge");
        if (cartCountBadge) {
          cartCountBadge.innerText = data.cartCount;
        }

        if (data.check === "flashsale_exceeded") {
          const modalEl = document.getElementById("flashSaleModal");
          const flashSaleModal = new bootstrap.Modal(modalEl);
          document.getElementById("flashSaleMessage").textContent =
            data.messageModal;
          flashSaleModal.show();
        }
      } else {
        toast({
          title: data.title,
          message: data.message,
          type: data.type,
          duration: 3000,
        });
        const loginModalEl = document.getElementById("loginModal");
        if (data.user && loginModalEl) {
          setTimeout(() => {
            new bootstrap.Modal(loginModalEl).show();
          }, 3000);
        }
      }
    })
    .catch((error) => {
      console.error(error);
    })
    .finally(() => {
      addToCartBtn.disabled = false;
      addToCartBtn.innerHTML = originalText;
    });
});
// ======================== BUY NOW ==========================
const buyNow = document.getElementById("buyNow");
buyNow.addEventListener("click", () => {
  buyNow.disabled = true;
  const originalText = buyNow.innerHTML;
  buyNow.innerHTML = `
    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
    Đang xử lý...
  `;
  const productId = buyNow.dataset.productId;
  fetch("/cart/buyNow", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: "productId=" + productId,
  })
    .then((res) => res.json())
    .then((data) => {
      if (data.success) {
        window.location.href = "/checkout";
        const cartCountBadge = document.getElementById("cartCountBadge");
        if (cartCountBadge) {
          cartCountBadge.innerText = data.cartCount;
        }
      } else {
        toast({
          title: data.title,
          message: data.message,
          type: data.type,
          duration: 3000,
        });
        const loginModalEl = document.getElementById("loginModal");
        if (data.user == null && loginModalEl) {
          setTimeout(() => {
            new bootstrap.Modal(loginModalEl).show();
          }, 3000);
        }
      }
    })
    .catch((error) => {
      console.error(error);
    })
    .finally(() => {
      buyNow.disabled = false;
      buyNow.innerHTML = originalText;
    });
});
