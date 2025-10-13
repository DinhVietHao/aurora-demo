function initProvinceWard(
  provinceSelect,
  wardSelect,
  defaultProvince = "",
  defaultWard = ""
) {
  fetch("https://provinces.open-api.vn/api/v2/p/")
    .then((res) => res.json())
    .then((provinces) => {
      provinces.forEach((p) => {
        const opt = document.createElement("option");
        opt.value = p.name;
        opt.textContent = p.name;
        opt.dataset.code = p.code;
        provinceSelect.appendChild(opt);
      });

      if (defaultProvince) {
        provinceSelect.value = defaultProvince;
        loadWards(provinceSelect, wardSelect, defaultWard);
      }
      provinceSelect.addEventListener("change", () =>
        loadWards(provinceSelect, wardSelect)
      );
    });
}

function loadWards(provinceSelect, wardSelect, defaultWard = "") {
  if (defaultWard) {
    wardSelect.value = defaultWard;
    return;
  }
  wardSelect.innerHTML = "<option value=''>Chọn Phường/Xã</option>";
  const selectedOption = provinceSelect.selectedOptions[0];
  if (!selectedOption) return;
  fetch(
    `https://provinces.open-api.vn/api/v2/p/${selectedOption.dataset.code}?depth=2`
  )
    .then((res) => res.json())
    .then((data) => {
      data.wards.forEach((w) => {
        const opt = document.createElement("option");
        opt.value = w.name;
        opt.textContent = w.name;
        wardSelect.appendChild(opt);
      });
    });
}

// AJAX Delete Address
const confirmDeleteAddress = document.getElementById("confirmDeleteAddress");
const deleteAddress = document.querySelectorAll(".delete-address");
deleteAddress.forEach((btn) => {
  btn.addEventListener("click", () => {
    const deleteAddressModal = document.getElementById("deleteAddressModal");
    deleteAddressModal.dataset.addressid = btn.dataset.addressid;
  });
});

// AJAX Update Address
const btnUpdateAddress = document.querySelectorAll(".update-address");
btnUpdateAddress.forEach((btn) => {
  btn.addEventListener("click", () => {
    const addressId = btn.dataset.addressid;
    console.log("Check addressId=", addressId);

    fetch(`/address/update?addressId=${addressId}`)
      .then((res) => res.json())
      .then((data) => {
        document.querySelector(
          "#form-update-address input[name='addressId']"
        ).value = data.addressId;
        document.querySelector(".update-fullname").value = data.recipientName;
        document.querySelector(".update-phone").value = data.phone;
        const cityOption = document.querySelector(".update-city");
        cityOption.value = data.city;
        cityOption.innerText = data.city;

        const wardOption = document.querySelector(".update-ward");
        wardOption.value = data.ward;
        wardOption.innerText = data.ward;
        document.querySelector(".update-description").value = data.description;
        document.querySelector(".update-default").checked = data.defaultAddress;

        const updateProvinceSelect = document.getElementById("updateProvince");
        const updateWardSelect = document.getElementById("updateWard");

        initProvinceWard(
          updateProvinceSelect,
          updateWardSelect,
          data.city,
          data.ward
        );
      });
  });
});
if (confirmDeleteAddress) {
  confirmDeleteAddress.addEventListener("click", () => {
    const deleteAddressModalEl = document.getElementById("deleteAddressModal");
    const addressId = deleteAddressModalEl.dataset.addressid;
    fetch("/address/delete", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `addressId=${addressId}`,
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          const deleteAddressModal =
            bootstrap.Modal.getInstance(deleteAddressModalEl);
          deleteAddressModal.hide();

          const addressEl = document.getElementById(`addressId${addressId}`);
          if (addressEl) {
            addressEl.remove();
          }

          if (document.querySelectorAll(".address-card").length === 0) {
            document.querySelector(".address-empty").innerHTML = `
          <div class="text-center mt-5">
            <img src="./assets/images/common/addressEmpty.png" alt="">
            <p class="text-muted mt-3">Bạn chưa có địa chỉ nào.</p>
          </div>
        `;
          }
        } else {
          alert("Xóa thất bại: " + data.message);
        }
      })
      .catch((err) => {
        console.error(err);
        alert("Có lỗi xảy ra, vui lòng thử lại!");
      });
  });
}
