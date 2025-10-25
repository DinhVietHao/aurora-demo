package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.service.CategoryService;
import com.group01.aurora_demo.admin.service.VATService;
import com.group01.aurora_demo.admin.model.Category;
import com.group01.aurora_demo.admin.model.VAT;
import com.group01.aurora_demo.common.util.PaginatedResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for managing categories (admin only)
 *
 * @author Aurora Team
 */
@WebServlet(name = "CategoryManagementServlet", urlPatterns = {"/admin/category-management"})
public class CategoryManagementServlet extends HttpServlet {
    private CategoryService categoryService;
    private VATService vatService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.categoryService = new CategoryService();
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
                listCategories(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteCategory(request, response);
                break;
            default:
                listCategories(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management");
            return;
        }

        switch (action) {
            case "add":
                addCategory(request, response);
                break;
            case "update":
                updateCategory(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/category-management");
                break;
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String searchTerm = request.getParameter("search");
        String vatCodeFilter = request.getParameter("vatFilter");

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
        PaginatedResult<Category> result = categoryService.getCategoriesWithPagination(
            searchTerm, vatCodeFilter, page, pageSize
        );

        // Get all VAT codes for filter dropdown
        List<VAT> vatList = vatService.getAllVAT();

        // Set attributes for JSP
        request.setAttribute("categoryList", result.getData());
        request.setAttribute("pagination", result.getPagination());
        request.setAttribute("vatList", vatList);
        request.setAttribute("searchTerm", searchTerm != null ? searchTerm : "");
        request.setAttribute("vatFilter", vatCodeFilter != null ? vatCodeFilter : "");
        request.setAttribute("currentPageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/admin/category-management.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management");
            return;
        }

        try {
            long categoryId = Long.parseLong(categoryIdStr);
            Category category = categoryService.getCategoryById(categoryId);

            if (category == null) {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?error=notfound");
                return;
            }

            request.setAttribute("category", category);
            request.setAttribute("mode", "edit");
            List<Category> categoryList = categoryService.getAllCategories();
            List<VAT> vatList = vatService.getAllVAT();

            request.setAttribute("categoryList", categoryList);
            request.setAttribute("vatList", vatList);
            request.getRequestDispatcher("/WEB-INF/views/admin/category-management.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management?error=invalid");
        }
    }

    private void addCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String vatCode = request.getParameter("vatCode");

            if (categoryService.addCategory(name, vatCode)) {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?error=add");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management?error=invalid");
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long categoryId = Long.parseLong(request.getParameter("categoryId"));
            String name = request.getParameter("name");
            String vatCode = request.getParameter("vatCode");

            if (categoryService.updateCategory(categoryId, name, vatCode)) {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?error=update");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management?error=invalid");
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management?error=invalid");
            return;
        }

        try {
            long categoryId = Long.parseLong(categoryIdStr);

            if (categoryService.deleteCategory(categoryId)) {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/category-management?error=delete");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/category-management?error=invalid");
        }
    }
}
