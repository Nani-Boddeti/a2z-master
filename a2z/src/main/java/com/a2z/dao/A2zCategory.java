package com.a2z.dao;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

@Entity
public class A2zCategory extends RootEntity {

	String categoryCode;
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

}
