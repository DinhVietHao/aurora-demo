// AJAX Delete Address
const confirmDeleteAddress = document.getElementById("confirmDeleteAddress");
const deleteAddress = document.querySelectorAll(".delete-address");
deleteAddress.forEach((btn) => {
  btn.addEventListener("click", () => {
    const deleteAddressModal = document.getElementById("deleteAddressModal");
    deleteAddressModal.dataset.addressid = btn.dataset.addressid;
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
          toast({
            title: data.title,
            message: data.message,
            type: data.type,
            duration: 3000,
          });
        }
      })
      .catch((err) => {
        console.error(err);
        alert("Có lỗi xảy ra, vui lòng thử lại!");
      });
  });
}
document.addEventListener("DOMContentLoaded", () => {
  const btnUpdateAddress = document.querySelectorAll(".update-address");
  btnUpdateAddress.forEach((btn) => {
    btn.addEventListener("click", () => {
      const addressId = btn.dataset.addressid;
      console.log("Check addressId=", addressId);
      fetch(`/address/update?addressId=${addressId}`)
        .then((res) => res.json())
        .then((data) => {
          document.querySelector("#updateAddressId").value = data.addressId;
          document.querySelector(".update-fullname").value = data.recipientName;
          document.querySelector(".update-phone").value = data.phone;
          document.querySelector(".update-description").value =
            data.description;
          document.querySelector(".update-default").checked =
            data.defaultAddress;

          provinceSelect.value = data.provinceId;
          console.log("Check provinceId", data.provinceId);

          provinceNameInput.value = data.province;
          provinceIdInput.value = data.provinceId;

          fetch("/api/address?type=district&province_id=" + data.provinceId)
            .then((res) => res.json())
            .then((districtData) => {
              if (districtData.data) {
                districtSelect.innerHTML =
                  '<option value="">Chọn Quận/Huyện</option>';
                districtData.data.forEach((d) => {
                  const opt = document.createElement("option");
                  opt.value = d.DistrictID;
                  opt.textContent = d.DistrictName;
                  districtSelect.appendChild(opt);
                });

                districtSelect.value = data.districtId;
                districtNameInput.value = data.district;
                districtIdInput.value = data.districtId;
              }

              return fetch(
                "/api/address?type=ward&district_id=" + data.districtId
              );
            })
            .then((res) => res.json())
            .then((wardData) => {
              if (wardData.data) {
                wardSelect.innerHTML =
                  '<option value="">Chọn Phường/Xã</option>';
                wardData.data.forEach((w) => {
                  const opt = document.createElement("option");
                  opt.value = w.WardCode;
                  opt.textContent = w.WardName;
                  wardSelect.appendChild(opt);
                });

                wardSelect.value = data.wardCode;
                wardNameInput.value = data.ward;
                wardCodeInput.value = data.wardCode;
              }
            });
        });
    });
  });
});
