<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <!-- Modal Voucher Shop 1 Cart -->
    <div class="modal fade cart-shop-voucher" id="shopVoucherModal_shop1" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">BookHaven Store Voucher</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-3 align-items-center">
                        <div class="col-8">
                            <input type="text" class="form-control" placeholder="Nhập mã giảm giá">
                        </div>
                        <div class="col-4">
                            <button class="button-four w-100">Áp dụng</button>
                        </div>
                    </div>
                    <div class="list-group">
                        <label class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <span class="badge bg-success"> BookHaven Store</span>
                                <span class="ms-2">Giảm 8% tối đa 30K</span><br>
                                <small>Cho đơn từ 199K - HSD: 25/08/25</small>
                            </div>
                            <input type="radio" name="voucherShopDiscount_shop1" value="discount1"
                                data-text="Giảm 8% tối đa 30K" data-discount="8" data-type="percent" data-max="30000"
                                data-min-order-amount="199000">
                        </label>

                        <label class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <span class="badge bg-success"> BookHaven Store</span>
                                <span class="ms-2">Giảm 15K</span><br>
                                <small>Cho đơn từ 199K - HSD: 25/08/25</small>
                            </div>
                            <input type="radio" name="voucherShopDiscount_shop1" value="discount2" data-text="Giảm 15K"
                                data-discount="15000" data-min-order-amount="199000">
                        </label>
                    </div>
                </div>

                <!-- Footer -->
                <div class="modal-footer">
                    <button type="button" class="button-five" data-bs-dismiss="modal">Trở lại</button>
                    <button type="button" class="button-four confirmShopVoucher" data-shop-id="shop1">OK</button>
                </div>
            </div>
        </div>
    </div>
    <!--End Modal Voucher Shop 1 Cart -->

    <!-- Modal Voucher Shop 2 Cart -->
    <div class="modal fade cart-shop-voucher" id="shopVoucherModal_shop2" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Hanveta Store Voucher</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row mb-3 align-items-center">
                        <div class="col-8">
                            <input type="text" class="form-control" placeholder="Nhập mã giảm giá">
                        </div>
                        <div class="col-4">
                            <button class="button-four w-100">Áp dụng</button>
                        </div>
                    </div>
                    <div class="list-group">
                        <label class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <span class="badge bg-success"> BookHaven Store</span>
                                <span class="ms-2">Giảm 10% tối đa 40K</span><br>
                                <small>Cho đơn từ 299K - HSD: 25/08/25</small>
                            </div>
                            <input type="radio" name="voucherShopDiscount_shop2" value="discount1"
                                data-text="Giảm 10% tối đa 40K" data-discount="10" data-type="percent" data-max="40000"
                                data-min-order-amount="299000">
                        </label>

                        <label class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <span class="badge bg-success"> BookHaven Store</span>
                                <span class="ms-2">Giảm 30K</span><br>
                                <small>Cho đơn từ 199K - HSD: 25/08/25</small>
                            </div>
                            <input type="radio" name="voucherShopDiscount_shop2" value="discount2" data-text="Giảm 30K"
                                data-discount="30000" data-min-order-amount="199000">

                        </label>
                    </div>
                </div>

                <!-- Footer -->
                <div class="modal-footer">
                    <button type="button" class="button-five" data-bs-dismiss="modal">Trở lại</button>
                    <button type="button" class="button-four confirmShopVoucher" data-shop-id="shop2">OK</button>
                </div>
            </div>
        </div>
    </div>
    <!--End Modal Voucher Shop 2 Cart -->