<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- Modal warns when user clicks "Buy" without selecting product -->
<div class="modal fade" id="emptySelectionModal" tabindex="-1" aria-labelledby="emptySelectionModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content text-center p-2">
            <div class="modal-body">
                <div class="mb-2 text-primary">
                    <i class="bi bi-info-circle fs-1"></i>
                </div>
                <p class="mb-3">Bạn vẫn chưa chọn sản phẩm nào để mua</p>
                <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK, đã hiểu</button>
            </div>
        </div>
    </div>
</div>
<!--End Modal warns when user clicks "Buy" without selecting product -->