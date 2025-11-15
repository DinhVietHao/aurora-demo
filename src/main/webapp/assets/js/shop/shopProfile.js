Validator({
  form: "#shopInfoForm",
  formGroupSelector: ".form-group",
  errorSelector: ".form-message",
  rules: [
    Validator.isRequired("#shopName", "Vui lòng nhập tên shop"),
    Validator.isRequired("#shopPhone", "Vui lòng nhập số điện thoại"),
    Validator.isRequired("#shopEmail", "Vui lòng nhập email"),
    Validator.isEmail("#shopEmail", "Email không hợp lệ"),
    Validator.isRequired("#updateProvince", "Vui lòng chọn Tỉnh/Thành phố"),
    Validator.isRequired("#updateDistrict", "Vui lòng chọn Quận/Huyện"),
    Validator.isRequired("#updateWard", "Vui lòng chọn Phường/Xã"),
    Validator.isRequired("#shopAddress", "Vui lòng nhập địa chỉ chi tiết"),
  ],
  onSubmit: function (data) {
    handleUpdateShopProfile();
  },
});

function handleUpdateShopProfile() {
  const submitBtn = document.querySelector(
    '#shopInfoForm button[type="submit"]'
  );
  const originalBtnText = submitBtn.innerHTML;

  submitBtn.disabled = true;
  submitBtn.innerHTML =
    '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';

  const formData = new FormData();
  // Shop profile data
  formData.append("action", "updateProfile");
  formData.append("shopName", document.getElementById("shopName").value);
  formData.append("shopPhone", document.getElementById("shopPhone").value);
  formData.append("shopEmail", document.getElementById("shopEmail").value);
  formData.append(
    "shopDescription",
    document.getElementById("shopDescription").value
  );

  // Address data
  formData.append(
    "cityName",
    document.getElementById("updateProvinceNameInput").value
  );
  formData.append(
    "districtName",
    document.getElementById("updateDistrictNameInput").value
  );
  formData.append(
    "wardName",
    document.getElementById("updateWardNameInput").value
  );
  formData.append(
    "provinceId",
    document.getElementById("updateProvinceIdInput").value
  );
  formData.append(
    "districtId",
    document.getElementById("updateDistrictIdInput").value
  );
  formData.append(
    "wardCode",
    document.getElementById("updateWardCodeInput").value
  );
  formData.append("addressLine", document.getElementById("shopAddress").value);

  fetch("/shop", {
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
          duration: 3000,
        });
      } else {
        toast({
          title: "Lỗi!",
          message: data.message,
          type: "error",
          duration: 3000,
        });
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      toast({
        title: "Lỗi hệ thống!",
        message: "Không thể kết nối tới server. Vui lòng thử lại.",
        type: "error",
        duration: 3000,
      });
    })
    .finally(() => {
      submitBtn.disabled = false;
      submitBtn.innerHTML = originalBtnText;
    });
}

const provinceSelect = document.getElementById("updateProvince");
const districtSelect = document.getElementById("updateDistrict");
const wardSelect = document.getElementById("updateWard");

const provinceNameInput = document.getElementById("updateProvinceNameInput");
const districtNameInput = document.getElementById("updateDistrictNameInput");
const wardNameInput = document.getElementById("updateWardNameInput");
const provinceIdInput = document.getElementById("updateProvinceIdInput");
const districtIdInput = document.getElementById("updateDistrictIdInput");
const wardCodeInput = document.getElementById("updateWardCodeInput");

initAddressSelects(
  provinceSelect,
  districtSelect,
  wardSelect,
  provinceNameInput,
  districtNameInput,
  wardNameInput,
  provinceIdInput,
  districtIdInput,
  wardCodeInput
);

document.addEventListener("DOMContentLoaded", function () {
  const uploadBtn = document.getElementById("uploadLogoBtn");
  const fileInput = document.getElementById("shopLogoInput");

  if (uploadBtn && fileInput) {
    uploadBtn.addEventListener("click", function () {
      fileInput.click();
    });
  }

  const shopAvatarUploader = new AvatarUploader({
    inputId: "shopLogoInput",
    previewId: "shopLogoPreview",
    uploadUrl: "/shop",
    fileParamName: "shopLogo",
    action: "uploadAvatar",
    onSuccess: function (data) {
      toast({
        title: "Thành công!",
        message: data.message,
        type: "success",
        duration: 3000,
      });

      if (data.avatarUrl) {
        document.getElementById("shopLogoPreview").src = data.avatarUrl;
      }
    },
    onError: function (message) {
      toast({
        title: "Lỗi!",
        message: message,
        type: "error",
        duration: 3000,
      });
    },
  });
});
