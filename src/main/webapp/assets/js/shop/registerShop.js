document.addEventListener("DOMContentLoaded", function () {
  const addProvince = document.getElementById("city");
  const addDistrict = document.getElementById("district");
  const addWard = document.getElementById("ward");

  const addProvinceNameInput = document.getElementById("provinceNameInput");
  const addDistrictNameInput = document.getElementById("districtNameInput");
  const addWardNameInput = document.getElementById("wardNameInput");
  const addProvinceIdInput = document.getElementById("provinceIdInput");
  const addDistrictIdInput = document.getElementById("districtIdInput");
  const addWardCodeInput = document.getElementById("wardCodeInput");

  initAddressSelects(
    addProvince,
    addDistrict,
    addWard,
    addProvinceNameInput,
    addDistrictNameInput,
    addWardNameInput,
    addProvinceIdInput,
    addDistrictIdInput,
    addWardCodeInput
  );

  // Validation cho form đăng ký shop
  const registerShopForm = document.querySelector("#register-shop-form");
  if (registerShopForm) {
    console.log("Form found, initializing Validator");
    Validator({
      form: "#register-shop-form",
      formGroupSelector: ".form-group",
      errorSelector: ".form-message",
      rules: [
        Validator.isRequired("#fullname", "Vui lòng nhập họ và tên"),
        Validator.minLength("#fullname", 2, "Họ và tên phải ít nhất 2 ký tự"),
        Validator.maxLength("#fullname", 150, "Họ và tên không quá 150 ký tự"),
        Validator.isName("#fullname", "Họ và tên chỉ chứa chữ cái và dấu cách"),

        Validator.isRequired("#email", "Vui lòng nhập email"),
        Validator.isEmail("#email", "Email không hợp lệ"),
        Validator.maxLength("#email", 255, "Email không quá 255 ký tự"),

        Validator.isRequired("#phone", "Vui lòng nhập số điện thoại"),
        Validator.isPhone(
          "#phone",
          "Số điện thoại không hợp lệ (10-11 chữ số, bắt đầu bằng 0)"
        ),

        Validator.isRequired("#shopName", "Vui lòng nhập tên shop"),
        Validator.minLength("#shopName", 3, "Tên shop phải ít nhất 3 ký tự"),
        Validator.maxLength("#shopName", 150, "Tên shop không quá 150 ký tự"),

        Validator.maxLength("#shopDesc", 255, "Mô tả không quá 255 ký tự"),

        Validator.isRequired("#city", "Vui lòng chọn tỉnh/thành phố"),
        Validator.isRequired("#ward", "Vui lòng chọn phường/xã"),
        Validator.isRequired("#addressLine", "Vui lòng nhập địa chỉ cụ thể"),
        Validator.minLength(
          "#addressLine",
          5,
          "Địa chỉ cụ thể phải ít nhất 5 ký tự"
        ),
        Validator.maxLength(
          "#addressLine",
          255,
          "Địa chỉ cụ thể không quá 255 ký tự"
        ),
      ],
      onSubmit: function (data) {
        console.log("Form data:", data);
      },
    });
  } else {
    console.log("Form #register-shop-form not found");
  }

  registerShopForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const formData = new FormData(registerShopForm);
    const submitBtn = registerShopForm.querySelector('button[type="submit"]');
    submitBtn.disabled = true;

    fetch(registerShopForm.action, {
      method: "POST",
      body: formData,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          toast({
            title: "Thành công!",
            message: data.message,
            type: "success",
            duration: 5000,
          });

          setTimeout(() => {
            window.location.href = "/home";
          }, 5000);
        } else {
          toast({
            title: "Có lỗi xảy ra",
            message: data.message,
            type: "error",
            duration: 5000,
          });

          if (data.message == "Phiên đăng nhập đã hết hạn.") {
            setTimeout(() => {
              window.location.href = "/home";
            }, 5000);
          }
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        toast({
          title: "Lỗi hệ thống",
          message: "Không thể kết nối. Vui lòng thử lại.",
          type: "error",
          duration: 5000,
        });
      });
  });
});

// ========= Preview shop logo (không upload ngay) ========
document.addEventListener("DOMContentLoaded", function () {
  const shopLogoInput = document.getElementById("shopLogo");
  const shopLogoPreview = document.getElementById("shopLogoPreview");
  const shopLogoPlaceholder = document.getElementById("shopLogoPlaceholder");

  if (shopLogoInput && shopLogoPreview && shopLogoPlaceholder) {
    shopLogoInput.addEventListener("change", function () {
      const file = shopLogoInput.files[0];

      if (!file) {
        // Reset về placeholder
        shopLogoPlaceholder.classList.remove("d-none");
        shopLogoPreview.classList.add("d-none");
        shopLogoPreview.classList.remove("show");
        return;
      }

      // Validate file type
      if (!file.type.startsWith("image/")) {
        if (typeof toast === "function") {
          toast({
            title: "Lỗi!",
            message: "Vui lòng chọn file ảnh hợp lệ.",
            type: "error",
            duration: 3000,
          });
        } else {
          alert("Vui lòng chọn file ảnh hợp lệ.");
        }
        shopLogoInput.value = "";
        shopLogoPlaceholder.classList.remove("d-none");
        shopLogoPreview.classList.add("d-none");
        return;
      }

      // Validate file size (5MB)
      if (file.size > 5 * 1024 * 1024) {
        if (typeof toast === "function") {
          toast({
            title: "Lỗi!",
            message: "Ảnh vượt quá 5MB.",
            type: "error",
            duration: 3000,
          });
        } else {
          alert("Ảnh vượt quá 5MB.");
        }
        shopLogoInput.value = "";
        shopLogoPlaceholder.classList.remove("d-none");
        shopLogoPreview.classList.add("d-none");
        return;
      }

      // Preview image
      const reader = new FileReader();
      reader.onload = function (e) {
        shopLogoPlaceholder.classList.add("d-none");
        shopLogoPreview.classList.remove("d-none");
        shopLogoPreview.classList.add("show");
        shopLogoPreview.src = e.target.result;
      };
      reader.readAsDataURL(file);
    });
  }
});
