package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.dao.VoucherDAO;
import com.group01.aurora_demo.admin.model.Voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "VoucherManagementServlet", urlPatterns = {
        "/admin/vouchers",
        "/admin/vouchers/create",
        "/admin/vouchers/edit",
        "/admin/vouchers/delete",
        "/admin/vouchers/view"
})
public class VoucherManagementServlet extends HttpServlet {

    private final VoucherDAO voucherDAO = new VoucherDAO();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        String action = request.getPathInfo();

        if (action == null) {
            action = "";
        }

        if (path.equals("/admin/vouchers/view")) {
            viewVoucher(request, response);
        } else if (path.equals("/admin/vouchers/create")) {
            showCreateForm(request, response);
        } else if (path.equals("/admin/vouchers/edit")) {
            showEditForm(request, response);
        } else {
            listVouchers(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/admin/vouchers/create")) {
            createVoucher(request, response);
        } else if (path.equals("/admin/vouchers/edit")) {
            updateVoucher(request, response);
        } else if (path.equals("/admin/vouchers/delete")) {
            deleteVoucher(request, response);
        }
    }

    private void listVouchers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String statusFilter = request.getParameter("status");
        List<Voucher> vouchers;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            vouchers = voucherDAO.getVouchersByStatus(statusFilter);
        } else {
            vouchers = voucherDAO.getAllVouchers();
        }

        int activeCount = voucherDAO.getActiveVouchersCount();
        int pendingCount = voucherDAO.getPendingVouchersCount();
        int expiredCount = voucherDAO.getExpiredVouchersCount();
        int totalUsageCount = voucherDAO.getTotalUsageCount();

        request.setAttribute("vouchers", vouchers);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("expiredCount", expiredCount);
        request.setAttribute("totalUsageCount", totalUsageCount);
        request.setAttribute("statusFilter", statusFilter);

        request.getRequestDispatcher("/WEB-INF/views/admin/voucher_management.jsp").forward(request, response);
    }

    private void viewVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String voucherId = request.getParameter("id");

        if (voucherId != null && !voucherId.isEmpty()) {
            Optional<Voucher> optVoucher = voucherDAO.getVoucherById(Long.parseLong(voucherId));
            if (optVoucher.isPresent()) {
                request.setAttribute("voucher", optVoucher.get());
                request.getRequestDispatcher("/WEB-INF/views/admin/voucher_details.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=voucher_not_found");
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String> discountTypes = voucherDAO.getAllDiscountTypes();
        List<String> statusCodes = voucherDAO.getAllVoucherStatuses();

        request.setAttribute("discountTypes", discountTypes);
        request.setAttribute("statusCodes", statusCodes);

        request.getRequestDispatcher("/WEB-INF/views/admin/voucher_create.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String voucherId = request.getParameter("id");

        if (voucherId != null && !voucherId.isEmpty()) {
            Optional<Voucher> optVoucher = voucherDAO.getVoucherById(Long.parseLong(voucherId));
            if (optVoucher.isPresent()) {
                List<String> discountTypes = voucherDAO.getAllDiscountTypes();
                List<String> statusCodes = voucherDAO.getAllVoucherStatuses();

                request.setAttribute("voucher", optVoucher.get());
                request.setAttribute("discountTypes", discountTypes);
                request.setAttribute("statusCodes", statusCodes);

                // Format dates for the form
                Voucher voucher = optVoucher.get();
                request.setAttribute("startAtFormatted", voucher.getStartAt().format(DATE_TIME_FORMATTER));
                request.setAttribute("endAtFormatted", voucher.getEndAt().format(DATE_TIME_FORMATTER));

                request.getRequestDispatcher("/WEB-INF/views/admin/voucher_create.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=voucher_not_found");
    }

    private void createVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Basic field validation before extracting full object
            String code = param(request, "code");
            if (code.isEmpty()) {
                request.setAttribute("error", "Voucher code is required");
                showCreateForm(request, response);
                return;
            }

            // Check if voucher code already exists
            if (voucherDAO.isVoucherCodeExists(code)) {
                request.setAttribute("error", "Voucher code already exists. Please use a different code.");
                // Preserve form data
                preserveFormData(request);
                showCreateForm(request, response);
                return;
            }

            Voucher voucher = extractVoucherFromRequest(request);
            voucher.setUsageCount(0);

            // Validate discount type before saving
            String discountType = voucher.getDiscountType();
            List<String> validTypes = voucherDAO.getAllDiscountTypes();

            if (!validTypes.contains(discountType)) {
                request.setAttribute("error", "Invalid discount type: " + discountType + ". Valid types are: "
                        + String.join(", ", validTypes));
                request.setAttribute("voucher", voucher);
                showCreateForm(request, response);
                return;
            }

            // Additional business rule validations
            StringBuilder errors = new StringBuilder();

            // Validate value based on discount type
            if ("PERCENT".equals(discountType) && voucher.getValue().compareTo(new BigDecimal("100")) > 0) {
                errors.append("Percentage discount cannot exceed 100%. ");
            }

            // Validate minimum order amount vs max discount
            if (voucher.getMaxAmount() != null && voucher.getMinOrderAmount() != null) {
                if (voucher.getMaxAmount().compareTo(voucher.getMinOrderAmount()) > 0) {
                    errors.append("Maximum discount amount cannot be greater than minimum order amount. ");
                }
            }

            // Usage limits validation
            if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) {
                errors.append("Usage limit must be greater than zero. ");
            }

            if (voucher.getPerUserLimit() != null && voucher.getPerUserLimit() <= 0) {
                errors.append("Per-user limit must be greater than zero. ");
            }

            // Date validation (additional check beyond what's in extractVoucherFromRequest)
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartAt().isBefore(now)) {
                errors.append("Ngày bắt đầu không được nằm trong quá khứ. ");
            }
            
            if (voucher.getEndAt().isBefore(voucher.getStartAt()) || voucher.getEndAt().isEqual(voucher.getStartAt())) {
                errors.append("Ngày kết thúc phải sau ngày bắt đầu. ");
            }

            // If there are validation errors, show form with error messages
            if (errors.length() > 0) {
                request.setAttribute("error", errors.toString());
                request.setAttribute("voucher", voucher);
                showCreateForm(request, response);
                return;
            }

            boolean success = voucherDAO.createVoucher(voucher);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/vouchers?success=voucher_created");
            } else {
                request.setAttribute("error", "Failed to create voucher");
                request.setAttribute("voucher", voucher);
                showCreateForm(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error processing voucher data: " + e.getMessage());
            preserveFormData(request);
            showCreateForm(request, response);
        }
    }

    /**
     * Helper method to preserve form data when validation fails
     */
    private void preserveFormData(HttpServletRequest request) {
        request.setAttribute("code", param(request, "code"));
        request.setAttribute("discountType", param(request, "discountType"));
        request.setAttribute("value", param(request, "value"));
        request.setAttribute("maxAmount", param(request, "maxAmount"));
        request.setAttribute("minOrderAmount", param(request, "minOrderAmount"));
        request.setAttribute("startAt", param(request, "startAt"));
        request.setAttribute("endAt", param(request, "endAt"));
        request.setAttribute("usageLimit", param(request, "usageLimit"));
        request.setAttribute("perUserLimit", param(request, "perUserLimit"));
        request.setAttribute("status", param(request, "status"));
        request.setAttribute("description", param(request, "description"));
    }

    /**
     * Helper method similar to the one in other servlets
     */
    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    private void updateVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String voucherId = request.getParameter("id");

        if (voucherId == null || voucherId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=invalid_voucher_id");
            return;
        }

        try {
            long id = Long.parseLong(voucherId);
            Optional<Voucher> existingVoucherOpt = voucherDAO.getVoucherById(id);

            if (!existingVoucherOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=voucher_not_found");
                return;
            }

            Voucher existingVoucher = existingVoucherOpt.get();

            // Basic field validation before extracting full object
            String code = param(request, "code");
            if (code.isEmpty()) {
                request.setAttribute("error", "Voucher code is required");
                showEditForm(request, response);
                return;
            }

            // Check if voucher code already exists and is different from current voucher
            if (!code.equals(existingVoucher.getCode()) && voucherDAO.isVoucherCodeExists(code)) {
                request.setAttribute("error", "Voucher code already exists. Please use a different code.");
                showEditForm(request, response);
                return;
            }

            // Extract and validate voucher data
            Voucher voucher;
            try {
                voucher = extractVoucherFromRequest(request);
                voucher.setVoucherId(id);
                voucher.setUsageCount(existingVoucher.getUsageCount());
            } catch (Exception e) {
                request.setAttribute("error", "Invalid voucher data: " + e.getMessage());
                showEditForm(request, response);
                return;
            }

            // Validate discount type
            String discountType = voucher.getDiscountType();
            List<String> validTypes = voucherDAO.getAllDiscountTypes();

            if (!validTypes.contains(discountType)) {
                request.setAttribute("error", "Invalid discount type: " + discountType + ". Valid types are: "
                        + String.join(", ", validTypes));
                setEditFormAttributes(request, voucher, validTypes);
                request.getRequestDispatcher("/WEB-INF/views/admin/voucher_create.jsp").forward(request, response);
                return;
            }

            // Additional business rule validations
            StringBuilder errors = new StringBuilder();

            // Validate value based on discount type
            if ("PERCENT".equals(discountType) && voucher.getValue().compareTo(new BigDecimal("100")) > 0) {
                errors.append("Percentage discount cannot exceed 100%. ");
            }

            // Validate minimum order amount vs max discount
            if (voucher.getMaxAmount() != null && voucher.getMinOrderAmount() != null) {
                if (voucher.getMaxAmount().compareTo(voucher.getMinOrderAmount()) > 0) {
                    errors.append("Maximum discount amount cannot be greater than minimum order amount. ");
                }
            }

            // Usage limits validation
            if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) {
                errors.append("Usage limit must be greater than zero. ");
            }

            if (voucher.getPerUserLimit() != null && voucher.getPerUserLimit() <= 0) {
                errors.append("Per-user limit must be greater than zero. ");
            }

            // If there are validation errors, show form with error messages
            if (errors.length() > 0) {
                request.setAttribute("error", errors.toString());
                setEditFormAttributes(request, voucher, validTypes);
                request.getRequestDispatcher("/WEB-INF/views/admin/voucher_create.jsp").forward(request, response);
                return;
            }

            // Check if voucher is currently in use and dates are being changed
            if (existingVoucher.getUsageCount() > 0) {
                if (!existingVoucher.getStartAt().equals(voucher.getStartAt()) ||
                        !existingVoucher.getEndAt().equals(voucher.getEndAt())) {
                    errors.append("Cannot change dates for vouchers that are already in use. ");
                    request.setAttribute("error", errors.toString());
                    setEditFormAttributes(request, voucher, validTypes);
                    request.getRequestDispatcher("/WEB-INF/views/admin/voucher_create.jsp").forward(request, response);
                    return;
                }
            }

            boolean success = voucherDAO.updateVoucher(voucher);
            if (success) {
                response.sendRedirect(
                        request.getContextPath() + "/admin/vouchers/view?id=" + id + "&success=voucher_updated");
            } else {
                request.setAttribute("error", "Failed to update voucher");
                setEditFormAttributes(request, voucher, validTypes);
                request.getRequestDispatcher("/WEB-INF/views/admin/voucher_create.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=invalid_voucher_id_format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/admin/vouchers/edit?id=" + voucherId + "&error=" + e.getMessage());
        }
    }

    /**
     * Helper method to set attributes for edit form
     */
    private void setEditFormAttributes(HttpServletRequest request, Voucher voucher, List<String> discountTypes)
            throws ServletException, IOException {
        request.setAttribute("voucher", voucher);
        request.setAttribute("discountTypes", discountTypes);
        request.setAttribute("statusCodes", voucherDAO.getAllVoucherStatuses());
        request.setAttribute("startAtFormatted", voucher.getStartAt().format(DATE_TIME_FORMATTER));
        request.setAttribute("endAtFormatted", voucher.getEndAt().format(DATE_TIME_FORMATTER));
    }

    private void deleteVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String voucherId = request.getParameter("id");

        if (voucherId != null && !voucherId.isEmpty()) {
            try {
                long id = Long.parseLong(voucherId);
                boolean success = voucherDAO.deleteVoucher(id);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/vouchers?success=voucher_deleted");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=delete_failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=" + e.getMessage());
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/vouchers?error=invalid_voucher_id");
        }
    }

    private Voucher extractVoucherFromRequest(HttpServletRequest request) {
        Voucher voucher = new Voucher();

        String code = request.getParameter("code");
        String discountType = request.getParameter("discountType");
        String valueStr = request.getParameter("value");
        String maxAmountStr = request.getParameter("maxAmount");
        String minOrderAmountStr = request.getParameter("minOrderAmount");
        String startAtStr = request.getParameter("startAt");
        String endAtStr = request.getParameter("endAt");
        String usageLimitStr = request.getParameter("usageLimit");
        String perUserLimitStr = request.getParameter("perUserLimit");
        String status = request.getParameter("status");
        String defaultStatus = request.getParameter("defaultStatus");
        String description = request.getParameter("description");
        
        // Nếu checkbox status không được chọn, sử dụng giá trị mặc định
        if (status == null || status.isEmpty()) {
            status = defaultStatus != null ? defaultStatus : "PENDING";
        }

        voucher.setCode(code);
        voucher.setDiscountType(discountType);

        // Kiểm tra và làm sạch giá trị trước khi chuyển đổi
        if (valueStr != null && !valueStr.trim().isEmpty()) {
            try {
                // Thay thế dấu phẩy bằng dấu chấm nếu có
                valueStr = valueStr.trim().replace(',', '.');
                voucher.setValue(new BigDecimal(valueStr));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Giá trị giảm không hợp lệ: " + valueStr);
            }
        } else {
            throw new NumberFormatException("Giá trị giảm không được để trống");
        }

        // Xử lý giá trị tối đa
        if (maxAmountStr != null && !maxAmountStr.trim().isEmpty()) {
            try {
                maxAmountStr = maxAmountStr.trim().replace(',', '.');
                voucher.setMaxAmount(new BigDecimal(maxAmountStr));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Giá trị giảm tối đa không hợp lệ: " + maxAmountStr);
            }
        }

        // Xử lý giá trị đơn hàng tối thiểu
        if (minOrderAmountStr != null && !minOrderAmountStr.trim().isEmpty()) {
            try {
                minOrderAmountStr = minOrderAmountStr.trim().replace(',', '.');
                voucher.setMinOrderAmount(new BigDecimal(minOrderAmountStr));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Giá trị đơn hàng tối thiểu không hợp lệ: " + minOrderAmountStr);
            }
        } else {
            // Nếu không có giá trị, đặt là 0
            voucher.setMinOrderAmount(BigDecimal.ZERO);
        }

        // Parse dates
        try {
            if (startAtStr != null && !startAtStr.trim().isEmpty()) {
                voucher.setStartAt(LocalDateTime.parse(startAtStr.trim(), DATE_TIME_FORMATTER));
            } else {
                throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
            }

            if (endAtStr != null && !endAtStr.trim().isEmpty()) {
                voucher.setEndAt(LocalDateTime.parse(endAtStr.trim(), DATE_TIME_FORMATTER));
            } else {
                throw new IllegalArgumentException("Ngày kết thúc không được để trống");
            }

            // Kiểm tra ngày bắt đầu không được trong quá khứ
            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartAt().isBefore(now)) {
                throw new IllegalArgumentException("Ngày bắt đầu không được nằm trong quá khứ");
            }
            
            // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
            if (voucher.getEndAt().isBefore(voucher.getStartAt()) || voucher.getEndAt().isEqual(voucher.getStartAt())) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException("Định dạng ngày giờ không hợp lệ: " + e.getMessage());
        }

        // Xử lý giới hạn sử dụng - bắt buộc
        if (usageLimitStr != null && !usageLimitStr.trim().isEmpty()) {
            try {
                voucher.setUsageLimit(Integer.parseInt(usageLimitStr.trim()));
                if (voucher.getUsageLimit() < 1) {
                    throw new NumberFormatException("Giới hạn sử dụng phải lớn hơn 0");
                }
            } catch (NumberFormatException e) {
                if (e.getMessage().contains("phải lớn hơn 0")) {
                    throw e;
                }
                throw new NumberFormatException("Giới hạn sử dụng phải là số nguyên: " + usageLimitStr);
            }
        } else {
            throw new IllegalArgumentException("Giới hạn sử dụng là bắt buộc");
        }

        // Xử lý giới hạn sử dụng mỗi người dùng
        if (perUserLimitStr != null && !perUserLimitStr.trim().isEmpty()) {
            try {
                voucher.setPerUserLimit(Integer.parseInt(perUserLimitStr.trim()));
                if (voucher.getPerUserLimit() < 0) {
                    throw new NumberFormatException("Giới hạn mỗi người dùng không được âm");
                }
            } catch (NumberFormatException e) {
                if (e.getMessage().contains("không được âm")) {
                    throw e;
                }
                throw new NumberFormatException("Giới hạn mỗi người dùng phải là số nguyên: " + perUserLimitStr);
            }
        }

        voucher.setStatus(status);
        voucher.setDescription(description);

        return voucher;
    }
}