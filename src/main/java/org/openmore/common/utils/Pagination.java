package org.openmore.common.utils;

/**
 * Created by michaeltang on 2018/3/23.
 */
public class Pagination {
    /**
     * 当前页码
     */
    private long page;
    /**
     * 每页多少条
     */
    private long limit;
    /**
     * 共计多少条
     */
    private long totalItem;
    /**
     * 共计多少页
     */
    private long totalPage;
    public Pagination(long page, long limit, long totalItem, long totalPage){
        this.page = page;
        this.limit = limit;
        this.totalItem = totalItem;
        this.totalPage = totalPage;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(long totalItem) {
        this.totalItem = totalItem;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }
}
