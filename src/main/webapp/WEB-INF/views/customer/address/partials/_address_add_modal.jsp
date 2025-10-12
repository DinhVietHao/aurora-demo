<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <div class="modal fade" id="addAddressModal" tabindex="-1" aria-labelledby="addAddressModalLabel"
            aria-hidden="true">
            <div class="modal-dialog ">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="addAddressModalLabel">Địa chỉ của tôi</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form class="shipping-address" id="form-create-address" action="/address/add" method="post">
                            <input type="hidden" name="from" value="address">
                            <div class="row mb-3">
                                <div class="col-md-6 form-group">
                                    <label for="fullName" class="form-label">Họ tên</label>
                                    <input type="text" class="form-control" id="fullName" placeholder="Nhập họ tên"
                                        name="fullName">
                                    <span class="form-message"></span>
                                </div>
                                <div class="col-md-6 form-group">
                                    <label for="phone" class="form-label">Điện thoại di động</label>
                                    <input type="text" class="form-control" id="phone" placeholder="Nhập số điện thoại"
                                        name="phone">
                                    <span class="form-message"></span>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6 form-group">
                                    <label for="province" class="form-label">Tỉnh/Thành phố</label>
                                    <select class="form-select" id="addProvince" name="city">
                                        <option value="">Chọn Tỉnh/Thành phố</option>
                                    </select>
                                    <span class="form-message"></span>
                                </div>
                                <div class="col-md-6 form-group">
                                    <label for="ward" class="form-label">Phường/Xã</label>
                                    <select class="form-select" id="addWard" name="ward">
                                        <option value="">Chọn Phường/Xã</option>
                                    </select>
                                    <span class="form-message"></span>
                                </div>
                            </div>

                            <div class="mb-3 form-group">
                                <label for="address" class="form-label">Địa chỉ</label>
                                <textarea class="form-control" id="address" placeholder="Ví dụ: 52, đường Trần Hưng Đạo"
                                    name="description"></textarea>
                                <span class="form-message"></span>
                            </div>
                            <div class="form-check mb-3 <c:if test='${empty addresses}'>d-none</c:if>">
                                <input class="form-check-input" type="checkbox" value="true" id="checkChecked"
                                    name="isDefault" <c:if test='${empty addresses}'>checked</c:if> />
                                <label class="form-check-label" for="checkChecked">
                                    Đặt làm địa chỉ mặc định
                                </label>
                            </div>

                            <div class="modal-footer">
                                <button type="reset" class="button-five" data-bs-dismiss="modal">Trở
                                    lại</button>
                                <button type="submit" class="button-four">Hoàn thành</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>