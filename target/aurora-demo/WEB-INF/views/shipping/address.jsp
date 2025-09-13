<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <jsp:include page="/WEB-INF/views/layouts/_head.jsp">
            <jsp:param name="title" value="Địa chỉ giao hàng - Aurora"/>
        </jsp:include>

        <!-- CSS dùng chung + riêng trang -->
        <link rel="stylesheet" href="<c:url value='/assets/css/globals.css?v=1.0.1'/>">
        <link rel="stylesheet" href="<c:url value='/assets/css/home.css?v=1.0.1'/>">
        <!-- Dự án của bạn đang đặt file chuẩn ở assets/css/shipping_address.css -->
        <link rel="stylesheet" href="<c:url value='/assets/css/shipping_address.css?v=1.0.1'/>">
    </head>

    <body>
        <!-- Header tái sử dụng (đã có modal auth bên trong) -->
        <jsp:include page="/WEB-INF/views/layouts/_header.jsp"/>

        <div class="container mt-4">
            <form class="shipping-address" id="form-create-address">
                <div class="row mb-3">
                    <div class="col-md-6 form-group">
                        <label for="fullName" class="form-label">Họ tên</label>
                        <input type="text" class="form-control" id="fullName"
                               value="${fullName}" placeholder="Nhập họ tên">
                        <span class="form-message"></span>
                    </div>
                    <div class="col-md-6 form-group">
                        <label for="phone" class="form-label">Điện thoại di động</label>
                        <input type="text" class="form-control" id="phone"
                               value="${phone}" placeholder="Nhập số điện thoại">
                        <span class="form-message"></span>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-4 form-group">
                        <label for="province" class="form-label">Tỉnh/Thành phố</label>
                        <select class="form-select" id="province">
                            <option value="">Chọn Tỉnh/Thành phố</option>
                        </select>
                        <span class="form-message"></span>
                    </div>
                    <div class="col-md-4 form-group">
                        <label for="district" class="form-label">Quận/Huyện</label>
                        <select class="form-select" id="district">
                            <option value="">Chọn Quận/Huyện</option>
                        </select>
                        <span class="form-message"></span>
                    </div>
                    <div class="col-md-4 form-group">
                        <label for="ward" class="form-label">Phường/Xã</label>
                        <select class="form-select" id="ward">
                            <option value="">Chọn Phường/Xã</option>
                        </select>
                        <span class="form-message"></span>
                    </div>
                </div>

                <div class="mb-3 form-group">
                    <label for="address" class="form-label">Địa chỉ</label>
                    <textarea class="form-control" id="address"
                              placeholder="Ví dụ: 52, đường Trần Hưng Đạo">${address}</textarea>
                    <span class="form-message"></span>
                </div>

                <button type="reset" class="btn btn-secondary">Hủy bỏ</button>
                <button type="submit" class="btn btn-primary mx-2">Giao đến địa chỉ này</button>
            </form>
        </div>

        <jsp:include page="/WEB-INF/views/layouts/_footer.jsp"/>
        <jsp:include page="/WEB-INF/views/layouts/_scripts.jsp"/>

        <!-- JS trang -->
        <script src="<c:url value='/assets/js/shipping_address.js'/>"></script>
        <script src="<c:url value='/assets/js/validator.js'/>"></script>

        <!-- Inline validate giữ nguyên logic cũ -->
        <script>
            Validator({
                form: '#form-create-address',
                formGroupSelector: '.form-group',
                errorSelector: '.form-message',
                rules: [
                    Validator.isRequired('#fullName', 'Vui lòng nhập họ tên'),
                    Validator.isRequired('#phone', 'Vui lòng nhập số điện thoại'),
                    Validator.isRequired('#province', 'Vui lòng chọn Tỉnh/Thành phố'),
                    Validator.isRequired('#district', 'Vui lòng chọn Quận/Huyện'),
                    Validator.isRequired('#ward', 'Vui lòng chọn Phường/Xã'),
                    Validator.isRequired('#address', 'Vui lòng nhập đại chỉ')
                ]
            });
        </script>
    </body>
</html>