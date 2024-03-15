package com.a2z.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


@Entity
public class PaymentInfo extends RootEntity {
	
	private String paymentCode;
	

	private String paymentType;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "a2zAddress_id", referencedColumnName = "id")
	private A2zAddress paymentAddress;
	
	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}

	public A2zAddress getPaymentAddress() {
		return paymentAddress;
	}

	public void setPaymentAddress(A2zAddress paymentAddress) {
		this.paymentAddress = paymentAddress;
	}



}
