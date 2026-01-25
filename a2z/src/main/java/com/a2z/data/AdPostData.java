package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Validated
public class AdPostData extends RootData {
	private Long id;
	
	@Pattern(regexp="^[a-zA-Z0-9[ .,@!@#$%^&*+=_-]*]*" )
	@Size(min = 10, message = "{validation.description.size.too_short}")
	@Size(max = 200, message = "{validation.description.size.too_long}")
	private String description;

	@Valid
	private PriceData price;
	
	@Valid
	private MediaContainerData mediaContainerData;
	
	@Valid
	private CustomerData customer;
	
	@Valid
	private AddressData sourceAddress;
	
	private boolean isActive;
	
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.productName.size.too_short}")
	@Size(max = 30, message = "{validation.productName.size.too_long}")
	private String productName;
	private double latitude;
	private double longitude;
	private String categoryCode;
	private boolean selfAdded;
	@NotNull
	private String orderType;
	@Override
	public void setErrorMessage(String errorMessage) {
		// TODO Auto-generated method stub
		super.setErrorMessage(errorMessage);
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public PriceData getPrice() {
		return price;
	}


	public void setPrice(PriceData price) {
		this.price = price;
	}


	public MediaContainerData getMediaContainerData() {
		return mediaContainerData;
	}


	public void setMediaContainerData(MediaContainerData mediaContainerData) {
		this.mediaContainerData = mediaContainerData;
	}


	public CustomerData getCustomer() {
		return customer;
	}


	public void setCustomer(CustomerData customer) {
		this.customer = customer;
	}


	public boolean isActive() {
		return isActive;
	}


	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}

	public AddressData getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(AddressData sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public boolean isSelfAdded() {
		return selfAdded;
	}

	public void setSelfAdded(boolean selfAdded) {
		this.selfAdded = selfAdded;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
}
