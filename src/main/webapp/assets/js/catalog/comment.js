const commentImageReals = document.querySelectorAll(".comment-image-real img");
const carousel = document.querySelector("#reviewCarousel");

commentImageReals.forEach((imgActive) => {
  imgActive.addEventListener("click", () => {
    commentImageReals.forEach((removeActive) =>
      removeActive.classList.remove("active")
    );
    imgActive.classList.add("active");
  });
});

// Khi carousel đổi slide → cập nhật thumbnail active
carousel.addEventListener("slid.bs.carousel", function (event) {
  commentImageReals.forEach((removeActive) =>
    removeActive.classList.remove("active")
  );
  commentImageReals[event.to].classList.add("active");
  console.log("check event.to", event);
});
