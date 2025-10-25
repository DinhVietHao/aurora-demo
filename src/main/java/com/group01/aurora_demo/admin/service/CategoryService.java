package com.group01.aurora_demo.admin.service;

import com.group01.aurora_demo.admin.dao.CategoryDAO;
import com.group01.aurora_demo.admin.model.Category;
import com.group01.aurora_demo.admin.dao.VATDAO;
import com.group01.aurora_demo.common.util.Pagination;
import com.group01.aurora_demo.common.util.PaginatedResult;

import java.util.List;

/**
 * Service class for Category business logic
 *
 * @author Aurora Team
 */
public class CategoryService {
    private final CategoryDAO categoryDAO;
    private final VATDAO vatDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
        this.vatDAO = new VATDAO();
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(long categoryId) {
        return categoryDAO.getCategoryById(categoryId);
    }

    /**
     * Add new category
     */
    public boolean addCategory(String name, String vatCode) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (vatCode == null || vatCode.trim().isEmpty()) {
            return false;
        }

        // Check if category name already exists
        if (categoryDAO.categoryNameExists(name.trim())) {
            return false;
        }

        // Check if VAT code exists
        if (!vatDAO.vatCodeExists(vatCode)) {
            return false;
        }

        Category category = new Category();
        category.setName(name.trim());
        category.setVatCode(vatCode);

        return categoryDAO.addCategory(category);
    }

    /**
     * Update category
     */
    public boolean updateCategory(long categoryId, String name, String vatCode) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (vatCode == null || vatCode.trim().isEmpty()) {
            return false;
        }

        // Check if category exists
        Category existingCategory = categoryDAO.getCategoryById(categoryId);
        if (existingCategory == null) {
            return false;
        }

        // Check if new name conflicts with another category
        if (categoryDAO.categoryNameExistsExcludingId(name.trim(), categoryId)) {
            return false;
        }

        // Check if VAT code exists
        if (!vatDAO.vatCodeExists(vatCode)) {
            return false;
        }

        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName(name.trim());
        category.setVatCode(vatCode);

        return categoryDAO.updateCategory(category);
    }

    /**
     * Delete category
     */
    public boolean deleteCategory(long categoryId) {
        // Check if category exists
        Category category = categoryDAO.getCategoryById(categoryId);
        if (category == null) {
            return false;
        }

        // Check if category is in use by any product
        if (categoryDAO.isCategoryInUse(categoryId)) {
            return false; // Cannot delete category that is in use
        }

        return categoryDAO.deleteCategory(categoryId);
    }

    /**
     * Check if category is in use
     */
    public boolean isCategoryInUse(long categoryId) {
        return categoryDAO.isCategoryInUse(categoryId);
    }

    /**
     * Check if category name exists
     */
    public boolean categoryNameExists(String name) {
        return categoryDAO.categoryNameExists(name);
    }

    /**
     * Get categories with pagination and filtering
     */
    public PaginatedResult<Category> getCategoriesWithPagination(String searchTerm, String vatCodeFilter, int page, int pageSize) {
        // Get total count with filters
        int totalRecords = categoryDAO.getCategoryCount(searchTerm, vatCodeFilter);

        // Create pagination object
        Pagination pagination = new Pagination(page, pageSize, totalRecords);

        // Get paginated data
        List<Category> categories = categoryDAO.getCategoriesWithPagination(searchTerm, vatCodeFilter, pagination.getOffset(), pagination.getLimit());

        return new PaginatedResult<>(categories, pagination);
    }
}
