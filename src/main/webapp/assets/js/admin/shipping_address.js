const provinceSelect = document.getElementById("province");
const districtSelect = document.getElementById("district");
const wardSelect = document.getElementById("ward");



fetch('https://provinces.open-api.vn/api/p/')
    .then(res => res.json())
    .then(provinces => {
        provinces.forEach(p => {
            let opt = document.createElement('option');
            opt.value = p.code;
            opt.text = p.name;
            provinceSelect.appendChild(opt);
        });

    });

provinceSelect.addEventListener("change", () => {
    fetch(`https://provinces.open-api.vn/api/p/${provinceSelect.value}?depth=2`)
        .then(res => res.json())
        .then(data => {
            data.districts.forEach(d => {
                let opt = document.createElement('option');
                opt.value = d.code;
                opt.text = d.name;
                districtSelect.appendChild(opt);
            });

        });
});

districtSelect.addEventListener("change", () => {
    fetch(`https://provinces.open-api.vn/api/d/${districtSelect.value}?depth=2`)
        .then(res => res.json())
        .then(data => {
            data.wards.forEach(w => {
                let opt = document.createElement('option');
                opt.value = w.code;
                opt.text = w.name;
                wardSelect.appendChild(opt);
            });

        });
});