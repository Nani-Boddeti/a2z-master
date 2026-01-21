package com.a2z.dao;

import com.a2z.data.AdPostData;

import java.util.List;

public class CategoryData {
	String categoryCode;
	boolean isVisible;
	String categoryName;
	List<AdPostData> adPostDataList;
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public boolean getIsVisible() {
		return isVisible;
	}
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<AdPostData> getAdPostDataList() {
		return adPostDataList;
	}

	public void setAdPostDataList(List<AdPostData> adPostDataList) {
		this.adPostDataList = adPostDataList;
	}
}
