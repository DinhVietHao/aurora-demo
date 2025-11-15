const commentImageReals = document.querySelectorAll(".comment-image-real img");

commentImageReals.forEach((imgActive) => {
  imgActive.addEventListener("click", () => {
    commentImageReals.forEach((removeActive) =>
      removeActive.classList.remove("active")
    );
    imgActive.classList.add("active");
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const carousels = document.querySelectorAll('[id^="reviewCarousel"]');

  carousels.forEach((carousel) => {
    const modalId = carousel.id;
    const reviewId = modalId.replace("reviewCarousel", "");
    const thumbnails = document.querySelectorAll(
      `#reviewModal${reviewId} .comment-image-real img`
    );

    if (thumbnails.length === 0) return;

    thumbnails.forEach((imgActive) => {
      imgActive.addEventListener("click", () => {
        thumbnails.forEach((removeActive) =>
          removeActive.classList.remove("active")
        );
        imgActive.classList.add("active");
      });
    });

    carousel.addEventListener("slid.bs.carousel", function (event) {
      thumbnails.forEach((removeActive) =>
        removeActive.classList.remove("active")
      );
      if (thumbnails[event.to]) {
        thumbnails[event.to].classList.add("active");
      }
    });
  });
});
