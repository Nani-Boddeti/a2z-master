package com.a2z.data;

import java.util.List;

public class PagedAdPostResult {
    private List<AdPostData> adPosts;
    private int currentPage;
    private int totalPages;

    public List<AdPostData> getAdPosts() {
        return adPosts;
    }

    public void setAdPosts(List<AdPostData> adPosts) {
        this.adPosts = adPosts;
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
