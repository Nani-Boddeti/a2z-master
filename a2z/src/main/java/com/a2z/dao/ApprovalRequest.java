package com.a2z.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class ApprovalRequest extends RootEntity {

	private ApprovalStatus approvalStatus;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "adPost_id", referencedColumnName = "id")
	private AdPost adPost;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private A2zOrder order;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_userName", referencedColumnName = "userName")
	private Customer customer;
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public AdPost getAdPost() {
		return adPost;
	}
	public void setAdPost(AdPost adPost) {
		this.adPost = adPost;
	}
	public A2zOrder getOrder() {
		return order;
	}
	public void setOrder(A2zOrder order) {
		this.order = order;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
