package com.a2z.dao;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
public class A2zOrder  extends RootEntity{

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_userName", referencedColumnName = "userName")
	private Customer customer;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "price_id", referencedColumnName = "id")
	private Price price;
	@Enumerated(EnumType.ORDINAL)
	private OrderStatus status;
	@Enumerated(EnumType.ORDINAL)
	private DeliveryMode deliveryMode;

	private String namedDeliveryDate;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "a2zAddress_id", referencedColumnName = "id")
	private A2zAddress deliveryAddress;

	
	@OneToMany(mappedBy="order",cascade = CascadeType.ALL , fetch = FetchType.EAGER)
	private List<OrderEntry> entries;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "paymentInfo_id", referencedColumnName = "id")
	private PaymentInfo paymentInfo;
	
	@OneToOne(mappedBy="order")
	private ApprovalRequest approvalRequest;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "a2zOrder_id", referencedColumnName = "id")
	private A2zOrder originalVersion ;
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Price getPrice() {
		return price;
	}
	public void setPrice(Price price) {
		this.price = price;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public DeliveryMode getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(DeliveryMode deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public String getNamedDeliveryDate() {
		return namedDeliveryDate;
	}
	public void setNamedDeliveryDate(String namedDeliveryDate) {
		this.namedDeliveryDate = namedDeliveryDate;
	}
	public A2zAddress getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(A2zAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public List<OrderEntry> getEntries() {
		return entries;
	}
	public void setEntries(List<OrderEntry> entries) {
		this.entries = entries;
	}
	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	public ApprovalRequest getApprovalRequest() {
		return approvalRequest;
	}
	public void setApprovalRequest(ApprovalRequest approvalRequest) {
		this.approvalRequest = approvalRequest;
	}
	public A2zOrder getOriginalVersion() {
		return originalVersion;
	}
	public void setOriginalVersion(A2zOrder originalVersion) {
		this.originalVersion = originalVersion;
	}
}
