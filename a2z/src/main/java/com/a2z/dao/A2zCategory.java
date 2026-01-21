package com.a2z.dao;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.UniqueConstraint;

@Entity
public class A2zCategory extends RootEntity {
	@Column(unique = true, nullable = false)
	String categoryCode;
	String categoryName;
	boolean isVisible;
	@ManyToMany(mappedBy = "categories")
	List<AdPost> adPosts;
	
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
	public List<AdPost> getAdPosts() {
		return adPosts;
	}
	public void setAdPosts(List<AdPost> adPosts) {
		this.adPosts = adPosts;
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
}
