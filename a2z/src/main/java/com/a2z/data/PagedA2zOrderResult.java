package com.a2z.data;

import java.util.List;

public class PagedA2zOrderResult {
    private List<OrderData> a2zOrders;
    private int currentPage;
    private int totalPages;


    public List<OrderData> getA2zOrders() {
        return a2zOrders;
    }

    public void setA2zOrders(List<OrderData> a2zOrders) {
        this.a2zOrders = a2zOrders;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
