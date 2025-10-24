package com.group01.aurora_demo.common.util;

import java.util.List;

/**
 * Generic wrapper for paginated results
 *
 * @author Aurora Team
 */
public class PaginatedResult<T> {
    private List<T> data;
    private Pagination pagination;

    public PaginatedResult(List<T> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
