package com.a2z.dao;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class A2zWishlist extends RootEntity{

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "a2zWishlist_id", referencedColumnName = "id")
	private List<AdPost> ads;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "customer_userName", referencedColumnName = "userName")
	private Customer customer;

	public List<AdPost> getAds() {
		return ads;
	}

	public void setAds(List<AdPost> ads) {
		this.ads = ads;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}
