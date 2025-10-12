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
                        <input type="hidden" name="addressId" value="" />
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
                                <label for="province" class="form-label">Tỉnh/Thành phố</label>
                                <select class="form-select" name="city" id="updateProvince">
                                    <option value="" class="update-city"></option>
                                </select>
                                <span class="form-message"></span>
                            </div>
                            <div class="col-md-6 form-group">
                                <label for="ward" class="form-label">Phường/Xã</label>
                                <select class="form-select " name="ward" id="updateWard">
                                    <option value="" class="update-ward"></option>
                                </select>
                                <span class="form-message"></span>
                            </div>
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