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
            if (defaultWard) {
                wardSelect.value = defaultWard;
            }
        });
}
