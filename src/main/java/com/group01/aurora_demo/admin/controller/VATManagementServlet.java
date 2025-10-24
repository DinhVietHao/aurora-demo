package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.model.VAT;
import com.group01.aurora_demo.admin.service.VATService;
import com.group01.aurora_demo.common.util.PaginatedResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for managing VAT rates (admin only)
 *
 * @author Aurora Team
 */
@WebServlet(name = "VATManagementServlet", urlPatterns = {"/admin/vat-management"})
public class VATManagementServlet extends HttpServlet {
    private VATService vatService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.vatService = new VATService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listVAT(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteVAT(request, response);
                break;
            default:
                listVAT(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management");
            return;
        }

        switch (action) {
            case "add":
                addVAT(request, response);
                break;
            case "update":
                updateVAT(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/vat-management");
                break;
        }
    }

    private void listVAT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String searchTerm = request.getParameter("search");

        // Get pagination parameters
        int page = 1;
        int pageSize = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        String pageSizeParam = request.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.trim().isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
                if (pageSize < 5) pageSize = 5;
                if (pageSize > 100) pageSize = 100;
            } catch (NumberFormatException e) {
                pageSize = 10;
            }
        }

        // Get paginated and filtered results
        PaginatedResult<VAT> result = vatService.getVATWithPagination(searchTerm, page, pageSize);

        // Set attributes for JSP
        request.setAttribute("vatList", result.getData());
        request.setAttribute("pagination", result.getPagination());
        request.setAttribute("searchTerm", searchTerm != null ? searchTerm : "");
        request.setAttribute("currentPageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/admin/vat-management.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vatCode = request.getParameter("vatCode");
        if (vatCode == null || vatCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management");
            return;
        }

        VAT vat = vatService.getVATByCode(vatCode);
        if (vat == null) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=notfound");
            return;
        }

        request.setAttribute("vat", vat);
        request.setAttribute("mode", "edit");
        List<VAT> vatList = vatService.getAllVAT();
        request.setAttribute("vatList", vatList);
        request.getRequestDispatcher("/jsp/admin/vat-management.jsp").forward(request, response);
    }

    private void addVAT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String vatCode = request.getParameter("vatCode");
            double vatRate = Double.parseDouble(request.getParameter("vatRate"));
            String description = request.getParameter("description");

            if (vatService.addVAT(vatCode, vatRate, description)) {
                response.sendRedirect(request.getContextPath() + "/admin/vat-management?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=add");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=invalid");
        }
    }

    private void updateVAT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String vatCode = request.getParameter("vatCode");
            double vatRate = Double.parseDouble(request.getParameter("vatRate"));
            String description = request.getParameter("description");

            if (vatService.updateVAT(vatCode, vatRate, description)) {
                response.sendRedirect(request.getContextPath() + "/admin/vat-management?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=update");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=invalid");
        }
    }

    private void deleteVAT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vatCode = request.getParameter("vatCode");
        if (vatCode == null || vatCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=invalid");
            return;
        }

        if (vatService.deleteVAT(vatCode)) {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management?success=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/vat-management?error=delete");
        }
    }
}
