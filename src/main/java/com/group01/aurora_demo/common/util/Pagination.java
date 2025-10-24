package com.group01.aurora_demo.common.util;

/**
 * Utility class for pagination support
 *
 * @author Aurora Team
 */
public class Pagination {
    private int currentPage;
    private int pageSize;
    private int totalRecords;
    private int totalPages;
    private int startRecord;
    private int endRecord;

    public Pagination(int currentPage, int pageSize, int totalRecords) {
        this.currentPage = Math.max(1, currentPage);
        this.pageSize = Math.max(1, pageSize);
        this.totalRecords = Math.max(0, totalRecords);
        this.totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Ensure current page is within valid range
        if (this.currentPage > this.totalPages && this.totalPages > 0) {
            this.currentPage = this.totalPages;
        }

        // Calculate start and end record numbers for display
        this.startRecord = totalRecords == 0 ? 0 : (this.currentPage - 1) * pageSize + 1;
        this.endRecord = Math.min(this.currentPage * pageSize, totalRecords);
    }

    /**
     * Get the offset for SQL OFFSET clause
     */
    public int getOffset() {
        return (currentPage - 1) * pageSize;
    }

    /**
     * Get the limit for SQL LIMIT clause
     */
    public int getLimit() {
        return pageSize;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public boolean hasNext() {
        return currentPage < totalPages;
    }

    public int getPreviousPage() {
        return Math.max(1, currentPage - 1);
    }

    public int getNextPage() {
        return Math.min(totalPages, currentPage + 1);
    }

    // Getters
    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getStartRecord() {
        return startRecord;
    }

    public int getEndRecord() {
        return endRecord;
    }
}
