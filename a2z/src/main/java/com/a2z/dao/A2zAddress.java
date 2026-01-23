package com.a2z.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class A2zAddress extends RootEntity{

	private boolean isDeliveryAddress;
	private boolean isDefaultAddress;
	private boolean isPaymentAddress;
	private String firstName;
	private String lastName;
	private String line1;
	private String line2;
	private String apartment;
	private String building;
	private String cellphone;
	private String company;
	@OneToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "country_isoCode", referencedColumnName = "isoCode")
	private Country country;
	private String district;
	private String email;
	@ManyToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "customer_userName", referencedColumnName = "userName")
	private Customer customer;

	@OneToOne(mappedBy="paymentAddress")
	private PaymentInfo paymentInfo;
		
	private double latitude;
	private double longitude;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLine1() {
		return line1;
	}
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	public String getLine2() {
		return line2;
	}
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	public String getApartment() {
		return apartment;
	}
	public void setApartment(String apartment) {
		this.apartment = apartment;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getCellphone() {
		return cellphone;
	}
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public boolean isDeliveryAddress() {
		return isDeliveryAddress;
	}
	public void setDeliveryAddress(boolean isDeliveryAddress) {
		this.isDeliveryAddress = isDeliveryAddress;
	}
	public boolean isPaymentAddress() {
		return isPaymentAddress;
	}
	public void setPaymentAddress(boolean isPaymentAddress) {
		this.isPaymentAddress = isPaymentAddress;
	}
	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(PaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
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
	public boolean isDefaultAddress() {
		return isDefaultAddress;
	}

	public void setDefaultAddress(boolean defaultAddress) {
		isDefaultAddress = defaultAddress;
	}

}
