package com.group01.aurora_demo.admin.service;

import com.group01.aurora_demo.admin.dao.VATDAO;
import com.group01.aurora_demo.admin.model.VAT;
import com.group01.aurora_demo.common.util.Pagination;
import com.group01.aurora_demo.common.util.PaginatedResult;

import java.util.List;

/**
 * Service class for VAT business logic
 *
 * @author Aurora Team
 */
public class VATService {
    private final VATDAO vatDAO;

    public VATService() {
        this.vatDAO = new VATDAO();
    }

    /**
     * Get all VAT records
     */
    public List<VAT> getAllVAT() {
        return vatDAO.getAllVAT();
    }

    /**
     * Get VAT by code
     */
    public VAT getVATByCode(String vatCode) {
        return vatDAO.getVATByCode(vatCode);
    }

    /**
     * Calculate VAT amount based on price and product ID
     */
    public double calculateVATAmount(double price, long productId) {
        double vatRate = vatDAO.getVATRateByProductId(productId);
        return price * (vatRate / 100.0);
    }

    /**
     * Calculate price including VAT
     */
    public double calculatePriceWithVAT(double price, long productId) {
        double vatAmount = calculateVATAmount(price, productId);
        return price + vatAmount;
    }

    /**
     * Get VAT rate by product ID
     */
    public double getVATRateByProductId(long productId) {
        return vatDAO.getVATRateByProductId(productId);
    }

    /**
     * Add new VAT
     */
    public boolean addVAT(String vatCode, double vatRate, String description) {
        // Validate input
        if (vatCode == null || vatCode.trim().isEmpty()) {
            return false;
        }
        if (vatRate < 0 || vatRate > 100) {
            return false;
        }
        if (vatDAO.vatCodeExists(vatCode)) {
            return false;
        }

        VAT vat = new VAT(vatCode, vatRate, description);
        return vatDAO.addVAT(vat);
    }

    /**
     * Update VAT
     */
    public boolean updateVAT(String vatCode, double vatRate, String description) {
        // Validate input
        if (vatCode == null || vatCode.trim().isEmpty()) {
            return false;
        }
        if (vatRate < 0 || vatRate > 100) {
            return false;
        }
        if (!vatDAO.vatCodeExists(vatCode)) {
            return false;
        }

        VAT vat = new VAT(vatCode, vatRate, description);
        return vatDAO.updateVAT(vat);
    }

    /**
     * Delete VAT
     */
    public boolean deleteVAT(String vatCode) {
        if (vatCode == null || vatCode.trim().isEmpty()) {
            return false;
        }
        if (!vatDAO.vatCodeExists(vatCode)) {
            return false;
        }
        if (vatDAO.isVATInUse(vatCode)) {
            return false; // Cannot delete VAT that is in use
        }

        return vatDAO.deleteVAT(vatCode);
    }

    /**
     * Check if VAT is in use
     */
    public boolean isVATInUse(String vatCode) {
        return vatDAO.isVATInUse(vatCode);
    }

    /**
     * Get VAT records with pagination and filtering
     */
    public PaginatedResult<VAT> getVATWithPagination(String searchTerm, int page, int pageSize) {
        // Get total count with filters
        int totalRecords = vatDAO.getVATCount(searchTerm);

        // Create pagination object
        Pagination pagination = new Pagination(page, pageSize, totalRecords);

        // Get paginated data
        List<VAT> vatList = vatDAO.getVATWithPagination(searchTerm, pagination.getOffset(), pagination.getLimit());

        return new PaginatedResult<>(vatList, pagination);
    }
}
