(function () {
  "use strict";

  const CONFIG = {
    showAfter: window.innerHeight,
    scrollDuration: 800,
    easing: "easeInOutCubic",
  };

  // Tạo nút "Về đầu trang"
  function createButton() {
    const button = document.createElement("button");
    button.className = "scroll-to-top";
    button.setAttribute("aria-label", "Scroll to top");
    button.setAttribute("title", "Về đầu trang");
    button.innerHTML = '<i class="bi bi-rocket-takeoff-fill"></i>';
    document.body.appendChild(button);
    return button;
  }

  // Các hàm easing
  const easingFunctions = {
    linear: (t) => t,
    easeInOutCubic: (t) =>
      t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1,
    easeOutQuart: (t) => 1 - --t * t * t * t,
  };

  // Cuộn mượt lên đầu trang
  function scrollToTop(duration = 800, easingName = "easeInOutCubic") {
    const startPosition = window.pageYOffset;
    const startTime = performance.now();
    const easing = easingFunctions[easingName] || easingFunctions.linear;

    function animation(currentTime) {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const ease = easing(progress);
      window.scrollTo(0, startPosition * (1 - ease));
      if (progress < 1) requestAnimationFrame(animation);
    }

    requestAnimationFrame(animation);
  }

  // Hiện/ẩn nút dựa trên vị trí cuộn
  function toggleButton(button) {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    if (scrollTop > CONFIG.showAfter) button.classList.add("show");
    else button.classList.remove("show");
  }

  // Khởi tạo chức năng
  function init() {
    const button = createButton();
    let scrollTimeout;

    // Theo dõi cuộn trang
    window.addEventListener(
      "scroll",
      function () {
        if (scrollTimeout) window.cancelAnimationFrame(scrollTimeout);
        scrollTimeout = window.requestAnimationFrame(function () {
          toggleButton(button);
        });
      },
      { passive: true }
    );

    // Click cuộn lên đầu
    button.addEventListener("click", function (e) {
      e.preventDefault();
      scrollToTop(CONFIG.scrollDuration, CONFIG.easing);
    });

    // Hỗ trợ bàn phím
    button.addEventListener("keydown", function (e) {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        scrollToTop(CONFIG.scrollDuration, CONFIG.easing);
      }
    });
  }

  // Chạy sau khi DOM sẵn sàng
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
