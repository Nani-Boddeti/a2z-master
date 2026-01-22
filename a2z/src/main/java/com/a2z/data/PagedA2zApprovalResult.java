package com.a2z.data;

import java.util.List;

public class PagedA2zApprovalResult {
    private List<ApprovalRequestData> approvalRequestDataList;
    private int currentPage;
    private int totalPages;

    public List<ApprovalRequestData> getApprovalRequestDataList() {
        return approvalRequestDataList;
    }

    public void setApprovalRequestDataList(List<ApprovalRequestData> approvalRequestDataList) {
        this.approvalRequestDataList = approvalRequestDataList;
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
