package com.a2z.search.dao;


import java.util.List;

public class A2zCategorySearch {
    String categoryCode;
    String categoryName;
    boolean isVisible;
    List<AdPostSearch> adPosts;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public List<AdPostSearch> getAdPosts() {
        return adPosts;
    }

    public void setAdPosts(List<AdPostSearch> adPosts) {
        this.adPosts = adPosts;
    }
}
