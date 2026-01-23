package com.a2z.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


@Entity
public class OrderEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long code;
	private int qty;
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="basePrice_id")
	private Price basePrice;
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="discountedPrice_id")
	private Price discountedPrice;
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="totalPrice_id")
	private Price totalPrice;
	@ManyToOne
	@JoinColumn(name = "ad_post_id", nullable = true)
	@JsonIgnoreProperties("orderEntries")  // Prevent recursion
	private AdPost adPost;
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@PrimaryKeyJoinColumn
	private Price tax;
	@ManyToOne(cascade={CascadeType.REFRESH})
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
