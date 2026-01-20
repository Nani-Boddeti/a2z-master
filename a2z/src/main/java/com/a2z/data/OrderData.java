package com.a2z.data;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

@Validated
public class OrderData extends RootData {
	private Long id;
	
	private CustomerData customer;
	@Valid
	private PriceData price;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.status.size.too_short}")
	@Size(max = 10, message = "{validation.status.size.too_long}")
	private String status;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.deliveryMode.size.too_short}")
	@Size(max = 10, message = "{validation.deliveryMode.size.too_long}")
	private String deliveryMode;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.namedDeliveryDate.size.too_short}")
	@Size(max = 10, message = "{validation.namedDeliveryDate.size.too_long}")
	private String namedDeliveryDate;
	@Valid
	private AddressData deliveryAddress;
	@Valid
	private AddressData paymentAddress;
	@Valid
	private List<OrderEntryData> entries;
	@Valid
	private PaymentInfoData paymentInfo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CustomerData getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerData customer) {
		this.customer = customer;
	}

	public PriceData getPrice() {
		return price;
	}

	public void setPrice(PriceData price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeliveryMode() {
		return deliveryMode;
	}

	public void setDeliveryMode(String deliveryMode) {
		this.deliveryMode = deliveryMode;
	}

	public String getNamedDeliveryDate() {
		return namedDeliveryDate;
	}

	public void setNamedDeliveryDate(String namedDeliveryDate) {
		this.namedDeliveryDate = namedDeliveryDate;
	}

	public AddressData getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(AddressData deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public AddressData getPaymentAddress() {
		return paymentAddress;
	}

	public void setPaymentAddress(AddressData paymentAddress) {
		this.paymentAddress = paymentAddress;
	}

	public List<OrderEntryData> getEntries() {
		return entries;
	}

	public void setEntries(List<OrderEntryData> entries) {
		this.entries = entries;
	}

	public PaymentInfoData getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(PaymentInfoData paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

}
