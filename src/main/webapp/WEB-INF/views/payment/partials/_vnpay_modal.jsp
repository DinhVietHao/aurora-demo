<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="modal fade" id="vnpayModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h6 class="modal-title d-flex align-items-center">
                    <img
                        src="<c:url value='/assets/images/vnpay.png'/>"
                        alt="VNPAY"
                        width="32"
                        class="me-2"
                    />
                    Thanh toán bằng VNPAY-QR
                </h6>
                <a href="#" class="text-primary small" data-bs-dismiss="modal"
                    >Đổi phương thức khác</a
                >
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-5 text-center">
                        <div class="border rounded p-3">
                            <img
                                src="<c:url value='/assets/images/qr-demo.png'/>"
                                alt="QR Code"
                                class="img-fluid mb-2"
                            />
                            <div class="fw-bold">
                                Tổng tiền:
                                <span class="text-danger">302.700 đ</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-7">
                        <h6 class="mb-3">Quét mã QR để thanh toán</h6>
                        <ol class="ps-3">
                            <li>
                                Mở ứng dụng ngân hàng hỗ trợ VNPAY-QR trên điện
                                thoại
                            </li>
                            <li>Chọn <strong>Quét mã QR</strong></li>
                            <li>Quét mã trên trang này và thanh toán</li>
                        </ol>
                        <div class="mt-4 p-3 bg-light text-center rounded">
                            Giao dịch kết thúc sau
                            <span class="fw-bold text-danger">04 : 51</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
