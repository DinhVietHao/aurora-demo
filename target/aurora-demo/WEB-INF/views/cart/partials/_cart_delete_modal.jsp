<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="modal fade" id="deleteCartModal" tabindex="-1" aria-labelledby="deleteCartModalLabel" aria-hidden="true" data-cartitemid="" data-cartid="">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-danger" id="deleteCartModalLabel">
                    <i class="bi bi-exclamation-triangle-fill me-2 text-warning"></i>
                    Xóa sản phẩm
                </h5>
            </div>
            <div class="modal-body">Bạn có muốn xóa sản phẩm đang chọn?</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="confirmDeleteCartItem">Xác Nhận</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            </div>
        </div>
    </div>
</div>