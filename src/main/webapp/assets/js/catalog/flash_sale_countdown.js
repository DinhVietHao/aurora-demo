(function () {
  "use strict";

  class FlashSaleCountdown {
    constructor(endTime) {
      this.endTime = endTime;
      this.timerElement = document.querySelector(".countdown-timer");
      if (!this.timerElement) {
        console.log("ℹ️ No flash sale timer found");
        return;
      }

      this.daysElement = this.timerElement.querySelector(".days");
      this.hoursElement = this.timerElement.querySelector(".hours");
      this.minutesElement = this.timerElement.querySelector(".minutes");
      this.secondsElement = this.timerElement.querySelector(".seconds");

      if (
        !this.daysElement ||
        !this.hoursElement ||
        !this.minutesElement ||
        !this.secondsElement
      ) {
        console.error("❌ Missing time elements");
        return;
      }

      if (this.endTime <= Date.now()) {
        console.warn("⚠️ Flash Sale already expired");
        this.handleExpired();
        return;
      }

      console.log("🔥 Flash Sale Countdown initialized", {
        endTime: new Date(this.endTime).toLocaleString("vi-VN"),
        remaining: Math.floor((this.endTime - Date.now()) / 1000) + "s",
      });

      this.startCountdown();
    }

    startCountdown() {
      this.updateCountdown();
      this.intervalId = setInterval(() => {
        this.updateCountdown();
      }, 1000);
    }

    updateCountdown() {
      const now = Date.now();
      const distance = this.endTime - now;
      if (distance < 0) {
        this.handleExpired();
        return;
      }

      // Calculate time units
      const days = Math.floor(distance / (1000 * 60 * 60 * 24));
      const hours = Math.floor(
        (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
      );
      const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distance % (1000 * 60)) / 1000);

      // Update display with smooth transition
      this.updateElement(this.daysElement, days);
      this.updateElement(this.hoursElement, hours);
      this.updateElement(this.minutesElement, minutes);
      this.updateElement(this.secondsElement, seconds);

      if (distance < 3600000) {
        this.timerElement.classList.add("urgency");
      } else {
        this.timerElement.classList.remove("urgency");
      }

      if (distance < 600000) {
        this.timerElement.classList.add("critical");
      } else {
        this.timerElement.classList.remove("critical");
      }
    }

    updateElement(element, value) {
      const newValue = this.padZero(value);
      if (element.textContent !== newValue) {
        element.textContent = newValue;
      }
    }

    padZero(num) {
      return num.toString().padStart(2, "0");
    }

    handleExpired() {
      if (this.intervalId) {
        clearInterval(this.intervalId);
        this.intervalId = null;
      }

      console.log("⏰ Flash Sale expired");

      const banner = document.querySelector(".flash-sale-banner");
      if (banner) {
        banner.innerHTML = `
          <div class="text-center py-5" style="color: #fff;">
            <i class="bi bi-clock-history" style="font-size: 64px; opacity: 0.8;"></i>
            <h4 class="mt-3 mb-2">Flash Sale đã kết thúc!</h4>
            <p class="mb-3" style="opacity: 0.9;">Hãy theo dõi để không bỏ lỡ đợt sale tiếp theo</p>
            <button class="btn btn-light btn-lg" onclick="location.reload()">
              <i class="bi bi-arrow-clockwise"></i> Tải lại trang
            </button>
          </div>
        `;
      }
    }

    destroy() {
      if (this.intervalId) {
        clearInterval(this.intervalId);
        this.intervalId = null;
      }
    }
  }

  function init() {
    const timerElement = document.querySelector(".countdown-timer");
    if (!timerElement) {
      console.log("ℹ️ No flash sale on this page");
      return;
    }

    const endTime = parseInt(timerElement.dataset.endTime);

    if (!endTime || isNaN(endTime)) {
      console.error(
        "❌ Invalid flash sale end time:",
        timerElement.dataset.endTime
      );
      return;
    }

    const now = Date.now();
    const maxFuture = now + 365 * 24 * 60 * 60 * 1000;
    if (endTime < now - 7 * 24 * 60 * 60 * 1000) {
      console.warn("⚠️ Flash Sale end time is too far in the past");
      return;
    }

    if (endTime > maxFuture) {
      console.warn("⚠️ Flash Sale end time is too far in the future");
      return;
    }

    const countdown = new FlashSaleCountdown(endTime);

    window.addEventListener("beforeunload", () => {
      if (countdown && countdown.destroy) {
        countdown.destroy();
      }
    });

    document.addEventListener("visibilitychange", () => {
      if (document.hidden) {
        console.log("🔕 Tab hidden, countdown continues in background");
      } else {
        console.log("👁️ Tab visible, syncing countdown");
        if (countdown && countdown.updateCountdown) {
          countdown.updateCountdown();
        }
      }
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
