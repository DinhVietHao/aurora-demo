function initAddressSelects(
  provinceSelect,
  districtSelect,
  wardSelect,
  provinceNameInput,
  districtNameInput,
  wardNameInput,
  provinceIdInput,
  districtIdInput,
  wardCodeInput
) {
  fetch("/api/address?type=province")
    .then((res) => res.json())
    .then((provinceData) => {
      if (provinceData.data && Array.isArray(provinceData.data)) {
        provinceData.data.forEach((p) => {
          const opt = document.createElement("option");
          opt.value = p.ProvinceID;
          opt.textContent = p.ProvinceName;
          provinceSelect.appendChild(opt);
        });
      }
    });

  provinceSelect.addEventListener("change", function () {
    const id = provinceSelect.value;
    const name =
      provinceSelect.options[provinceSelect.selectedIndex]?.text || "";
    provinceNameInput.value = name;
    provinceIdInput.value = id;

    districtSelect.innerHTML =
      '<option value="">-- Chọn Quận/Huyện --</option>';
    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
    districtSelect.disabled = !id;
    wardSelect.disabled = true;

    if (!id) return;

    fetch("/api/address?type=district&province_id=" + id)
      .then((res) => res.json())
      .then((data) => {
        if (data.data && Array.isArray(data.data)) {
          data.data.forEach((d) => {
            const opt = document.createElement("option");
            opt.value = d.DistrictID;
            opt.textContent = d.DistrictName;
            districtSelect.appendChild(opt);
          });
        }
      });
  });

  districtSelect.addEventListener("change", function () {
    const id = districtSelect.value;
    const name =
      districtSelect.options[districtSelect.selectedIndex]?.text || "";
    districtNameInput.value = name;
    districtIdInput.value = id;

    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
    wardSelect.disabled = !id;

    if (!id) return;

    fetch("/api/address?type=ward&district_id=" + id)
      .then((res) => res.json())
      .then((data) => {
        if (data.data && Array.isArray(data.data)) {
          data.data.forEach((w) => {
            const opt = document.createElement("option");
            opt.value = w.WardCode;
            opt.textContent = w.WardName;
            wardSelect.appendChild(opt);
          });
        }
      });
  });

  wardSelect.addEventListener("change", function () {
    const code = wardSelect.value;
    const name = wardSelect.options[wardSelect.selectedIndex]?.text || "";
    wardNameInput.value = name;
    wardCodeInput.value = code;
  });
}
