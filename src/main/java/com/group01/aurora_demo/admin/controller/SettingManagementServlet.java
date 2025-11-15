package com.group01.aurora_demo.admin.controller;

import com.group01.aurora_demo.admin.model.Setting;
import com.group01.aurora_demo.admin.service.SettingService;
import com.group01.aurora_demo.common.util.PaginatedResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for managing settings (admin only)
 *
 * @author Aurora Team
 */
@WebServlet(name = "SettingManagementServlet", urlPatterns = {"/admin/setting-management"})
public class SettingManagementServlet extends HttpServlet {
    private SettingService settingService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.settingService = new SettingService();
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
                listSettings(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteSetting(request, response);
                break;
            default:
                listSettings(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management");
            return;
        }

        switch (action) {
            case "add":
                addSetting(request, response);
                break;
            case "update":
                updateSetting(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/setting-management");
                break;
        }
    }

    private void listSettings(HttpServletRequest request, HttpServletResponse response)
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
        PaginatedResult<Setting> result = settingService.getSettingsWithPagination(searchTerm, page, pageSize);

        // Set attributes for JSP
        request.setAttribute("settingList", result.getData());
        request.setAttribute("pagination", result.getPagination());
        request.setAttribute("searchTerm", searchTerm != null ? searchTerm : "");
        request.setAttribute("currentPageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/admin/setting-management.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String settingIdStr = request.getParameter("settingId");
        if (settingIdStr == null || settingIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management");
            return;
        }

        try {
            int settingId = Integer.parseInt(settingIdStr);
            Setting setting = settingService.getSettingById(settingId);

            if (setting == null) {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=notfound");
                return;
            }

            request.setAttribute("setting", setting);
            request.setAttribute("mode", "edit");
            List<Setting> settingList = settingService.getAllSettings();
            request.setAttribute("settingList", settingList);
            request.getRequestDispatcher("/WEB-INF/views/admin/setting-management.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=invalid");
        }
    }

    private void addSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String settingKey = request.getParameter("settingKey");
            String settingValue = request.getParameter("settingValue");
            String description = request.getParameter("description");

            if (settingService.addSetting(settingKey, settingValue, description)) {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?success=added");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=add");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=invalid");
        }
    }

    private void updateSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int settingId = Integer.parseInt(request.getParameter("settingId"));
            String settingKey = request.getParameter("settingKey");
            String settingValue = request.getParameter("settingValue");
            String description = request.getParameter("description");

            if (settingService.updateSetting(settingId, settingKey, settingValue, description)) {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?success=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=update");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=invalid");
        }
    }

    private void deleteSetting(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String settingIdStr = request.getParameter("settingId");
        if (settingIdStr == null || settingIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=invalid");
            return;
        }

        try {
            int settingId = Integer.parseInt(settingIdStr);

            if (settingService.deleteSetting(settingId)) {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=delete");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/setting-management?error=invalid");
        }
    }
}

