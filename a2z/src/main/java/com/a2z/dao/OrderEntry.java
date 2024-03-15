package com.a2z.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;


@Entity
public class OrderEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;
	private int qty;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="basePrice_id")
	private Price basePrice;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="discountedPrice_id")
	private Price discountedPrice;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="totalPrice_id")
	private Price totalPrice;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "adPost_id", referencedColumnName = "id")
	private AdPost adPost;
	@OneToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private Price tax;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="order_id", referencedColumnName="id")
	private A2zOrder order;
	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public Price getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(Price basePrice) {
		this.basePrice = basePrice;
	}
	public Price getDiscountedPrice() {
		return discountedPrice;
	}
	public void setDiscountedPrice(Price discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	public Price getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Price totalPrice) {
		this.totalPrice = totalPrice;
	}
	public AdPost getAdPost() {
		return adPost;
	}
	public void setAdPost(AdPost adPost) {
		this.adPost = adPost;
	}
	public Price getTax() {
		return tax;
	}
	public void setTax(Price tax) {
		this.tax = tax;
	}
	public A2zOrder getOrder() {
		return order;
	}
	public void setOrder(A2zOrder order) {
		this.order = order;
	}
	
}
