<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="vi">

    <head>
        <meta charset="UTF-8">
        <title>Checkout GHN Test</title>
    </head>

    <body>
        <h3>Tính phí vận chuyển GHN</h3>

        <label>Quận Gửi:</label>
        <input type="number" id="fromDistrict" value="1454" /><br />
        <label>Phường Gửi:</label>
        <input type="text" id="fromWard" value="21211" /><br />
        <label>Quận Nhận:</label>
        <input type="number" id="toDistrict" value="1452" /><br />
        <label>Phường Nhận:</label>
        <input type="text" id="toWard" value="21012" /><br />

        <label>Chiều cao (cm):</label><input type="number" id="height" value="50" /><br />
        <label>Chiều dài (cm):</label><input type="number" id="length" value="20" /><br />
        <label>Chiều rộng (cm):</label><input type="number" id="width" value="20" /><br />
        <label>Trọng lượng (gram):</label><input type="number" id="weight" value="200" /><br />

        <label>Giá trị bảo hiểm (VND):</label><input type="number" id="insurance" value="10000" /><br />
        <label>COD thất bại (VND):</label><input type="number" id="codFailed" value="2000" /><br />

        <h4>Thông tin sản phẩm</h4>
        <label>Tên:</label><input type="text" id="itemName" value="TEST1" /><br />
        <label>Số lượng:</label><input type="number" id="itemQuantity" value="1" /><br />
        <label>Chiều cao (cm):</label><input type="number" id="itemHeight" value="200" /><br />
        <label>Chiều dài (cm):</label><input type="number" id="itemLength" value="200" /><br />
        <label>Chiều rộng (cm):</label><input type="number" id="itemWidth" value="200" /><br />
        <label>Trọng lượng (gram):</label><input type="number" id="itemWeight" value="1000" /><br />

        <button id="btnCalc">Tính phí GHN</button>

        <p>Phí vận chuyển: <span id="fee">0</span> ₫</p>

        <script>
            document.getElementById("btnCalc").addEventListener("click", () => {
                const params = new URLSearchParams({
                    fromDistrict: document.getElementById("fromDistrict").value,
                    fromWard: document.getElementById("fromWard").value,
                    toDistrict: document.getElementById("toDistrict").value,
                    toWard: document.getElementById("toWard").value,
                    height: document.getElementById("height").value,
                    length: document.getElementById("length").value,
                    width: document.getElementById("width").value,
                    weight: document.getElementById("weight").value,
                    insurance: document.getElementById("insurance").value,
                    codFailed: document.getElementById("codFailed").value,
                    itemName: document.getElementById("itemName").value,
                    itemQuantity: document.getElementById("itemQuantity").value,
                    itemHeight: document.getElementById("itemHeight").value,
                    itemLength: document.getElementById("itemLength").value,
                    itemWidth: document.getElementById("itemWidth").value,
                    itemWeight: document.getElementById("itemWeight").value
                });

                fetch("ghn", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: params
                })
                    .then(res => res.json())
                    .then(data => {
                        document.getElementById("fee").textContent = data.fee ? data.fee.toLocaleString("vi-VN") : "Lỗi";
                    });
            });
        </script>
    </body>

    </html>