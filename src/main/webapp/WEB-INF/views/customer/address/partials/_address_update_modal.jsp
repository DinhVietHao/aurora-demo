<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <div class="modal fade  " id="updateAddressModal" tabindex="-1" aria-labelledby="updateAddressModalLabel"
        aria-hidden="true">
        <div class="modal-dialog ">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="updateAddressModalLabel">Địa chỉ của tôi</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form class="shipping-address" id="form-update-address" method="POST" action="/address/update">
                        <input type="hidden" id="updateAddressId" name="addressId" value="" />
                        <input type="hidden" name="from" value="address">
                        <div class="row mb-3">
                            <div class="col-md-6 form-group">
                                <label for="fullName" class="form-label">Họ tên</label>
                                <input type="text" class="form-control update-fullname" id="updateFullname"
                                    placeholder="Nhập họ tên" name="fullName" value="">
                                <span class="form-message"></span>
                            </div>
                            <div class="col-md-6 form-group">
                                <label for="phone" class="form-label">Điện thoại di động</label>
                                <input type="text" class="form-control update-phone" id="updatePhone"
                                    placeholder="Nhập số điện thoại" name="phone" value="">
                                <span class="form-message"></span>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6 form-group">
                                <label for="updateProvince" class="form-label">Tỉnh/Thành phố</label>
                                <select class="form-select " id="updateProvince" name="city">
                                    <option value="" class="update-city">Chọn Tỉnh/Thành phố</option>
                                </select>
                                <span class=" form-message"></span>
                            </div>
                            <div class="col-md-6 form-group">
                                <label for="updateDistrict" class="form-label">Quận/Huyện</label>
                                <select id="updateDistrict" class="form-select" name="district">
                                    <option value="">-- Chọn Quận/Huyện --</option>
                                </select>
                            </div>
                            <div class="col-md-12 form-group">
                                <label for="updateWard" class="form-label">Phường/Xã</label>
                                <select class="form-select" id="updateWard" name="ward">
                                    <option value="">Chọn Phường/Xã</option>
                                </select>
                                <span class="form-message"></span>
                            </div>
                            <input type="hidden" id="updateProvinceNameInput" name="cityName">
                            <input type="hidden" id="updateDistrictNameInput" name="districtName">
                            <input type="hidden" id="updateWardNameInput" name="wardName">

                            <input type="hidden" id="updateProvinceIdInput" name="provinceId">
                            <input type="hidden" id="updateDistrictIdInput" name="districtId">
                            <input type="hidden" id="updateWardCodeInput" name="wardCode">
                        </div>

                        <div class="mb-3 form-group">
                            <label for="address" class="form-label">Địa chỉ</label>
                            <textarea class="form-control update-description" id="updateAddress"
                                placeholder="Ví dụ: 52, đường Trần Hưng Đạo" name="description" value=""></textarea>
                            <span class="form-message"></span>
                        </div>
                        <div class="form-check mb-3">
                            <input class="form-check-input update-default" type="checkbox" value="" id="checkChecked"
                                name="isDefault">
                            <label class="form-check-label" for="checkChecked">
                                Đặt làm địa chỉ mặc định
                            </label>
                        </div>

                        <div class="modal-footer">
                            <button type="reset" class="button-five" data-bs-dismiss="modal">Trở
                                lại</button>
                            <button class="button-four form-submit">Hoàn thành</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>