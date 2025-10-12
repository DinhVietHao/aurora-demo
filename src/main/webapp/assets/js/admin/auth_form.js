// Show and Hide Login - Register
const registerModalEl = document.querySelector('#registerModal');
const loginModalEl = document.querySelector('#loginModal');
const forgetPasswordModalEl = document.querySelector('#forgetPasswordModal');

const registerModal = new bootstrap.Modal(registerModalEl);
const loginModal = new bootstrap.Modal(loginModalEl);
const forgetPasswordModal = new bootstrap.Modal(forgetPasswordModalEl);



const openRegister = document.querySelectorAll("[data-open='register']");
const openLogin = document.querySelectorAll("[data-open='login']");
const openForgetPassword = document.querySelectorAll("[data-open='forget-password']");


openRegister.forEach(btn => {
    btn.addEventListener('click', () => {
        loginModal.hide();
        registerModal.show();
    })
})

openLogin.forEach(btn => {
    btn.addEventListener('click', () => {
        registerModal.hide();
        loginModal.show();
    })
})

openForgetPassword.forEach(btn => {
    btn.addEventListener('click', () => {
        loginModal.hide();
        forgetPasswordModal.show();
    })
})



//End  Show and Hide Login - Register


// Show and hide Password
const togglePassword = document.querySelectorAll('.toggle-password');

togglePassword.forEach(icon => {
    icon.addEventListener('click', () => {
        const inputPassword = icon.previousElementSibling;
        const type = inputPassword.getAttribute('type') === 'password' ? 'text' : 'password';
        inputPassword.setAttribute("type", type);

        icon.classList.toggle('bi-eye');
        icon.classList.toggle('bi-eye-slash');
    })
})
//End Show and hide Password
