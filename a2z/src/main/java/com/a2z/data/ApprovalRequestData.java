package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Validated
public class ApprovalRequestData extends RootData {

	private Long id;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.status.size.too_short}")
	@Size(max = 10, message = "{validation.status.size.too_long}")
	private String status;
	@Valid
	private CustomerData customerData;
	@Valid
	private AdPostData adPostData;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public CustomerData getCustomerData() {
		return customerData;
	}
	public void setCustomerData(CustomerData customerData) {
		this.customerData = customerData;
	}
	public AdPostData getAdPostData() {
		return adPostData;
	}
	public void setAdPostData(AdPostData adPostData) {
		this.adPostData = adPostData;
	}
}
