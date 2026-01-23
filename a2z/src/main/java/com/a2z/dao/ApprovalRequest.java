package com.a2z.dao;

import com.a2z.enums.ApprovalStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class ApprovalRequest extends RootEntity {

	private ApprovalStatus approvalStatus;
	@ManyToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "adPost_id", referencedColumnName = "id")
	private AdPost adPost;

	@ManyToOne(cascade = {CascadeType.REFRESH})
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
