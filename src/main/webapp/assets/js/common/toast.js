function toast({ title = "", message = "", type = "", duration = 2000 }) {
    const main = document.getElementById("notify-toast");

    if (main) {
        const toastElement = document.createElement("div");

        //auto remove toast
        const autoRemoveId = setTimeout(() => {
            main.removeChild(toastElement);
        }, duration);

        //Remove toast when click
        toastElement.addEventListener("click", (e) => {
            if (e.target.closest(".toast__close")) {
                main.removeChild(toastElement);
                clearTimeout(autoRemoveId);
            }
        });

        const icons = {
            success: "fa-solid fa-circle-check",
            info: "fa-solid fa-circle-info",
            warning: "fa-solid fa-triangle-exclamation",
            error: "fa-solid fa-triangle-exclamation",
        };

        const icon = icons[type];
        toastElement.classList.add("notify-toast", `toast--${type}`);
        const delay = (duration / 1000).toFixed(2);
        toastElement.style.animation = `slideInLeft ease .3s, fadeOut linear 1s ${delay}s forwards`;
        toastElement.innerHTML = `
            <div class="toast__icon">
                <i class="${icon}"></i>
            </div>
            <div class="toast__body">
                <h3 class="toast__title">${title}</h3>
                <p class="toast__msg">${message}</p>
            </div>
            <div class="toast__close">
                <i class="fa-solid fa-circle-xmark"></i>
            </div>`;
        main.appendChild(toastElement);
    }
}
